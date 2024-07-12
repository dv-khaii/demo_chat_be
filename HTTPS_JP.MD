# アプリケーションへSSL実装マニュアル

BEアプリケーションにHttpsを展開するには以下通りです。　

## 1. SSL証明書を購入する
証明書(CA)は通常、一般的な形式のPEMであり、例えば次のファイルが含まれる。


- **cert1.pem**: これはサーバーのSSL証明書であり、公開証明書とも呼ばれる。
- **privkey1.pem**: これは公開証明書に該当する秘密鍵です（非公開）。
- **chain1.pem**: これは証明書チェーンであり、自分の証明書からCAの証明書までの信頼のチェーンを作成するために必要な1つか複数の中間証明書が含まれる。証明書チェーンは、サーバーの証明書が信頼できるCAによって署名されていることをお客さんが検証するのに役立つ。
## 2. keystoreファイルを作成する

Spring BootはデフォルトとしてSSL構成でのPEMファイルの使用を直接サポートしていません。Java KeyStore(JKS)またはPKCS12に変換する必要があります。変換方法については以下通りです。提供されたPEMファイルからkeystore.p12ファイルを作成し、openssl:を利用する。
```bash
openssl pkcs12 -export -in cert1.pem -inkey privkey1.pem -out keystore.p12 -name aliasname -CAfile chain1.pem -caname root
```

コマンド:

- keystore.p12: 生成されるkeystoreファイル名は環境変数：SSL_KEY_STOREへ導入するように使われます。　
- aliasname: alias-nameは環境変数SSL_KEY_ALIASへ導入するように使われます。
- root: KeyStore内の元証明書にアサインしたい名前です
- keystoreを作成する際に、opensslがパスワードを確認して暗記することがあります。このパスワードは環境変数SSL_KEY_STORE_PASSWORDへ導入するように使われます。
## 3. アプリ用のSSL構成

### 3.1. keystore.p12ファイルをアプリに導入


普通はkeystore.p12ファイルをプロジェクトのルートディレクトリに配置します (.envファイルと同様)。

### 3.2 SSLの構成

SSLは.env.に定義されて、各値が以下ファイルに入れされます。
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

**注記**:

- とりあえず、SSL_ENABLED=trueをセットすることでSSLを起動する。falseをセットする場合は他の箇所にもセットする必要ですが、何でもセットしても良いです。
- SSLが有効の場合はポート：4431を設定したほうが良いです・