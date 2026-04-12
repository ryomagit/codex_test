# Book app requirements

プログラマー向け書籍管理ツールの要件と実装方針です。セッションが切り替わっても、この内容を前提として扱います。

## Product goal

このアプリは、プログラマーが技術書を発見し、管理し、レビューし、他ユーザーのおすすめから学習対象を探せるようにするためのツールです。

代表的なユースケース:

- ユーザーが Java などの技術領域でおすすめの書籍を検索・ランキングから発見する。
- ユーザーが書籍を保存し、お気に入り、レビュー本文、1〜5 の星評価を登録する。
- すべてのユーザーが書籍ごとのレビューと評価を確認する。
- フォロワー数が多いユーザーや、特定ユーザーのおすすめ書籍リストを確認する。
- 固定ジャンルとタグから学習対象の書籍を探す。

## Fixed decisions

- API 技術要素は Java 17+、Jersey、Google Guice、Grizzly から変更しない。
- API の実行とテストはローカルの `gradle` コマンドではなく、必ず `./gradlew` を使う。
- API の DB アクセスは EclipseLink JPA を使い、複雑な集計・結合クエリでは QueryDSL を使う。
- Java ファイル編集後は google-java-format を Spotless 経由で適用する。
- DB は MySQL + Docker Compose を使う。
- DB 関連ファイルは `db/` 配下に置く。
- 書籍情報取得元は Google Books API とする。
- 認証はメールアドレス + パスワード方式とし、DB 保存セッションを HttpOnly Cookie で扱う。
- 「有名なユーザー」はフォロワー数順で扱う。
- 書籍分類は固定ジャンル + タグ方式とする。
- 個人のおすすめ紹介は、おすすめリスト方式とし、`public` / `private` の公開範囲を持つ。
- フロントエンドは Vue.js + Vite + npm で実装する。

## Current scope

現在の `develop` では、DB 基盤、書籍管理 API、Vue フロントエンドが一通り実装されています。

- API: Jersey Resource、Guice による依存注入、Grizzly 起動、Jackson JSON、例外 mapper。
- DB: MySQL 8.4、Docker Compose、初期化 SQL、EclipseLink JPA entity、RESOURCE_LOCAL transaction。
- 外部 API: Google Books API の volume 検索。
- Frontend: Vue single file component の単一画面アプリ、Vite proxy 経由の API 呼び出し。

## Data to persist

外部 API に毎回依存せず、ランキング・おすすめ・レビュー表示を安定させるため、書籍の基本情報はアプリ DB に保存する。

- 書籍: Google Books volume id、タイトル、説明、出版社、出版日、ISBN、サムネイル画像 URL
- 著者: 著者名と表示順
- 分類: 固定ジャンル、タグ
- ユーザー: email、表示名、プロフィール、フォロー関係
- 認証: パスワードハッシュ、パスワード salt、セッション
- 行動データ: お気に入り、レビュー本文、1〜5 の星評価
- おすすめ: ユーザーごとのおすすめリスト、リスト内の書籍、紹介コメント、表示順、公開範囲

## Data model

- `users`: email、display_name、password_hash、password_salt、created_at
- `user_profiles`: user_id、bio、avatar_url、headline
- `user_follows`: follower_user_id、followed_user_id、created_at
- `sessions`: user_id、token_hash、expires_at、created_at
- `books`: google_volume_id、title、description、publisher、published_date、isbn_10、isbn_13、thumbnail_url、created_at
- `book_authors`: book_id、author_name、display_order
- `genres`: name、slug
- `book_genres`: book_id、genre_id
- `tags`: name、slug
- `book_tags`: book_id、tag_id
- `favorites`: user_id、book_id、created_at
- `reviews`: id、user_id、book_id、rating、body、created_at、updated_at
- `recommendation_lists`: user_id、title、description、visibility、created_at、updated_at
- `recommendation_list_items`: list_id、book_id、comment、display_order

主な制約:

- `users.email` は unique。
- `books.google_volume_id` は unique。
- `reviews.rating` は 1〜5。
- `favorites` は 1 ユーザー 1 書籍 1 件。
- `reviews` は 1 ユーザー 1 書籍 1 件。
- `recommendation_list_items.display_order` はリスト内で unique。
- `user_follows` は自分自身をフォローできない。

## API scope

- 認証: `POST /api/auth/register`、`POST /api/auth/login`、`POST /api/auth/logout`、`GET /api/auth/me`
- ユーザー: `GET /api/users/{userId}`、`POST /api/users/{userId}/follow`、`DELETE /api/users/{userId}/follow`、`GET /api/users/featured`、`GET /api/users/{userId}/recommendation-lists`
- 書籍: `GET /api/books/search?q=...`、`POST /api/books`、`GET /api/books/{bookId}`
- お気に入り: `POST /api/books/{bookId}/favorite`、`DELETE /api/books/{bookId}/favorite`、`GET /api/me/favorites`
- レビュー: `PUT /api/books/{bookId}/review`、`DELETE /api/books/{bookId}/review`、`GET /api/books/{bookId}/reviews`
- ジャンル: `GET /api/genres`、`GET /api/genres/{genreId}/ranking`
- おすすめリスト: `POST /api/me/recommendation-lists`、`PUT /api/me/recommendation-lists/{listId}`、`DELETE /api/me/recommendation-lists/{listId}`、`GET /api/recommendation-lists/{listId}`

## Frontend scope

- アカウント: 登録、ログイン、ログアウト、ログイン中ユーザー表示
- 書籍発見: Google Books API 経由の検索、保存時のジャンル・タグ指定
- 書籍詳細: 著者、画像、ISBN、説明、出版社、出版日、タグ表示
- 読書ログ: お気に入り追加・削除・一覧、レビュー作成・更新・削除・一覧
- 発見導線: 固定ジャンル別ランキング、タグクリックによる検索
- ユーザー: フォロワー数順のユーザー一覧、プロフィール表示、フォロー・解除
- 状態表示: 読み込み中、成功、失敗

## Branch policy

各 feature ブランチは GitHub Pull Request で `develop` に merge する。ローカルで `develop` へ merge しない。
