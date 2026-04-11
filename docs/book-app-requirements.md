# Book app requirements

プログラマー向け書籍管理ツールの要件と実装方針です。セッションが切り替わっても、この内容を前提として扱います。

## Product goal

このアプリは、プログラマーが技術書を発見し、管理し、レビューし、他ユーザーのおすすめから学習対象を探せるようにするためのツールです。

代表的なユースケース:

- ユーザーが Java でおすすめの書籍を検索・ランキングから発見する。
- ユーザーが書籍をお気に入りに追加し、レビュー本文と 1〜5 の星評価を登録する。
- すべてのユーザーが書籍ごとのレビューと評価を確認する。
- 有名なユーザーが公開しているおすすめ書籍一覧を確認する。
- 特定ジャンルの人気書籍ランキングを確認する。

## Fixed decisions

- API 技術要素は Java 17+、Jersey、Google Guice、Grizzly から変更しない。
- API の実行とテストはローカルの `gradle` コマンドではなく、必ず `./gradlew` を使う。
- DB は MySQL + Docker Compose を使う。
- DB 基盤は `feature/db` で作業し、DB 関連ファイルは `db/` 配下に置く。
- 書籍情報取得元は Google Books API とする。
- 認証はメールアドレス + パスワード方式とする。
- 「有名なユーザー」はフォロワー数順で扱う。
- 書籍分類は固定ジャンル + タグ方式とする。
- 個人のおすすめ紹介は、おすすめリスト方式とする。

## Data to persist

外部 API に毎回依存せず、ランキング・おすすめ・レビュー表示を安定させるため、書籍の基本情報はアプリ DB に保存する。

- 書籍: Google Books volume id、タイトル、説明、出版社、出版日、ISBN、サムネイル画像 URL
- 著者: 著者名と表示順。著者名は DB に保存する。
- 分類: 固定ジャンル、タグ
- ユーザー: email、表示名、プロフィール、フォロー関係
- 認証: パスワードハッシュ、セッション
- 行動データ: お気に入り、レビュー本文、1〜5 の星評価
- おすすめ: ユーザーごとのおすすめリスト、リスト内の書籍、紹介コメント、表示順

## Initial data model

- `users`: email、display_name、password_hash、password_salt、created_at
- `user_profiles`: user_id、bio、avatar_url、headline
- `user_follows`: follower_user_id、followed_user_id
- `sessions`: user_id、token_hash、expires_at、created_at
- `books`: google_volume_id、title、description、publisher、published_date、isbn_10、isbn_13、thumbnail_url、created_at
- `book_authors`: book_id、author_name、display_order
- `genres`: name、slug
- `book_genres`: book_id、genre_id
- `tags`: name、slug
- `book_tags`: book_id、tag_id
- `favorites`: user_id、book_id、created_at
- `reviews`: user_id、book_id、rating、body、created_at、updated_at
- `recommendation_lists`: user_id、title、description、visibility、created_at、updated_at
- `recommendation_list_items`: list_id、book_id、comment、display_order

主な制約:

- `users.email` は unique。
- `books.google_volume_id` は unique。
- `reviews.rating` は 1〜5。
- `favorites` は 1 ユーザー 1 書籍 1 件。
- `reviews` は 1 ユーザー 1 書籍 1 件。

## Initial API scope

- 認証: `POST /api/auth/register`、`POST /api/auth/login`、`POST /api/auth/logout`、`GET /api/auth/me`
- ユーザー: `GET /api/users/{userId}`、`POST /api/users/{userId}/follow`、`DELETE /api/users/{userId}/follow`、`GET /api/users/featured`
- 書籍: `GET /api/books/search?q=...`、`POST /api/books`、`GET /api/books/{bookId}`
- お気に入り: `POST /api/books/{bookId}/favorite`、`DELETE /api/books/{bookId}/favorite`、`GET /api/me/favorites`
- レビュー: `PUT /api/books/{bookId}/review`、`DELETE /api/books/{bookId}/review`、`GET /api/books/{bookId}/reviews`
- ジャンル: `GET /api/genres`、`GET /api/genres/{genreId}/ranking`
- おすすめリスト: `POST /api/me/recommendation-lists`、`PUT /api/me/recommendation-lists/{listId}`、`DELETE /api/me/recommendation-lists/{listId}`、`GET /api/recommendation-lists/{listId}`

## Branch order

1. `feature/db`: MySQL + Docker Compose、DB 初期化、マイグレーション方針、DB ドキュメントを整備する。
2. `feature/api`: `feature/db` merge 後、DB 接続、認証、Google Books API 連携、書籍管理 API を実装する。
3. `feature/front`: API merge 後、Vue.js + Vite で UI を実装する。

各 feature ブランチは GitHub Pull Request で `develop` に merge する。ローカルで `develop` へ merge しない。
