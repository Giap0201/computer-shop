<h1 align="center">Computer Shop API</h1>

<p align="center">
  RESTful API cho hệ thống bán máy tính, linh kiện và thiết bị công nghệ
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-blue?logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen?logo=springboot&logoColor=white" alt="Spring Boot 3.5.9" />
  <img src="https://img.shields.io/badge/MySQL-8.0-orange?logo=mysql&logoColor=white" alt="MySQL 8.0" />
  <img src="https://img.shields.io/badge/Redis-7.x-red?logo=redis&logoColor=white" alt="Redis 7.x" />
  <img src="https://img.shields.io/badge/OAuth2-Google-4285F4?logo=google&logoColor=white" alt="Google OAuth2" />
  <img src="https://img.shields.io/badge/OpenAPI-Swagger-85EA2D?logo=swagger&logoColor=black" alt="Swagger" />
</p>

---

## Giới thiệu

**Computer Shop API** là dự án backend cho website thương mại điện tử chuyên bán máy tính, linh kiện và thiết bị công nghệ. Hệ thống cung cấp các API phục vụ xác thực người dùng, quản lý sản phẩm, giỏ hàng, đơn hàng, thanh toán và quản trị.

Dự án được xây dựng bằng **Spring Boot** theo kiến trúc phân lớp, kết hợp **MySQL** để lưu trữ dữ liệu, **Redis** để cache và hỗ trợ quản lý token, đồng thời tích hợp **Google OAuth2**, **VNPay Sandbox** và gửi email thông báo.

## Công nghệ sử dụng

| Nhóm | Công nghệ |
| --- | --- |
| Ngôn ngữ | Java 21 |
| Framework | Spring Boot 3.5.9 |
| Bảo mật | Spring Security, JWT, Google OAuth2 |
| Cơ sở dữ liệu | MySQL 8.0, Spring Data JPA, Hibernate |
| Cache | Redis, Spring Cache |
| Mapping & Validation | MapStruct, Jakarta Bean Validation, Lombok |
| Tài liệu API | Springdoc OpenAPI, Swagger UI |
| Tích hợp | VNPay Sandbox, Brevo SMTP, Google OAuth2 |
| Xử lý nền | Spring Events, `@Async`, `@Scheduled` |

## Chức năng chính

### Người dùng và xác thực

- Đăng ký, đăng nhập bằng email và mật khẩu.
- Đăng nhập bằng tài khoản Google thông qua OAuth2.
- Xác thực API bằng Access Token và Refresh Token.
- Đăng xuất và vô hiệu hóa token đã thu hồi.
- Xem và cập nhật thông tin cá nhân.

### Sản phẩm và danh mục

- Xem danh sách, chi tiết sản phẩm, biến thể sản phẩm.
- Tìm kiếm và lọc sản phẩm theo điều kiện.
- Quản lý danh mục, thương hiệu, sản phẩm và hình ảnh sản phẩm dành cho quản trị viên.
- Cache dữ liệu sản phẩm, danh mục và thương hiệu bằng Redis.

### Giỏ hàng và đơn hàng

- Hỗ trợ giỏ hàng cho khách chưa đăng nhập bằng `sessionId`.
- Hỗ trợ giỏ hàng cho người dùng đã đăng nhập.
- Hợp nhất giỏ hàng sau khi đăng nhập.
- Đặt hàng, xem lịch sử mua hàng và theo dõi trạng thái đơn hàng.
- Kiểm tra tồn kho khi tạo đơn, hạn chế tình trạng bán vượt số lượng hiện có.

### Thanh toán và thông báo

- Thanh toán khi nhận hàng (COD).
- Thanh toán trực tuyến qua VNPay Sandbox.
- Tự động xử lý đơn VNPay quá hạn thanh toán.
- Gửi email thông báo sau khi đơn hàng được tạo thành công.

### Quản trị hệ thống

- Quản lý người dùng và phân quyền.
- Quản lý sản phẩm, danh mục, thương hiệu.
- Quản lý đơn hàng và cập nhật trạng thái xử lý.

## Điểm kỹ thuật nổi bật

### 1. Hệ thống xác thực đa tầng

Hệ thống hỗ trợ hai phương thức đăng nhập độc lập nhưng cùng đầu ra là JWT của hệ thống:

- **Email / Mật khẩu** — xác thực thủ công bằng `Nimbus JOSE-JWT` (thuật toán HS512)
- **Google OAuth2** — Authorization Code Flow thông qua `Spring Security OAuth2 Client`

Sau khi Google trả về thông tin người dùng, hệ thống tự tạo JWT của riêng mình (không dùng token của Google) để đảm bảo tính nhất quán với toàn bộ luồng xác thực còn lại.

**Refresh Token Rotation:** Mỗi lần làm mới phiên, refresh token cũ bị vô hiệu hóa và cấp token mới — hạn chế rủi ro nếu token bị lộ.

**JWT Blacklist bằng Redis:**

Thay vì lưu token đã thu hồi vào cơ sở dữ liệu (phải query DB ở mọi request và cần job dọn dẹp định kỳ), hệ thống lưu vào Redis với TTL bằng đúng thời gian còn lại của token. Redis tự xóa khi token hết hạn — không cần thêm tác vụ dọn dẹp.

```
POST /auth/logout
  → Lấy JTI từ token
  → SET "blacklist:{jti}" = "revoked"  EX {giây còn lại}
  → Mọi request tiếp theo đều bị từ chối khi verifyToken() phát hiện key này
```

---

### 2. Kiểm soát tồn kho an toàn khi có nhiều yêu cầu đồng thời

**Vấn đề:** Nhiều người dùng cùng đặt hàng một sản phẩm có tồn kho giới hạn → nguy cơ bán vượt số lượng.

**Giải pháp — Atomic SQL Update:**

```sql
UPDATE product_variants
SET stock_quantity = stock_quantity - :quantity
WHERE id = :variantId
  AND stock_quantity >= :quantity
```

Câu lệnh này chỉ thực hiện khi tồn kho đủ. Nếu trả về 0 dòng bị ảnh hưởng → hệ thống trả lỗi `INSUFFICIENT_STOCK` ngay lập tức, không cần khóa tầng ứng dụng.

Ngoài ra, `ProductVariant` sử dụng **Optimistic Locking** (`@Version`) để phát hiện xung đột ghi đồng thời từ các luồng khác nhau.

---

### 3. Gửi email không đồng bộ, đảm bảo tính nhất quán giao dịch

**Vấn đề:** Nếu gửi email ngay trong transaction tạo đơn hàng → email có thể được gửi dù transaction bị rollback.

**Giải pháp:**

```
createOrder()  →  DB commit thành công
  → ApplicationEventPublisher.publishEvent(OrderPlaceEvent)
    → @TransactionalEventListener(phase = AFTER_COMMIT)
      → @Async  →  EmailService.sendEmail()
                   (chạy trên thread pool riêng, không chặn response)
```

`@TransactionalEventListener(phase = AFTER_COMMIT)` đảm bảo email chỉ gửi sau khi dữ liệu đã được lưu thành công. `@Async` đảm bảo việc gửi email không làm chậm response trả về người dùng.

---

### 4. Quản lý trạng thái đơn hàng

Đơn hàng chuyển trạng thái theo một luồng được kiểm soát chặt chẽ, không cho phép nhảy cóc hoặc quay ngược:

```
PENDING → CONFIRMED → PROCESSING → SHIPPING → DELIVERED → COMPLETED
   │           │             │
   └───────────┴─────────────┴──────────────────────────────► CANCELLED
                                                    SHIPPING / DELIVERED ──► RETURNED
```

- Đơn COD được tự động xác nhận ngay sau khi đặt.
- Đơn VNPay chưa thanh toán sau 15 phút sẽ bị hủy tự động bởi `@Scheduled` job.
- Khi đơn ở trạng thái RETURNED hoặc CANCELLED, tồn kho được hoàn trả lại.

---

### 5. Giỏ hàng cho cả khách và người dùng đã đăng nhập

| Trường hợp | Định danh | Lưu trữ |
|-----------|----------|---------|
| Khách chưa đăng nhập | `sessionId` (Cookie) | Database |
| Người dùng đã đăng nhập | `userId` | Database |
| Sau khi đăng nhập | Hợp nhất giỏ khách vào giỏ user | Tự động |

Khi hợp nhất, số lượng được cộng dồn nhưng bị giới hạn theo tồn kho thực tế.

---

### 6. Redis Cache cho dữ liệu ít thay đổi

| Cache | Phương thức | Điều kiện xóa cache |
|------|------------|-------------------|
| `products` | `getProductById(id)` | Cập nhật / xóa sản phẩm hoặc biến thể |
| `categories` | `getAllCategories()`, `getCategoryById(id)` | Tạo / sửa / xóa danh mục |
| `brands` | `getAllBrands()` | Tạo / sửa / xóa thương hiệu |

TTL mặc định 60 phút, có thể cấu hình lại trong `RedisConfig`.

---

## Kiến trúc dự án

```text
Client (HTTP / REST)
  │
  ▼
Spring Security Filter Chain
  ├── JwtAuthenticationFilter   →  verify JWT + check Redis Blacklist
  └── OAuth2 Login Handler      →  Google Authorization Code Flow
  │
  ▼
Controller Layer   (validate input, route mapping)
  │
  ▼
Service Layer      (business logic, transactions, events)
  ├── Redis Cache        (@Cacheable / @CacheEvict)
  ├── Redis Blacklist    (token revocation)
  ├── Event Publisher    (OrderPlaceEvent → email async)
  └── Scheduled Jobs     (order expiration, cart cleanup)
  │
  ▼
Repository Layer   (JPA / Specifications / custom queries)
  │
  ▼
MySQL Database
```

## Cấu trúc thư mục

```text
src/main/java/com/nguyenhuugiap/computer_shop/
├── configuration/       # Cấu hình Redis, CORS, Async, VNPay, lưu trữ tệp
├── controller/          # REST Controller
│   └── admin/           # API dành cho quản trị viên
├── dto/                 # Dữ liệu request/response
├── entity/              # JPA Entity
├── enums/               # Các kiểu liệt kê của hệ thống
├── event/               # Sự kiện nghiệp vụ
├── exception/           # Xử lý ngoại lệ và mã lỗi
├── job/                 # Tác vụ định kỳ
├── listener/            # Bộ lắng nghe sự kiện
├── mapper/              # MapStruct Mapper
├── repository/          # Truy xuất dữ liệu
├── security/            # JWT, OAuth2, cấu hình Spring Security
├── service/             # Xử lý nghiệp vụ
├── specification/       # Truy vấn động bằng JPA Specification
└── utils/               # Các lớp tiện ích
```

## Các nhóm API

| Nhóm chức năng | Đường dẫn cơ sở | Quyền truy cập |
| --- | --- | --- |
| Xác thực | `/api/auth/**` | Công khai |
| Đăng nhập Google | `/api/oauth2/authorization/google` | Công khai |
| Người dùng | `/api/users/**` | Người dùng / Quản trị viên |
| Sản phẩm | `/api/products/**` | Xem công khai, quản lý bởi Admin |
| Danh mục | `/api/categories/**` | Xem công khai, quản lý bởi Admin |
| Thương hiệu | `/api/brands/**` | Xem công khai, quản lý bởi Admin |
| Giỏ hàng | `/api/carts/**` | Khách hoặc người dùng |
| Đơn hàng | `/api/orders/**` | Người dùng / Quản trị viên |
| Thanh toán | `/api/payments/**` | Tùy nghiệp vụ |
| Tệp hình ảnh | `/api/files/**` | Quản trị viên |

## Hướng dẫn chạy dự án

### Yêu cầu môi trường

- Java 21 trở lên
- Maven 3.8 trở lên
- MySQL 8.0 trở lên
- Redis 7.x

### 1. Tải mã nguồn

```bash
git clone https://github.com/Giap0201/computer-shop.git
cd computer-shop
```

### 2. Tạo cơ sở dữ liệu

```sql
CREATE DATABASE computer_shop_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

### 3. Cấu hình ứng dụng

Tạo hoặc cập nhật tệp `src/main/resources/application.properties`:

```properties
# Server
server.port=8080
server.servlet.context-path=/api

# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/computer_shop_db?useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

# JWT
jwt.signer-key=YOUR_BASE64_ACCESS_SECRET_KEY
jwt.refresh-signer-key=YOUR_BASE64_REFRESH_SECRET_KEY
jwt.valid-duration=604800
jwt.refreshable-duration=604800

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Google OAuth2
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=openid,email,profile
app.oauth2.redirect-uri=http://localhost:3002/oauth2/callback

# Email
spring.mail.host=smtp-relay.brevo.com
spring.mail.port=587
spring.mail.username=YOUR_BREVO_EMAIL
spring.mail.password=YOUR_BREVO_API_KEY

# VNPay Sandbox
vnpay.tmn-code=YOUR_TMN_CODE
vnpay.hash-secret=YOUR_HASH_SECRET
vnpay.url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
vnpay.return-url=http://localhost:8080/api/payments/vnpay-return
```

> Không đưa mật khẩu, khóa JWT, thông tin OAuth2 hoặc khóa VNPay thật lên GitHub. Nên sử dụng biến môi trường cho các thông tin nhạy cảm.

### 4. Khởi chạy ứng dụng

```bash
mvn spring-boot:run
```

Ứng dụng chạy tại:

```text
http://localhost:8080/api
```

### 5. Xem tài liệu API

Sau khi ứng dụng chạy thành công, truy cập Swagger UI tại:

```text
http://localhost:8080/api/swagger-ui/index.html
```

## Tác giả

**Nguyễn Hữu Giáp**  
Backend Developer

- GitHub: [Giap0201](https://github.com/Giap0201)

---

<p align="center">
  Xây dựng với Spring Boot, MySQL và Redis
</p>
