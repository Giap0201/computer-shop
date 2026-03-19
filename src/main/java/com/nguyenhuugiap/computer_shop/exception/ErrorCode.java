package com.nguyenhuugiap.computer_shop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // --- SYSTEM & COMMON (1XXX) ---
    UNCATEGORIZED_EXCEPTION(1001, "Lỗi hệ thống không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1002, "Mã lỗi không hợp lệ", HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED(1003, "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),

    // --- FILES (11xx) ---
    INVALID_FILE_LOCATION(1100, "Cấu hình thư mục lưu trữ không hợp lệ", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_IS_EMPTY(1101, "File không được để trống", HttpStatus.BAD_REQUEST),
    FILE_NAME_IS_EMPTY(1102, "Tên file không được để trống", HttpStatus.BAD_REQUEST),
    CANNOT_STORE_FILE(1103, "Lỗi hệ thống: Không thể lưu file", HttpStatus.INTERNAL_SERVER_ERROR),
    CANNOT_CREATE_DIR(1104, "Lỗi hệ thống: Không thể tạo thư mục", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_TOO_LARGE(1105, "File quá lớn (Tối đa 10MB)", HttpStatus.PAYLOAD_TOO_LARGE),
    FILE_NOT_FOUND(1106, "Không tìm thấy file hoặc đường dẫn sai", HttpStatus.NOT_FOUND),
    INVALID_FILE_PATH(1107, "Tên file chứa kí tự không hợp lệ!", HttpStatus.BAD_REQUEST),
    FILE_EXTENSION_NOT_SUPPORTED(1108, "Định dạng file không được hỗ trợ", HttpStatus.BAD_REQUEST),
    // --- AUTH (2XXX) ---
    UNAUTHENTICATED(2001, "Bạn chưa đăng nhập", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(2002, "Bạn không có quyền truy cập", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS(2003, "Tên đăng nhập hoặc mật khẩu sai", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(2004, "Hệ thống đang gặp sự cố, vui lòng thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),

    // --- USER (3XXX) ---
    USER_EXISTS(3001, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(3002, "Người dùng không tồn tại", HttpStatus.NOT_FOUND), // Chuẩn 404
    USERNAME_INVALID(3003, "Tên đăng nhập phải từ 3 ký tự trở lên", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(3004, "Mật khẩu phải có ít nhất 8 ký tự", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(3005, "Email không đúng định dạng", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(3006, "Số điện thoại không đúng định dạng", HttpStatus.BAD_REQUEST),
    GENDER_INVALID(3007, "Giới tính không đúng định dạng (MALE, FEMALE, OTHER)", HttpStatus.BAD_REQUEST),
    // --- ROLE (35XX) ---
    ROLE_NOT_FOUND(3501, "Vai trò (Role) không tồn tại", HttpStatus.NOT_FOUND),

    // --- BRAND (40XX) ---
    BRAND_EXISTS(4001, "Thương hiệu này đã tồn tại", HttpStatus.BAD_REQUEST),
    BRAND_NAME_REQUIRED(4002, "Tên thương hiệu bắt buộc nhập", HttpStatus.BAD_REQUEST),
    BRAND_NAME_INVALID(4003, "Tên thương hiệu không hợp lệ", HttpStatus.BAD_REQUEST),
    BRAND_NOT_FOUND(4004, "Thương hiệu không tồn tại", HttpStatus.NOT_FOUND),
    BRAND_NAME_EXISTS(4005, "Tên thương hiệu đã được sử dụng", HttpStatus.BAD_REQUEST),

    // --- PRODUCT (41XX)
    PRODUCT_EXISTS(4100, "Sản phầm này đã tồn tại", HttpStatus.BAD_REQUEST),
    PRODUCT_NAME_REQUIRED(4101, "Tên sản phẩm bắt buộc nhập", HttpStatus.BAD_REQUEST),
    PRODUCT_NAME_INVALID(4102, "Tên sản phẩm không đúng định dạng", HttpStatus.BAD_REQUEST),
    DESCRIPTION_INVALID(4103, "Mô tả không đúng định dạng", HttpStatus.BAD_REQUEST),
    CATEGORY_REQUIRED(4104, "Danh mục không hợp lệ", HttpStatus.BAD_REQUEST),
    BRAND_REQUIRED(4105, "Thương hiệu không hợp lệ", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(4106, "Không tồn tại sản phẩm", HttpStatus.NOT_FOUND),

    // --- PRODUCT_VARIANTS_ATTRIBUTES(42XX)
    SKU_CODE_INVALID(4200, "Mã sku không hợp lệ", HttpStatus.BAD_REQUEST),
    PRICE_REQUIRED(4201, "Giá không được bỏ trống", HttpStatus.BAD_REQUEST),
    PRICE_INVALID(4202, "Giá tiền không hợp lệ", HttpStatus.BAD_REQUEST),
    STOCK_REQUIRED(4203, "Tồn kho không được bỏ trống", HttpStatus.BAD_REQUEST),
    STOCK_INVALID(4204, "Tồn kho không hợp lệ", HttpStatus.BAD_REQUEST),
    NAME_ATTRIBUTE_REQUIRED(4205, "Tên thuộc tính không được bỏ trống", HttpStatus.BAD_REQUEST),
    VALUE_ATTRIBUTE_REQUIRED(4206, "Giá trị thuộc tính không được bỏ trống", HttpStatus.BAD_REQUEST),
    VARIANT_ATTRIBUTE_VALUE_REQUIRED(4207, "Danh sách thuộc tính không đuược bỏ trống", HttpStatus.BAD_REQUEST),
    ATTRIBUTE_ID_REQUIRED(4208, "Vui lòng thêm tên thuộc tính", HttpStatus.BAD_REQUEST),
    ATTRIBUTE_EXISTS(4209, "Thuộc tính đã tồn tại", HttpStatus.BAD_REQUEST),
    ATTRIBUTE_NOT_FOUND(4210, "Thuộc tính không tồn tại", HttpStatus.NOT_FOUND),
    ATTRIBUTE_IN_USE(4211, "Thuộc tính đang được sử dụng", HttpStatus.INTERNAL_SERVER_ERROR),
    SKU_CODE_EXISTS(4212, "Mã sku code đã tồn tại", HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_VARIANT_NOT_FOUND(4213, "Biến thể không tồn tại", HttpStatus.NOT_FOUND),
    VERSION_MISMATCH(4214, "Dữ liệu đã bị thay đổi bởi người khác. Vui lòng tải lại.", HttpStatus.CONFLICT),
    VERSION_REQUIRED(4215, "Version không được bỏ qua", HttpStatus.BAD_REQUEST),

    // --- IMAGE(43XX)
    IMAGE_REQUIRED(4300, "Vui lòng thêm ảnh", HttpStatus.BAD_REQUEST),
    TOO_MANY_IMAGES(4301, "Không thể upload hơn 10 ảnh một lúc", HttpStatus.BAD_REQUEST),
    IMAGE_NOT_FOUND(4302, "Ảnh không tồn tại", HttpStatus.NOT_FOUND),

    // ---CART(44xx)
    PRODUCT_VARIANT_REQUIRED(4401, "Vui lòng thêm 1 sản phẩm", HttpStatus.BAD_REQUEST),
    QUANTITY_REQUIRED(4402, "Vui lòng thêm số lượng", HttpStatus.BAD_REQUEST),
    QUANTITY_INVALID(4403, "Số lượng trong giỏ phải lớn hơn 0", HttpStatus.BAD_REQUEST),


    // --- CATEGORY (45XX) ---
    CATEGORY_NAME_REQUIRED(4501, "Tên danh mục bắt buộc nhập", HttpStatus.BAD_REQUEST),
    CATEGORY_TOO_LONG(4502, "Tên danh mục tối đa 100 kí tự", HttpStatus.BAD_REQUEST),
    CATEGORY_EXISTS(4503, "Danh mục này đã tồn tại", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(4504, "Danh mục không tồn tại", HttpStatus.NOT_FOUND),
    CANNOT_DELETE_HAS_CHILDREN(4505, "Không thể xoá danh mục đang chứa danh mục con", HttpStatus.BAD_REQUEST),
    CANNOT_UPDATE_CATEGORY(4506, "Không thể cập nhật danh mục này", HttpStatus.BAD_REQUEST),
    ;


    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;
}