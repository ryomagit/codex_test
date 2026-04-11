# API handoff

`feature/api` の API 構成を次セッション以降も維持するための引き継ぎメモです。

## Fixed decisions

- API は Java 17+ で実装します。
- HTTP API は Jersey の Resource で定義します。
- DI は Google Guice で行います。
- 起動は Grizzly 上の Jersey アプリケーションとして行います。
- 実行とテストはローカルの `gradle` コマンドではなく、必ず `./gradlew` を使います。
- DB は MySQL + Docker Compose を前提にします。
- 書籍情報取得元は Google Books API とします。

## Do not change

- Spring Boot へ置き換えない。
- 素の `com.sun.net.httpserver.HttpServer` 実装へ戻さない。
- Guice を外して手動生成だけの構成へ戻さない。
- `gradle` コマンド前提の手順に戻さない。
- `feature/api` 完了後にローカルで `develop` へ merge しない。
- DB を PostgreSQL 前提にしない。
- DB 関連ファイルを API ディレクトリ直下へ混在させない。DB 基盤は `feature/db` の `db/` 配下で扱う。

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

## Next API scope

書籍管理 API は `feature/db` が GitHub 上で `develop` に merge された後に実装します。

- 認証: メールアドレス + パスワード、DB 保存セッション、HttpOnly Cookie。
- 書籍検索: Google Books API からタイトル、著者、ISBN、画像 URL などを取得。
- 書籍保存: Google Books volume id、タイトル、著者名、説明、出版社、出版日、ISBN、サムネイル画像 URL を MySQL に保存。
- お気に入り: ログインユーザーごとに書籍を追加・削除・一覧表示。
- レビュー: ログインユーザーごとに 1 書籍 1 レビュー、1〜5 の星評価、全ユーザーが閲覧可能。
- ジャンルランキング: 固定ジャンル + タグを使い、プログラマー向けに Java などの技術領域別ランキングを提供。
- おすすめリスト: ユーザーがテーマ付きのおすすめ書籍一覧を作成し、有名ユーザーのおすすめ一覧として表示可能にする。

## Session start checks

```bash
git status --short --branch
cd api
./gradlew test
./gradlew run
```

`./gradlew run` は API サーバーを起動し続けます。別ターミナルで `curl http://localhost:8080/health` などを確認してください。

書籍管理 API の実装前には `docs/book-app-requirements.md` も確認してください。
