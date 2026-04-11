# API handoff

`feature/api` の API 構成を次セッション以降も維持するための引き継ぎメモです。

## Fixed decisions

- API は Java 17+ で実装します。
- HTTP API は Jersey の Resource で定義します。
- DI は Google Guice で行います。
- 起動は Grizzly 上の Jersey アプリケーションとして行います。
- 実行とテストはローカルの `gradle` コマンドではなく、必ず `./gradlew` を使います。

## Do not change

- Spring Boot へ置き換えない。
- 素の `com.sun.net.httpserver.HttpServer` 実装へ戻さない。
- Guice を外して手動生成だけの構成へ戻さない。
- `gradle` コマンド前提の手順に戻さない。
- `feature/api` 完了後にローカルで `develop` へ merge しない。

## API contract

API はデフォルトで `http://localhost:8080` に起動します。

### GET /health

```json
{"status":"ok"}
```

### GET /api/hello

```json
{"message":"Hello from Java API"}
```

## Session start checks

```bash
git status --short --branch
cd api
./gradlew test
./gradlew run
```

`./gradlew run` は API サーバーを起動し続けます。別ターミナルで `curl http://localhost:8080/health` などを確認してください。

