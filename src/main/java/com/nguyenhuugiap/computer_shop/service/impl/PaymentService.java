package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.configuration.VNPayConfig;
import com.nguyenhuugiap.computer_shop.entity.Order;
import com.nguyenhuugiap.computer_shop.entity.OrderStatusHistory;
import com.nguyenhuugiap.computer_shop.entity.PaymentTransaction;
import com.nguyenhuugiap.computer_shop.enums.OrderStatus;
import com.nguyenhuugiap.computer_shop.enums.PaymentMethod;
import com.nguyenhuugiap.computer_shop.enums.PaymentStatus;
import com.nguyenhuugiap.computer_shop.enums.TransactionStatus;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
import com.nguyenhuugiap.computer_shop.repository.PaymentTransactionRepository;
import com.nguyenhuugiap.computer_shop.service.interfaces.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class PaymentService {
    // Order service to handle business to logic related to order
    private final OrderService orderService;

    // Repository for PaymentTransaction entity
    private final PaymentTransactionRepository paymentTransactionRepository;

    // VNPay merchant code
    @Value("${vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${vnpay.hash-secret}")
    private String secretKey;

    @Value("${vnpay.url}")
    private String vnpPayUrl;

    @Value("${vnpay.return-url}")
    private String vnpReturnUrl;

    @Value("${vnpay.api-url}")
    private String vnpApiUrl;

    // Create VNPay payment URL for a given order
    public String createPaymentUrl(String orderCode, HttpServletRequest request) {
        // Validate order state
        Order order = orderService.getOrderEntityByCode(orderCode);
        validateOrderForPayment(order);

        // Cancel all order has payment status pending old
        paymentTransactionRepository.expireOldPendingTransactions(order.getId());

        // Create code tnxRef
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String vnp_TxnRef = order.getOrderCode() + "-" + uniqueSuffix;
        PaymentTransaction transaction = PaymentTransaction.builder()
                .paymentMethod(PaymentMethod.VNPAY)
                .transactionRef(vnp_TxnRef)
                .amount(order.getFinalAmount())
                .status(TransactionStatus.PENDING)
                .order(order)
                .build();
        paymentTransactionRepository.save(transaction);

        return buildVNPayUrl(order, vnp_TxnRef, request);
    }

    private String buildVNPayUrl(Order order, String vnp_TxnRef, HttpServletRequest request) {
        long amount = order.getFinalAmount().longValue() * 100L;
        String vnp_IpAddr = VNPayConfig.getIpAddress(request);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnpTmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef); // Dùng mã mới
        vnp_Params.put("vnp_OrderInfo", "Thanh_toan_Order_CODE_" + order.getOrderCode());
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnpReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // Xử lý định dạng ngày tháng
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Ngày hết hạn (15p)
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Build chuỗi dữ liệu
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return vnpPayUrl + "?" + queryUrl;
    }

    // Validate if order eligible for payment
    private void validateOrderForPayment(Order order) {
        if (order.getPaymentStatus() == PaymentStatus.PAID ||
                order.getStatus() == OrderStatus.CONFIRMED ||
                order.getStatus() == OrderStatus.COMPLETED) {
            throw new AppException(ErrorCode.ORDER_ALREADY_PAID);
        }
    }

    public Map<String, String> processIpn(HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            // 1. Gom tham số
            Map<String, String> fields = new HashMap<>();
            for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
                String fieldName = params.nextElement();
                String fieldValue = request.getParameter(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    fields.put(fieldName, fieldValue);
                }
            }

            String vnp_SecureHash = request.getParameter("vnp_SecureHash");

            fields.remove("vnp_SecureHashType");
            fields.remove("vnp_SecureHash");
            String signValue = VNPayConfig.hashAllFields(fields, secretKey);
            if (!signValue.equals(vnp_SecureHash)) {
                response.put("RspCode", "97");
                response.put("Message", "Invalid Checksum");
                return response;
            }

            String vnpTxnRef = request.getParameter("vnp_TxnRef");
            String vnpAmount = request.getParameter("vnp_Amount");

            // 1. TÌM GIAO DỊCH BẰNG VNP_TXNREF
            PaymentTransaction transaction = paymentTransactionRepository.findByTransactionRefForUpdate(vnpTxnRef)
                    .orElse(null);

            if (transaction == null) {
                response.put("RspCode", "01");
                response.put("Message", "Order not found");
                return response;
            }
            Order order = transaction.getOrder();

            // Check money valid
            long expectedAmount = transaction.getAmount().longValue() * 100L;
            if (expectedAmount != Long.parseLong(vnpAmount)) {
                response.put("RspCode", "04");
                response.put("Message", "Invalid Amount");
                return response;
            }

            // 4. Kiểm tra Idempotency (Giao dịch này đã xử lý chưa?)
            if (transaction.getStatus() != TransactionStatus.PENDING) {
                response.put("RspCode", "02");
                response.put("Message", "Order already confirmed");
                return response;
            }

            String responseCode = request.getParameter("vnp_ResponseCode");
            String transactionNo = request.getParameter("vnp_TransactionNo");

            // 5. CẬP NHẬT TRANSACTION
            transaction.setTransactionCode(transactionNo); // Lưu mã đối soát của VNPAY
            transaction.setProviderResponse(request.getQueryString()); // Lưu lại toàn bộ URL phòng rủi ro

            if ("00".equals(responseCode)) {
                transaction.setStatus(TransactionStatus.SUCCESS);
                paymentTransactionRepository.save(transaction);
                // 6. CẬP NHẬT ĐƠN HÀNG
                processOrderStatusConfirm(order);

                response.put("RspCode", "00");
                response.put("Message", "Confirm Success");
            } else {
                transaction.setStatus(TransactionStatus.FAILED);
                paymentTransactionRepository.save(transaction);

                response.put("RspCode", "00");
                response.put("Message", "Confirm Success");
            }

            return response;

        } catch (Exception e) {
            log.error("IPN Process Error: ", e);
            // Spring rollback when error
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response.put("RspCode", "99");
            response.put("Message", "Unknown error");
            return response;
        }
    }

    private void processOrderStatusConfirm(Order order) {
        orderService.updatePaymentStatus(order.getId(), PaymentStatus.PAID);
        OrderStatusHistory history = OrderStatusHistory.builder()
                .status(order.getStatus())
                .note("System: Nhận thanh toán thành công qua VNPAY. Chờ xác nhận đơn hàng.")
                .build();
        order.addStatusHistory(history);

    }

    public String processReturn(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");
        String signValue = VNPayConfig.hashAllFields(fields, secretKey);
        String txnRef = request.getParameter("vnp_TxnRef");

        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                return "http://localhost:3000/payment-success?" + request.getQueryString();
            } else {
                // Thất bại
                return "http://localhost:3000/payment-failed?pnr=" + txnRef;
            }
        } else {
            return "http://localhost:3000/payment-error?message=invalid-signature";
        }
    }

    public TransactionStatus queryTransaction(PaymentTransaction txn) {
        Order order = txn.getOrder();
        String vnp_RequestId = UUID.randomUUID().toString();
        String vnp_Version = "2.1.0";
        String vnp_Command = "querydr";
        String vnp_TxnRef = txn.getTransactionRef();
        String vnp_OrderInfo = "Query transaction " + vnp_TxnRef;
        String vnp_IpAddr = "127.0.0.1";

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        // Lấy thời gian tạo Transaction, không lấy thời gian tạo Order
        Date txnDate = Date.from(txn.getCreatedAt().atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
        String vnp_TransactionDate = formatter.format(txnDate);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String vnp_CreateDate = formatter.format(cld.getTime());

        String hashData = String.join("|",
                vnp_RequestId, vnp_Version, vnp_Command, vnpTmnCode,
                vnp_TxnRef, vnp_TransactionDate, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

        String vnp_SecureHash = VNPayConfig.hmacSHA512(secretKey, hashData);

        Map<String, String> requestData = new HashMap<>();
        requestData.put("vnp_RequestId", vnp_RequestId);
        requestData.put("vnp_Version", vnp_Version);
        requestData.put("vnp_Command", vnp_Command);
        requestData.put("vnp_TmnCode", vnpTmnCode);
        requestData.put("vnp_TxnRef", vnp_TxnRef);
        requestData.put("vnp_OrderInfo", vnp_OrderInfo);
        requestData.put("vnp_TransactionDate", vnp_TransactionDate);
        requestData.put("vnp_CreateDate", vnp_CreateDate);
        requestData.put("vnp_IpAddr", vnp_IpAddr);
        requestData.put("vnp_SecureHash", vnp_SecureHash);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestData, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(vnpApiUrl, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null) {
                String responseCode = (String) responseBody.get("vnp_ResponseCode");
                String transactionStatus = (String) responseBody.get("vnp_TransactionStatus");

                // Cập nhật lại provider response để lưu vết log
                txn.setProviderResponse(responseBody.toString());

                if ("00".equals(responseCode)) {
                    String vnpTransactionNo = (String) responseBody.get("vnp_TransactionNo");
                    txn.setTransactionCode(vnpTransactionNo); // Lưu mã đối soát

                    if ("00".equals(transactionStatus)) {
                        // VNPAY BÁO THÀNH CÔNG
                        if (txn.getStatus() != TransactionStatus.SUCCESS) {
                            txn.setStatus(TransactionStatus.SUCCESS);
                            paymentTransactionRepository.save(txn);

                            // Chốt đơn hàng nếu chưa chốt
                            if (order.getStatus() == OrderStatus.PENDING) {
                                processOrderStatusConfirm(order);
                                log.info("JOB ĐỐI SOÁT ĐÃ CỨU HỘ THÀNH CÔNG ĐƠN HÀNG: {}", order.getOrderCode());
                            }
                        }
                        return TransactionStatus.SUCCESS;
                    } else {
                        // VNPAY BÁO THẤT BẠI (Khách hủy, hụt tiền...)
                        log.warn("VNPAY báo GD thất bại cho Đơn {}. Lỗi: {}", order.getOrderCode(), transactionStatus);
                        txn.setStatus(TransactionStatus.FAILED);
                        paymentTransactionRepository.save(txn);
                        return TransactionStatus.FAILED;
                    }
                } else if ("91".equals(responseCode)) {
                    log.info("Khách hàng chưa thanh toán cho Đơn: {}", order.getOrderCode());
                    return TransactionStatus.PENDING;
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi gọi API Đối soát VNPAY cho Đơn: {}", order.getOrderCode(), e);
        }
        return TransactionStatus.PENDING;
    }

    public Map<String, String> verifyPaymentStatusImmediately(String orderCode) {
        Order order = orderService.getOrderEntityByCode(orderCode);

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            return Map.of("status", "SUCCESS", "message", "Thanh toán thành công. Đơn hàng đang chờ xử lý.");
        }

        if (order.getStatus() == OrderStatus.PENDING) {
            List<PaymentTransaction> pendingTxns = paymentTransactionRepository
                    .findByOrderIdAndStatusOrderByCreatedAtDesc(order.getId(), TransactionStatus.PENDING);

            // Lấy cái giao dịch PENDING đầu tiên (mới nhất) nếu danh sách không rỗng
            PaymentTransaction activeTxn = pendingTxns.isEmpty() ? null : pendingTxns.get(0);

            if (activeTxn != null) {
                TransactionStatus actualStatus = queryTransaction(activeTxn);

                if (actualStatus == TransactionStatus.SUCCESS) {
                    return Map.of("status", "SUCCESS", "message", "Đã cập nhật trạng thái thanh toán thành công!");
                } else if (actualStatus == TransactionStatus.FAILED) {
                    return Map.of("status", "FAILED", "message", "Giao dịch thất bại tại VNPAY.");
                }
            }
        }

        return Map.of("status", "PENDING", "message", "Đang chờ khách hàng thanh toán...");
    }
}