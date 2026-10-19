# Tích Phú – Personal Finance Management App

Ứng dụng Android hỗ trợ quản lý thu chi và lập kế hoạch tài chính cá nhân, giúp người dùng theo dõi giao dịch, quản lý ngân sách và nắm bắt tình hình tài chính thông qua thống kê trực quan.

## 📌 Giới thiệu

**Tích Phú** là dự án phát triển ứng dụng quản lý tài chính cá nhân trên nền tảng Android. Dự án bao gồm ứng dụng di động và hệ thống backend API, kết nối với cơ sở dữ liệu để xử lý dữ liệu tài chính của người dùng.

**Mục tiêu dự án:**
- Xây dựng ứng dụng Android phục vụ quản lý tài chính cá nhân.
- Thực hành phát triển giao diện Android và tích hợp backend API.
- Áp dụng cơ sở dữ liệu để lưu trữ và xử lý dữ liệu.
- Thực hành quản lý mã nguồn bằng Git và GitHub.

## ✨ Chức năng chính

### 1. Quản lý tài khoản
- Đăng ký, đăng nhập và đăng xuất.
- Quản lý thông tin cá nhân.
- Hỗ trợ xác thực tài khoản hoặc khôi phục mật khẩu qua email.

### 2. Quản lý thu chi
- Thêm, chỉnh sửa và xóa giao dịch thu nhập, chi tiêu.
- Phân loại giao dịch theo nhóm để thuận tiện theo dõi.
- Hỗ trợ quản lý các giao dịch tài chính cá nhân.

### 3. Quản lý ngân sách
- Lập ngân sách phục vụ kế hoạch tài chính cá nhân.
- Theo dõi tình hình chi tiêu so với ngân sách đã thiết lập.

### 4. Thống kê tài chính
- Tổng hợp dữ liệu thu nhập và chi tiêu.
- Hiển thị biểu đồ thống kê để hỗ trợ theo dõi tình hình tài chính.

### 5. Thông báo và tích hợp dịch vụ
- Tích hợp Firebase cho các chức năng tương ứng của ứng dụng.
- Hỗ trợ thông báo nhắc nhở.
- Hỗ trợ gửi email xác thực hoặc khôi phục mật khẩu.

## 🛠️ Công nghệ sử dụng

- **Android:** Kotlin, Jetpack Compose
- **Backend:** PHP REST API
- **Database:** MySQL
- **Firebase:** Các chức năng Firebase được sử dụng trong ứng dụng
- **Development tools:** Android Studio, XAMPP, Git, GitHub
- **Dependency management:** Gradle và Composer

## 📁 Cấu trúc dự án

```text
Tich-Phu/
├── android-app/       # Mã nguồn ứng dụng Android
├── backend-api/       # Backend API viết bằng PHP
├── .gitignore
└── README.md
```

## ⚙️ Cài đặt và chạy dự án

### 1. Yêu cầu môi trường

- Android Studio và Android SDK.
- PHP và MySQL; có thể sử dụng XAMPP cho môi trường phát triển cục bộ.
- Composer để cài đặt thư viện PHP.
- Git để clone mã nguồn.
- Cấu hình Firebase và email nếu các chức năng tương ứng được sử dụng.

### 2. Tải mã nguồn

```bash
git clone https://github.com/nqviet0905/Tich-Phu.git
cd Tich-Phu
```

Mở thư mục `android-app` bằng Android Studio để xây dựng và chạy ứng dụng Android.

### 3. Cấu hình backend API

1. Đặt thư mục `backend-api` trong môi trường PHP phù hợp, chẳng hạn thư mục `htdocs` của XAMPP.
2. Khởi động Apache và MySQL.
3. Tạo hoặc khôi phục cơ sở dữ liệu theo cấu trúc của dự án.
4. Cấu hình kết nối cơ sở dữ liệu trong `backend-api/config/database.php` hoặc thông qua các biến môi trường tương ứng.
5. Cài đặt các thư viện PHP từ thư mục `backend-api`:

```bash
composer install
```

6. Cấu hình Firebase và email theo môi trường chạy. Không đưa mật khẩu, khóa riêng Firebase hoặc thông tin xác thực vào GitHub.

> Lưu ý: Cần có cấu trúc cơ sở dữ liệu và các cấu hình môi trường phù hợp trước khi chạy backend. Không chia sẻ file chứa thông tin xác thực.

## 🌐 Cấu hình địa chỉ IP và port cho Android

Ứng dụng Android cần sử dụng đúng địa chỉ máy chủ backend để gửi yêu cầu API. Địa chỉ cần cấu hình tùy theo môi trường chạy ứng dụng.

### Trường hợp 1: Chạy trên Android Emulator

Nếu backend chạy trên máy tính Windows và ứng dụng chạy trong **Android Emulator**, có thể sử dụng địa chỉ đặc biệt:

```text
http://10.0.2.2:8080/
```

- `10.0.2.2` là địa chỉ đặc biệt để Android Emulator truy cập máy chủ trên máy tính host.
- `8080` là port ví dụ; phải trùng với port mà backend hoặc Apache thực sự đang lắng nghe.
- Đường dẫn sau port cần được điều chỉnh theo vị trí API của dự án.

Ví dụ, nếu API được đặt tại `personal_finance_api` trong `htdocs`, URL có thể có dạng:

```text
http://10.0.2.2:8080/personal_finance_api/
```

Chỉ sử dụng URL ví dụ này nếu cấu hình Apache và vị trí thư mục thực tế đúng như mô tả.

### Trường hợp 2: Chạy thử trên điện thoại Android thật

Khi điện thoại và máy tính chạy backend cùng kết nối một mạng Wi-Fi/LAN, sử dụng **địa chỉ IPv4 của máy tính**, không sử dụng `10.0.2.2`.

Ví dụ:

```text
http://192.168.1.24:8080/
```

Trong đó:
- `192.168.1.24` là ví dụ về địa chỉ IPv4 của máy tính.
- `8080` là port backend/Apache đang sử dụng.
- Điện thoại và máy tính cần có kết nối mạng cho phép truy cập lẫn nhau.
- Apache/backend phải lắng nghe trên giao diện mạng phù hợp; tường lửa Windows không được chặn kết nối.

Để kiểm tra địa chỉ IPv4 trên Windows:

1. Mở Command Prompt.
2. Chạy lệnh:

```cmd
ipconfig
```

3. Tìm dòng `IPv4 Address` của bộ điều hợp Wi-Fi hoặc Ethernet đang sử dụng.
4. Dùng địa chỉ đó để cấu hình URL API trên Android.

### Trường hợp 3: Đổi port backend

Nếu cần dùng port `8080`, phải đảm bảo dịch vụ backend thực sự chạy trên port đó.

Với Apache trong XAMPP, có thể kiểm tra hoặc chỉnh port trong cấu hình Apache, thường là file `httpd.conf`. Sau khi đổi cấu hình, khởi động lại Apache và kiểm tra port đang hoạt động.

Nếu backend chạy bằng một máy chủ PHP riêng, hãy đặt port trong lệnh khởi chạy tương ứng, chẳng hạn:

```bash
php -S 0.0.0.0:8080
```

Lệnh này chỉ dành cho máy chủ phát triển tích hợp của PHP và không thay thế cấu hình Apache nếu dự án đang chạy bằng XAMPP.

### Lưu ý khi kết nối Android

- Cấu hình URL API trong ứng dụng Android phải khớp với môi trường đang chạy.
- Đảm bảo đường dẫn endpoint chính xác, không chỉ đúng IP và port.
- Nếu dùng HTTP thay vì HTTPS, cần kiểm tra cấu hình bảo mật mạng của Android và chỉ cho phép HTTP trong môi trường phát triển khi phù hợp.
- Không sử dụng địa chỉ IP LAN làm địa chỉ máy chủ công khai cho người dùng bên ngoài mạng nội bộ.

## 🔐 Bảo mật

- Không commit mật khẩu email, khóa riêng Firebase, token hoặc thông tin xác thực.
- Sử dụng biến môi trường hoặc cấu hình riêng cho từng môi trường.
- Không đưa file chứa thông tin bí mật lên GitHub.
- Khi triển khai thực tế, cần cấu hình HTTPS và bảo vệ các endpoint API phù hợp.

## 👨‍💻 Tác giả

**Nguyễn Quốc Việt**

GitHub: https://github.com/nqviet0905

Repository: https://github.com/nqviet0905/Tich-Phu

## 📌 Trạng thái dự án

Dự án đang được phát triển và hoàn thiện.

