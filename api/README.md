# API

Java + Jersey + Google Guice で実装した最小 API です。

## Requirements

- Java 17+
- Gradle

この作業環境では Gradle が未インストールのため、依存関係の取得を伴うローカル実行確認は未実施です。
Gradle が利用できる環境では、以下のスクリプトで起動・テストします。

## Endpoints

- `GET /health`
- `GET /api/hello`

## Run

```bash
./scripts/run.sh
```

デフォルトでは `8080` 番ポートで起動します。変更する場合は `PORT` を指定します。

```bash
PORT=9090 ./scripts/run.sh
```

## Test

```bash
./scripts/test.sh
```

## Implementation notes

- Jersey は HTTP API の Resource 定義に使います。
- Google Guice はサービス生成と依存注入に使います。
- `Main` は Grizzly 上で Jersey アプリケーションを起動します。
