# API handoff

API 構成を次セッション以降も維持するための引き継ぎメモです。詳細な背景は `docs/architecture.md` と `docs/learning-guide.md` も参照してください。

## Fixed decisions

- API は Java 17+ で実装する。
- HTTP API は Jersey の Resource で定義する。
- DI は Google Guice で行う。
- 起動は Grizzly 上の Jersey アプリケーションとして行う。
- 実行とテストはローカルの `gradle` コマンドではなく、必ず `./gradlew` を使う。
- DB は MySQL + Docker Compose を前提にする。
- 書籍情報取得元は Google Books API とする。
- DB アクセスは EclipseLink JPA を使う。
- 複雑な集計・結合クエリでは QueryDSL を使う。
- Java ファイル編集後は google-java-format を Spotless 経由で適用する。

## Do not change

- Spring Boot へ置き換えない。
- 素の `com.sun.net.httpserver.HttpServer` 実装へ戻さない。
- Guice を外して手動生成だけの構成へ戻さない。
- `gradle` コマンド前提の手順に戻さない。
- google-java-format 以外の Java formatter に切り替えない。
- DB アクセスを JDBC Repository 実装へ戻さない。
- DB を PostgreSQL 前提にしない。
- DB 関連ファイルを API ディレクトリ直下へ混在させない。DB 基盤は `db/` 配下で扱う。

## Runtime

API はデフォルトで `http://localhost:8080` に起動する。`HOST` と `PORT` で起動アドレスを変更できる。

```bash
cd api
./gradlew run
```

DB 接続は `DB_HOST`、`DB_PORT`、`DB_NAME`、`DB_USER`、`DB_PASSWORD`、`DB_JDBC_URL` で上書きできる。デフォルトは `db/compose.yaml` の MySQL に合わせている。

## API contract

### Health

- `GET /health`: 認証不要。疎通確認。

### Auth

- `POST /api/auth/register`: 認証不要。ユーザー作成とログイン Cookie 発行。
- `POST /api/auth/login`: 認証不要。ログイン Cookie 発行。
- `POST /api/auth/logout`: 認証任意。現在のセッション削除。
- `GET /api/auth/me`: 認証必須。ログイン中ユーザー取得。

### Users

- `GET /api/users/{userId}`: 認証不要。指定ユーザーのプロフィール取得。
- `GET /api/users/featured`: 認証不要。フォロワー数が多いユーザー取得。
- `POST /api/users/{userId}/follow`: 認証必須。フォロー。
- `DELETE /api/users/{userId}/follow`: 認証必須。フォロー解除。
- `GET /api/users/{userId}/recommendation-lists`: 認証任意。本人なら `public` と `private`、別ユーザーまたは未ログインなら `public` のおすすめリストを返す。

### Books

- `GET /api/books/search?q=...`: 認証不要。Google Books API から外部検索結果を返す。
- `POST /api/books`: 認証不要。Google volume id と書誌情報を DB に保存または更新する。
- `GET /api/books/{bookId}`: 認証不要。保存済み書籍の詳細を返す。

### Favorites and reviews

- `POST /api/books/{bookId}/favorite`: 認証必須。お気に入り追加。
- `DELETE /api/books/{bookId}/favorite`: 認証必須。お気に入り削除。
- `GET /api/me/favorites`: 認証必須。ログイン中ユーザーのお気に入り一覧。
- `PUT /api/books/{bookId}/review`: 認証必須。自分のレビューを作成または更新。
- `DELETE /api/books/{bookId}/review`: 認証必須。自分のレビューを削除。
- `GET /api/books/{bookId}/reviews`: 認証不要。指定書籍のレビュー一覧。

### Genres and recommendation lists

- `GET /api/genres`: 認証不要。ジャンル一覧。
- `GET /api/genres/{genreId}/ranking`: 認証不要。お気に入り数、平均 rating、レビュー数の順でランキングを返す。
- `POST /api/me/recommendation-lists`: 認証必須。おすすめリスト作成。
- `PUT /api/me/recommendation-lists/{listId}`: 認証必須。所有するおすすめリスト更新。
- `DELETE /api/me/recommendation-lists/{listId}`: 認証必須。所有するおすすめリスト削除。
- `GET /api/recommendation-lists/{listId}`: 認証任意。`private` は作成者本人だけが閲覧できる。

## Implementation map

- `AppConfig` が Guice injector を作成し、Jersey の `ResourceConfig` へ Resource、例外 mapper、service binding を登録する。
- `ApiModule` が `EntityManagerFactory`、`ObjectMapper`、`PasswordHasher`、`SessionTokens` を singleton として bind する。
- `Main` が Grizzly server を起動する。
- `JpaExecutor` が read/write の transaction 境界を担当する。
- `BookRepository.rankingByGenre` と `RecommendationRepository.listByUser` / item 並び替えなど、集計・結合・並び替えが絡む処理で QueryDSL を使う。
- `GoogleBooksClient` は Java 標準の `HttpClient` で Google Books API を呼び、Jersey Resource へ返す DTO に変換する。

## Session start checks

```bash
git status --short --branch
cd db
docker compose up -d
cd ../api
./gradlew test
./gradlew spotlessCheck
./gradlew run
```

`./gradlew run` は API サーバーを起動し続ける。別ターミナルで `curl http://localhost:8080/health` などを確認する。
