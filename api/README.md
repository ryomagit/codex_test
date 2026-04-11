# API

素の Java 標準ライブラリで実装した最小 API です。

## Requirements

- Java 17+

この環境では Gradle が未インストールだったため、まずは `javac` ベースのスクリプトで起動・テストできる構成にしています。

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

