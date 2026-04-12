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

## Format

Java の format は google-java-format を使います。編集後は次を実行してください。

```bash
./gradlew spotlessApply
```

確認だけ行う場合:

```bash
./gradlew spotlessCheck
```

エディタで保存時に自動整形したい場合は、google-java-format 対応プラグインを有効にし、Java ファイル保存時に google-java-format が走るよう設定してください。リポジトリ側の判定は Spotless 経由の `./gradlew spotlessCheck` を正とします。

## Implementation notes

- Jersey は HTTP API の Resource 定義に使います。
- Google Guice はサービス生成と依存注入に使います。
- `Main` は Grizzly 上で Jersey アプリケーションを起動します。
- DB アクセスは EclipseLink JPA を使います。
- 複雑な集計・結合クエリでは QueryDSL を使います。
- Spring Boot や素の `HttpServer` へ置き換えません。
