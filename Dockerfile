# Bước 1: Chọn Image nền (JRE) cho nhẹ, ở đây dùng Java 21
FROM eclipse-temurin:21-jre-alpine

# Bước 2: Thiết lập thư mục làm việc bên trong Container
WORKDIR /app

# Bước 3: Copy file JAR từ máy bạn vào trong Container
# Lưu ý: Sửa tên file .jar cho đúng với file trong thư mục target của bạn
COPY target/dogomynghe-0.0.1-SNAPSHOT.jar app.jar

# Bước 5: Lệnh để chạy ứng dụng khi Container khởi động
ENTRYPOINT ["java", "-jar", "app.jar"]