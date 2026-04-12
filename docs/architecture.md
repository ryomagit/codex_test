# Architecture

プログラマー向け書籍管理アプリの現在の構成です。実装詳細を調べる入口として使います。

## Overview

```text
Browser
  -> Vue + Vite dev server
  -> Vite proxy /api, /health
  -> Grizzly + Jersey API
  -> Resource -> Service -> Repository
  -> EclipseLink JPA / QueryDSL
  -> MySQL

Book search
  -> BookResource
  -> GoogleBooksClient
  -> Google Books API
```

## API

- `Main` が `HOST` と `PORT` を読み、Grizzly 上で Jersey の `ResourceConfig` を起動する。
- `AppConfig` が Jersey Resource、Jackson、例外 mapper、service binding を登録する。
- `ApiModule` が Guice の依存注入を担当し、`EntityManagerFactory`、`ObjectMapper`、認証系 helper を singleton として扱う。
- Resource は HTTP の入出力を受け、Service は認証・権限・バリデーションを含むユースケースをまとめ、Repository は永続化と QueryDSL クエリを担当する。
- `ApiExceptionMapper` と `GenericExceptionMapper` が API エラーを JSON response に変換する。

主な Resource:

- `AuthResource`: 登録、ログイン、ログアウト、ログイン中ユーザー取得。
- `BookResource`: Google Books 検索、書籍保存、書籍詳細、お気に入り、レビュー。
- `GenreResource`: ジャンル一覧、ジャンルランキング。
- `MeResource`: 自分のお気に入り、おすすめリスト作成・更新・削除。
- `RecommendationListResource`: おすすめリスト詳細。
- `UserResource`: ユーザー詳細、フォロー、フォロー解除、有名ユーザー、おすすめリスト一覧。

## Persistence

- DB は `db/compose.yaml` の MySQL 8.4 を使う。
- 初期スキーマと初期ジャンルは `db/init/` 配下の SQL で作成する。
- JPA persistence unit は `book-app`。schema generation は `none` なので、スキーマの正は DB 初期化 SQL。
- `JpaExecutor` が EntityManager を作成し、read/write transaction の共通処理を持つ。
- 単純な検索や更新は JPA / JPQL を使い、ランキングやおすすめリスト一覧など集計・結合・並び替えが重要な箇所で QueryDSL を使う。

## Frontend

- フロントエンドは `front/` 配下の Vue + Vite アプリ。
- `front/src/App.vue` が現在の主画面をまとめている。
- 開発時は Vite proxy が `/api` と `/health` を `http://localhost:8080` に転送する。
- Cookie セッションは同一 origin proxy を通して扱うため、フロントエンドと API を両方起動して確認する。

## External integration

- 書籍検索は `GoogleBooksClient` が `https://www.googleapis.com/books/v1/volumes` を呼び出す。
- 検索結果はアプリ DB に自動保存されない。ユーザーが画面から保存操作を行うと、Google volume id と書誌情報が `books` 系テーブルへ保存または更新される。
- 保存時に固定ジャンルとタグを紐づけることで、ランキングやタグ検索導線に使えるアプリ内データへ変換する。
