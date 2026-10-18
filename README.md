# Renal Insight — CKD Risk Screening

Ứng dụng chạy local hỗ trợ sàng lọc nguy cơ bệnh thận mạn, gồm:

- Backend Java 17, Spring Boot, Spring Security JWT, JPA, Flyway và MySQL.
- Frontend HTML, CSS và JavaScript thuần.
- Nhập đủ 24 chỉ số lâm sàng, dự đoán đơn và dự đoán hàng loạt từ CSV/XLSX.
- Lưu lịch sử và thống kê kết quả theo tài khoản.
- Sẵn adapter để kết nối API mô hình AI sau này.

Theo phạm vi đã thống nhất, dự án không có biểu đồ SHAP và không đánh giá giai đoạn CKD.

## 1. Yêu cầu môi trường

Cài đặt các công cụ sau:

- Java 17 trở lên.
- Node.js 18 trở lên, đi kèm npm.
- Docker và Docker Compose để chạy MySQL.

Kiểm tra phiên bản:

```bash
java -version
node --version
npm --version
docker --version
docker compose version
```

Các lệnh bên dưới được chạy từ thư mục gốc của dự án, tức thư mục chứa `README.md`, `docker-compose.yml`, `Base-BE` và `frontend`.

## 2. Khởi động MySQL

Chạy container MySQL:

```bash
docker compose up -d mysql
```

Kiểm tra trạng thái:

```bash
docker compose ps
```

Đợi đến khi service `mysql` có trạng thái `healthy`. Cấu hình mặc định:

| Thuộc tính | Giá trị |
| --- | --- |
| Host | `localhost` |
| Port | `3306` |
| Database | `ckd_screening` |
| Username | `ckd` |
| Password | `ckd` |

Không cần tự tạo bảng. Flyway sẽ tạo schema khi backend khởi động lần đầu.

## 3. Khởi động backend

Mở terminal thứ nhất:

```bash
cd Base-BE
chmod +x mvnw
./mvnw spring-boot:run
```

Backend chạy tại `http://localhost:8080`.

Kiểm tra API health:

```bash
curl http://localhost:8080/api/health
```

Kết quả mong đợi:

```json
{
  "service": "ckd-screening-backend",
  "status": "UP"
}
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## 4. Khởi động frontend

Giữ backend đang chạy và mở terminal thứ hai từ thư mục gốc dự án:

```bash
cd frontend
npm run dev
```

Frontend chạy tại:

```text
http://localhost:5173
```

Mở địa chỉ này trên trình duyệt, chọn **Create one** để tạo tài khoản, sau đó đăng nhập và thực hiện screening.

## 5. Trình tự chạy nhanh

Terminal 1 — MySQL:

```bash
docker compose up -d mysql
```

Terminal 2 — backend:

```bash
cd Base-BE
./mvnw spring-boot:run
```

Terminal 3 — frontend:

```bash
cd frontend
npm run dev
```

Sau đó truy cập `http://localhost:5173`.

## 6. Cấu hình bằng biến môi trường

Các giá trị mẫu nằm trong [.env.example](.env.example). Spring Boot tự dùng cấu hình local mặc định nên không bắt buộc phải khai báo biến môi trường khi chạy với Docker Compose trong repository này.

Nếu muốn dùng cấu hình riêng, khai báo trước khi chạy backend:

```bash
export DB_URL='jdbc:mysql://localhost:3306/ckd_screening?useUnicode=true&characterEncoding=UTF-8&connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true'
export DB_USERNAME=ckd
export DB_PASSWORD=ckd
export JWT_SECRET='replace-with-a-long-random-secret'
export FRONTEND_ORIGIN=http://localhost:5173

cd Base-BE
./mvnw spring-boot:run
```

Lưu ý phải đặt `DB_URL` trong dấu nháy vì URL chứa ký tự `&`.

## 7. Cấu hình API AI

Hiện tại mô hình AI chưa được cung cấp. Khi `AI_API_URL` để trống:

- Dữ liệu screening vẫn được validate và lưu vào MySQL.
- Bản ghi có trạng thái `FAILED`.
- Giao diện hiển thị thông báo `AI_API_URL is not configured`.

Khi đã có service AI, khai báo URL trước khi chạy backend:

```bash
export AI_API_URL=http://localhost:8000
export AI_API_KEY=your-api-key

cd Base-BE
./mvnw spring-boot:run
```

Backend sẽ gọi:

```text
POST http://localhost:8000/predict
```

Định dạng request và response được mô tả trong [docs/ai-api-contract.md](docs/ai-api-contract.md).

## 8. Dự đoán hàng loạt

Trang **Batch upload** hỗ trợ `.csv` và `.xlsx`, tối đa 1.000 dòng mỗi lần tải lên.

Các cột bắt buộc:

```text
patientCode,age,bp,htn,dm,cad,appet,pe,ane,sg,al,su,rbc,pc,pcc,ba,bgr,bu,sc,sod,pot,hemo,pcv,wc,rc
```

File mẫu có tại [frontend/src/sample-patients.csv](frontend/src/sample-patients.csv) hoặc tải trực tiếp từ trang Batch upload.

## 9. Chạy kiểm thử

Backend:

```bash
cd Base-BE
./mvnw clean verify
```

Frontend:

```bash
cd frontend
npm run lint
npm run build
npm run test
```

Frontend sau khi build nằm trong `frontend/dist`.

## 10. Dừng dự án

Dừng backend và frontend bằng `Ctrl+C` trong từng terminal.

Dừng MySQL:

```bash
docker compose down
```

Dữ liệu MySQL vẫn được giữ trong Docker volume `ckd_mysql_data`.

Nếu muốn xóa hoàn toàn database local và tạo lại từ đầu, chạy lệnh sau. **Lệnh này xóa toàn bộ dữ liệu đã lưu:**

```bash
docker compose down -v
```

## 11. Lỗi thường gặp

### `Permission denied: ./mvnw`

```bash
chmod +x Base-BE/mvnw
```

### Port `3306` đã được sử dụng

Một MySQL khác đang chạy trên máy. Dừng MySQL đó hoặc đổi phần ánh xạ port trong `docker-compose.yml` và cập nhật `DB_URL` tương ứng.

### Backend báo không kết nối được database

Kiểm tra container và log:

```bash
docker compose ps
docker compose logs mysql
```

Chỉ khởi động backend sau khi MySQL chuyển sang trạng thái `healthy`.

### `node` hoặc `npm: command not found`

Cài Node.js 18 trở lên, mở terminal mới và kiểm tra lại:

```bash
node --version
npm --version
```

### Frontend báo lỗi CORS

Frontend mặc định phải chạy tại `http://localhost:5173`. Nếu dùng port khác, đặt đúng origin trước khi chạy backend:

```bash
export FRONTEND_ORIGIN=http://localhost:YOUR_PORT
```

### Dự đoán có trạng thái `FAILED`

Nếu thông báo là `AI_API_URL is not configured`, đây là hành vi dự kiến khi chưa kết nối mô hình AI. Xem mục **Cấu hình API AI** ở trên.
# TTCSN_N11
