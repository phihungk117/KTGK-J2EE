# Bước 1: Build ứng dụng bằng Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
# Copy file cấu hình và source code vào container
COPY pom.xml .
COPY src ./src
# Đóng gói ứng dụng thành file .jar (bỏ qua chạy test để build nhanh hơn)
RUN mvn clean package -DskipTests

# Bước 2: Chạy ứng dụng
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy file .jar đã build từ Bước 1 sang
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 80 theo đúng yêu cầu của hệ thống server
EXPOSE 80

# Chạy ứng dụng và ép Spring Boot khởi chạy trên cổng 80 (thay vì 8080 mặc định)
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=80"]
