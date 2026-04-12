# API

Java + Jersey + Google Guice で実装した最小 API です。

## Requirements

- Java 17+
- Gradle Wrapper

このプロジェクトではローカルにインストールされた `gradle` コマンドではなく、`./gradlew` を正とします。

## Endpoints

### Health

- `GET /health` - 認証不要。API サーバーの疎通確認を行います。

### Auth

- `POST /api/auth/register` - 認証不要。メールアドレス、パスワード、表示名でユーザーを作成し、ログイン cookie を返します。
- `POST /api/auth/login` - 認証不要。メールアドレスとパスワードでログインし、ログイン cookie を返します。
- `POST /api/auth/logout` - 認証任意。現在のセッションを削除します。
- `GET /api/auth/me` - 認証必須。ログイン中ユーザーの情報を返します。

### Users

- `GET /api/users/{userId}` - 認証不要。指定ユーザーのプロフィールを返します。
- `GET /api/users/featured` - 認証不要。フォロワー数が多いユーザーを返します。
- `POST /api/users/{userId}/follow` - 認証必須。指定ユーザーをフォローします。
- `DELETE /api/users/{userId}/follow` - 認証必須。指定ユーザーのフォローを解除します。
- `GET /api/users/{userId}/recommendation-lists` - 認証任意。指定ユーザーのおすすめリスト一覧を返します。未ログインまたは別ユーザーとして見る場合は `public` のみ、本人として見る場合は `public` と `private` の両方を返します。

### Books

- `GET /api/books/search?q=...` - 認証不要。Google Books API から外部検索結果を返します。
- `POST /api/books` - 認証不要。Google volume id と書誌情報を DB に保存または更新します。
- `GET /api/books/{bookId}` - 認証不要。保存済み書籍の詳細を返します。

### Favorites

- `POST /api/books/{bookId}/favorite` - 認証必須。指定書籍をお気に入りに追加します。
- `DELETE /api/books/{bookId}/favorite` - 認証必須。指定書籍をお気に入りから削除します。
- `GET /api/me/favorites` - 認証必須。ログイン中ユーザーのお気に入り書籍一覧を返します。

### Reviews

- `PUT /api/books/{bookId}/review` - 認証必須。指定書籍への自分のレビューを作成または更新します。
- `DELETE /api/books/{bookId}/review` - 認証必須。指定書籍への自分のレビューを削除します。
- `GET /api/books/{bookId}/reviews` - 認証不要。指定書籍のレビュー一覧を返します。

### Genres

- `GET /api/genres` - 認証不要。ジャンル一覧を返します。
- `GET /api/genres/{genreId}/ranking` - 認証不要。指定ジャンルのランキングを favorites 数、平均 rating、review 数の順で返します。

### Recommendation lists

- `POST /api/me/recommendation-lists` - 認証必須。ログイン中ユーザーのおすすめリストを作成します。
- `PUT /api/me/recommendation-lists/{listId}` - 認証必須。ログイン中ユーザーが所有するおすすめリストを更新します。
- `DELETE /api/me/recommendation-lists/{listId}` - 認証必須。ログイン中ユーザーが所有するおすすめリストを削除します。
- `GET /api/recommendation-lists/{listId}` - 認証任意。おすすめリスト詳細を返します。`private` は作成者本人だけが閲覧できます。

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

VS Code で QueryDSL の `Q*` class が見つからない場合は、`./gradlew compileJava` を実行して生成 source を作成し、Java プロジェクトをリロードしてください。

## Implementation notes

- Jersey は HTTP API の Resource 定義に使います。
- Google Guice はサービス生成と依存注入に使います。
- `Main` は Grizzly 上で Jersey アプリケーションを起動します。
- DB アクセスは EclipseLink JPA を使います。
- 複雑な集計・結合クエリでは QueryDSL を使います。
- Spring Boot や素の `HttpServer` へ置き換えません。
