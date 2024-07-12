# HƯỚNG DẪN TRIỂN KHAI SSL CHO APPLICATION

Để triển khai Https cho ứng dụng BE, thực hiện như sau

## 1. Đăng ký/mua SSL certificate

Chứng chỉ (CA) được cấp thường có định dạng phổ biến là PEM, ví dụ gồm các file như sau:

- **cert1.pem**: Đây là chứng chỉ SSL của server, còn được gọi là chứng chỉ công cộng
- **privkey1.pem**: Đây là khóa riêng tương ứng với chứng chỉ công cộng (không được tiết lộ)
- **chain1.pem**: Đây là chuỗi chứng chỉ, bao gồm một hoặc nhiều chứng chỉ trung gian cần thiết để tạo một chuỗi tin cậy từ chứng chỉ của bạn đến chứng chỉ gốc của CA. Chuỗi chứng chỉ giúp khách hàng xác minh rằng chứng chỉ của máy chủ được một CA đáng tin cậy ký

## 2. Tạo file keystore

Spring Boot mặc định không hỗ trợ trực tiếp việc sử dụng file PEM cho cấu hình SSL. Chúng ta cần phải chuyển đổi chúng vào một Java KeyStore (JKS) hoặc PKCS12. Dưới đây là cách chuyển đổi, tạo file keystore.p12 từ các file PEM được cấp, sử dụng openssl:

```bash
openssl pkcs12 -export -in cert1.pem -inkey privkey1.pem -out keystore.p12 -name aliasname -CAfile chain1.pem -caname root
```

Lệnh trên:

- keystore.p12: tên file keystore được tạo ra, được dùng đưa vào biến môi trường: SSL_KEY_STORE
- aliasname: alias-name dùng để đưa vào biến môi trường SSL_KEY_ALIAS
- root: Tên mà muốn gán cho CA gốc trong KeyStore
- Khi tạo keystore, openssl có hỏi password, hãy xác nhận password và ghi nhớ. Password này được dùng để đưa vào biến môi trường SSL_KEY_STORE_PASSWORD

## 3. Cấu hình SSL cho application

### 3.1. Đưa file keystore.p12 vào ứng dụng

Thường thì đưa file keystore.p12 ra thư mục gốc dự án (tương tự file .env)

### 3.2 Cấu hình SSL

SSL được cấu hình trong file .env. Các giá trị đưa vào file :

```bash
.env
---
APP_TILTE=Chat Application
APP_VERSION=1.0.0
APP_BANNER=classpath:banner.txt
...
APP_HOST_URL=localhost
APP_HOST_PORT=4431
APP_SCHEMA=https
SSL_ENABLED=true
SSL_KEY_STORE_TYPE=PKCS12
SSL_KEY_STORE=file:keystore.p12
SSL_KEY_ALIAS=xeexchat
SSL_KEY_STORE_PASSWORD=xeexpass
```

**Ghi chú**:

- Đầu tiên phải bật SSL bằng cách set SSL_ENABLED=true. Nếu đặt false thì chổ khác vẫn phải set, nhưng set gì cũng không quan trọng!!!
- Khi enable SSL, nên đặt port là 4431