# Learning guide

このファイルは、人間が今回のプロジェクトで採用した要件や技術選定を学ぶための説明です。実装の正はコードと他 docs ですが、ここでは「なぜそうしているか」を中心にまとめます。

## 全体像

このプロジェクトは、技術書を探す、保存する、レビューする、他ユーザーのおすすめを見る、という一連の体験を作るアプリです。

外部の Google Books API は検索に使いますが、アプリ内のランキング、レビュー、おすすめ、タグ分類は自前の DB に保存します。これは外部 API だけに頼ると、ユーザー固有の行動データやアプリ独自の分類を安定して扱えないためです。

## Java 17+、Jersey、Guice、Grizzly

API は Spring Boot ではなく、Jersey + Guice + Grizzly で構成しています。

- Jersey は JAX-RS 系の考え方で HTTP endpoint を Resource として書けるため、API の入口を薄く保ちやすい。
- Guice は必要な service や helper を注入するために使い、`new` が散らばる構成を避ける。
- Grizzly は Jersey アプリケーションを軽量に起動する HTTP server として使う。
- Java 17+ は現在の Java 開発で扱いやすい LTS 世代で、record なども使える。

この構成を採用すると、フレームワークが全部を自動で隠すよりも、HTTP server、Resource、DI、永続化の境界を学びやすくなります。一方で、Spring Boot のような自動設定は少ないので、`AppConfig` や `ApiModule` の責務を理解しておく必要があります。

## EclipseLink JPA と QueryDSL

DB アクセスは EclipseLink JPA を基本にしています。JPA は Java object と DB table の対応を entity として表現し、Repository から EntityManager 経由で読み書きします。

このプロジェクトでは、単純な保存・取得は JPA / JPQL で十分です。たとえば書籍保存、お気に入り追加、レビュー更新などは、どの table をどう更新するかが比較的明確です。

一方でランキングやおすすめ一覧のように、join、group by、count、avg、order by が絡む処理は QueryDSL を使います。QueryDSL は生成された `Q*` class を使うため、文字列の SQL / JPQL より型の補助を受けやすく、複雑なクエリを Java コードとして追いやすくなります。

## MySQL + Docker Compose

DB は MySQL を Docker Compose で起動します。

- 開発者ごとにローカル MySQL を手動構築しなくてよい。
- `db/init/` の SQL で初期スキーマと初期データを再現できる。
- API のデフォルト DB 接続情報と compose の設定を合わせやすい。

注意点として、`docker-entrypoint-initdb.d` の SQL は volume 初期作成時だけ実行されます。既存 DB にスキーマ変更を入れる段階では、別途マイグレーション方針を決める必要があります。

## Google Books API とアプリ DB

Google Books API は書籍を探す入口です。検索結果にはタイトル、著者、説明、出版社、出版日、ISBN、サムネイル画像 URL などが含まれます。

ただし、検索結果をそのまま使うだけでは次のことができません。

- 自分のお気に入りとして保存する。
- 自分のレビューや星評価を紐づける。
- アプリ独自の固定ジャンルやタグを付ける。
- おすすめリスト内の表示順や紹介コメントを持つ。
- フォロワー数やレビュー数を使ったランキングを作る。

そのため、ユーザーが保存操作を行った書籍はアプリ DB に保存します。外部 API は発見、アプリ DB は管理と学習ログ、という役割分担です。

## Cookie セッション認証

認証はメールアドレス + パスワード方式です。パスワードはハッシュと salt を DB に保存し、ログイン時にはセッション token を発行します。

セッションは token そのものではなく token hash を DB に保存します。ブラウザには Cookie を返し、以後の API 呼び出しでログイン中ユーザーを特定します。

この方式は、フロントエンドが token を JavaScript の状態として直接持ち回る方式より、まずは Web アプリの基本的なログインの流れを学びやすいです。開発時は Vite proxy を使い、同じ origin から API を呼ぶ形にして Cookie を扱いやすくしています。

## Vue.js + Vite

フロントエンドは Vue.js + Vite です。

- Vue は template、script、style の関係を追いやすく、状態に応じた画面更新を学びやすい。
- Vite は開発サーバーが軽く、proxy 設定も簡潔。
- 現在は `App.vue` に主要機能を集約し、まず動く画面として作っている。

今後機能が増える場合は、API 呼び出し、フォーム、書籍カード、レビュー、ユーザー表示などを component / composable に分けていくと読みやすくなります。

## このプロジェクトで学ぶとよい順番

1. `docs/book-app-requirements.md` でアプリの目的と要件を見る。
2. `docs/architecture.md` で全体の流れを見る。
3. `db/init/001_schema.sql` で保存するデータの形を見る。
4. `api/src/main/java/com/example/api/AppConfig.java` で Jersey と Guice のつなぎ方を見る。
5. `api/src/main/java/com/example/api/resource/BookResource.java` から Resource -> Service -> Repository の流れを追う。
6. `front/src/App.vue` で画面から API をどう呼ぶかを見る。
7. `docs/task-list.md` で次に改善する場所を選ぶ。
