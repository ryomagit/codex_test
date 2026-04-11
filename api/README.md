# API

Java + Jersey + Google Guice で実装した最小 API です。

## Requirements

- Java 17+
- Gradle Wrapper

このプロジェクトではローカルにインストールされた `gradle` コマンドではなく、`./gradlew` を正とします。

## Endpoints

- `GET /health`
- `GET /api/hello`

## Run

```bash
./scripts/run.sh
```

または:

```bash
./gradlew run
```

デフォルトでは `8080` 番ポートで起動します。変更する場合は `PORT` を指定します。

```bash
PORT=9090 ./scripts/run.sh
```

## Test

```bash
./scripts/test.sh
```

または:

```bash
./gradlew test
```

## Implementation notes

- Jersey は HTTP API の Resource 定義に使います。
- Google Guice はサービス生成と依存注入に使います。
- `Main` は Grizzly 上で Jersey アプリケーションを起動します。
- Spring Boot や素の `HttpServer` へ置き換えません。
