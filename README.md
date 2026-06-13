📚 Hệ Thống Quản Lý Thư Viện - Nhóm 4
Hệ thống quản lý thư viện trực tuyến được xây dựng bằng Spring Boot, hỗ trợ đầy đủ chức năng cho Admin, Nhân viên và Độc giả.

🌐 Demo: https://quanlythuvien-production-1d34.up.railway.app/books/list

👥 Thành viên nhóm
Họ tên và Vai trò:

- Dương Lê Mĩ Tâm: Review SRS 
- Đào Xuân Xâm: Frontend
- Nguyễn Xuân Tú: Backend
- Phan Vân Anh: Tester
- Hà Thị Quyên: Viết tài liệu đặc tả
  
⚙️ Công nghệ sử dụng

Backend: Java 17, Spring Boot 3.2.0, Spring Security, Spring Data JPA
Frontend: Thymeleaf, Bootstrap 5.3, Font Awesome 6.4, Chart.js
Database: MySQL 8.x
IDE: Spring Tools Suite 4 (STS)
Quản lý phụ thuộc: Maven


🚀 Chức năng chính
Admin / Nhân viên:

Đăng nhập, đăng xuất
Quản lý sách (thêm, sửa, xóa, tìm kiếm)
Quản lý phiếu mượn (duyệt, từ chối, xác nhận nhận tiền, trả sách)
Quản lý độc giả
Quản lý nhân viên (chỉ Admin)
Thống kê biểu đồ (chỉ Admin)

Độc giả:

Đăng ký tài khoản, đăng nhập
Xem danh sách sách, tìm kiếm, lọc theo thể loại
Xem chi tiết sách
Đăng ký mượn sách
Xem phiếu xác nhận mượn
Gia hạn sách (tối đa 2 lần)
Đánh giá sách (sau khi trả)
Xem lịch sử mượn trả


🗄️ Cài đặt và chạy
Yêu cầu:

Java 17+
MySQL 8.x
Maven

Các bước:
1. Clone project:
bashgit clone https://github.com/Daoxuanxam/Nh-m-4-QLDACNTT.git
cd Nh-m-4-QLDACNTT
2. Tạo database:
sqlCREATE DATABASE quanlythuvien;
3. Cấu hình database trong src/main/resources/application.properties:

propertiesspring.datasource.username=root

spring.datasource.password=your_password
5. Chạy ứng dụng:
bash.\mvnw spring-boot:run
6. Truy cập: http://localhost:8080

🔑 Tài khoản mặc định: Vai trò admin

Tài khoản: admin

Mật khẩu: admin123
















Tài khoảnMật khẩuVai tròadminadmin123Admin

