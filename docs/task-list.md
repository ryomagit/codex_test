# Task list

現時点のプロジェクトを引き継ぐためのチェックリストです。完了済み、未完了、一部完了を明示し、次の作業を選びやすくします。

## How to read

- `[x]`: 完了。現在の `develop` に実装または文書として入っている。
- `[ ]`: 未完了。まだ実装、検証、文書化、設計の作業が必要。
- `[~]`: 一部完了。動くものはあるが、改善や追加確認が必要。
- `Next:` は、次に着手するエージェントが最初に行う具体行動。

## Project checklist

### API

- [x] API 基盤: Java 17+、Jersey、Google Guice、Grizzly で起動する。Next: 維持し、Spring Boot や素の `HttpServer` へ戻さない。
- [x] 永続化基盤: EclipseLink JPA entity、Repository、QueryDSL 生成設定がある。Next: 複雑な集計・結合は QueryDSL を使う。
- [x] Google Books API 連携: 書籍検索から外部検索結果を取得できる。Next: 失敗時や欠落データの挙動をテストに追加する。
- [x] 認証: メールアドレス + パスワード、DB 保存セッション、HttpOnly Cookie がある。Next: register / login / logout / me の統合テストを追加する。
- [x] 書籍 API: 検索、保存、詳細、お気に入り、レビュー、ジャンルランキングがある。Next: 入力 validation とランキング順の回帰テストを増やす。
- [x] ユーザー API: ユーザー詳細、フォロー、有名ユーザー、おすすめリストがある。Next: visibility、所有者権限、cascade 削除をテストで固定する。
- [ ] API examples: README または docs に request / response 例が不足している。Next: 主要 endpoint の curl 例を追加する。

### DB

- [x] DB 基盤: MySQL + Docker Compose、初期 schema SQL、初期 genre seed がある。Next: `db/` 配下に DB 関連ファイルを置き続ける。
- [x] 初期化 flow: 初回 `docker compose up -d` で `db/init/` の SQL が実行される。Next: README のセットアップ手順と矛盾しないよう維持する。
- [ ] Migration 方針: 既存 DB へ後続変更を入れる正式な migration 方針が未決定。Next: schema 変更が必要になった時点で migration tool または SQL 運用を決める。
- [ ] Secret 管理: 本番相当の DB password / connection 管理は未整理。Next: 本番運用を考える段階で secret 管理方針を追加する。

### Frontend

- [x] 初期フロント: Vue.js + Vite の単一画面フロントエンドがあり、主要 API を呼び出せる。Next: UI 改善ではこの前提を壊さず分割する。
- [x] 認証 UI: ログイン/登録フォームを常設表示から外し、未ログイン時に header から開く認証パネルへ分離した。Next: 未ログイン誘導の文言を必要に応じて調整する。
- [x] 画面構成: `Discover`、`Library`、`People`、`Book Detail` の表示モードへ再構成し、全機能が縦積みで見える状態を解消した。Next: P1 で component / composable 分割を進める。
- [~] 状態表示: 読み込み中、成功、失敗の基本表示はある。Next: 空状態、未ログイン状態、API 停止時、保存前導線を画面ごとに整理する。
- [ ] おすすめリスト UI: API scope にある作成・編集・削除・詳細・ユーザー別一覧と画面導線の一致確認が未完了。Next: 不足 UI を洗い出す。
- [ ] Visual verification: redesign 後の desktop / mobile 表示確認手順が未整備。Next: screenshot または手動 viewport 確認手順を追加する。

### Documentation

- [x] 要件 docs: `docs/book-app-requirements.md` が現在仕様をまとめている。Next: API / UI 変更時に同期する。
- [x] Architecture docs: `docs/architecture.md` が API / DB / frontend の全体構成をまとめている。Next: 構成変更時に同期する。
- [x] Learning guide: `docs/learning-guide.md` が技術選定の学習用説明をまとめている。Next: 採用方針変更時に同期する。
- [~] README setup: README に clone から起動までの手順を追加する PR が別途ある。Next: PR merge 後、この項目を `[x]` に更新する。
- [ ] API examples: API request / response の具体例が不足している。Next: 代表的な認証、検索、保存、レビューの例を追加する。
- [ ] 操作手順: 画面操作と API 確認をつなぐユーザー向け手順が不足している。Next: end-to-end の確認シナリオを docs に追加する。

### Operations

- [ ] CI: API test、Spotless、frontend build を自動実行する CI が未整備。Next: GitHub Actions などの導入を検討する。
- [ ] Deploy: 本番 deploy 手順は未整理。Next: 本番運用の段階で環境変数、DB、frontend 配信方法を決める。
- [ ] Monitoring: 監視・ログ・障害対応手順は未整理。Next: 運用フェーズで別タスク化する。

## Frontend UI improvement checklist

UI 改善は、現在の「ログインなどすべてが同じページにある」状態を解消し、主要導線を見つけやすくすることを目的にします。

- [x] P0 認証 UI を分離する: ログイン/登録をメイン画面の常設フォームから外し、未ログイン時だけ header から開く認証パネルにした。Next: P1 で未ログイン誘導の文脈別表示を整える。
- [x] P0 主要画面を再構成する: `Discover`、`Library`、`Book Detail`、`People` の表示モードへ分けた。Next: P1 で API helper と auth state を分離する。
- [x] P0 初期表示を改善する: 初期表示を検索/発見導線中心にし、詳細・レビュー・お気に入り・ユーザー導線は必要な表示モードで見せる構成にした。Next: P1 で各 list の empty state を洗い出す。
- [ ] P1 component / composable 分割を行う: API 呼び出し、状態管理、書籍カード、レビュー、ユーザー表示、認証 UI を分割する。Next: まず API helper と auth state を分離する。
- [ ] P1 空状態を設計する: 検索結果なし、お気に入りなし、レビューなし、ランキングなしを自然な copy と action で表示する。Next: 各 list の empty state を洗い出す。
- [ ] P1 未ログイン状態を設計する: お気に入り、レビュー、フォローなどログイン必須操作で自然に認証へ誘導する。Next: ログイン必須操作の表示ルールを決める。
- [ ] P1 エラー/成功通知を整理する: API エラーや保存成功がページ全体の上部だけでなく、関連する文脈で理解できるようにする。Next: global notice と local message の役割を分ける。
- [ ] P1 モバイル表示を改善する: フォーム、カード、詳細、ランキング、ユーザー一覧が狭い幅でも読みやすいようにする。Next: mobile viewport で主要画面を確認する。
- [ ] P2 おすすめリスト UI を確認する: API scope の作成・編集・削除・詳細・ユーザー別一覧と画面導線が一致するか確認する。Next: 不足している UI を追加タスク化する。
- [ ] P2 redesign 後の表示確認手順を追加する: desktop / mobile の主要画面を screenshot または手動確認できるようにする。Next: 確認 viewport と対象画面を決める。

## Verification checklist

docs のみを変更した場合:

- [ ] `git diff --check`
- [ ] `sed -n '1,280p' docs/task-list.md`
- [ ] `rg "\\[x\\]|\\[ \\]|\\[~\\]|Frontend UI improvement|P0|App.vue|ログイン/登録" docs/task-list.md`

frontend を変更した場合:

- [ ] `cd front && npm install`
- [ ] `cd front && npm run build`
- [ ] `cd front && npm run dev`
- [ ] desktop / mobile 幅でログイン、検索、書籍詳細、お気に入り、レビュー、ランキング、ユーザー表示を確認する。

API または DB を変更した場合:

- [ ] `cd db && docker compose up -d`
- [ ] `cd api && ./gradlew test`
- [ ] `cd api && ./gradlew spotlessCheck`
- [ ] `cd api && ./gradlew run`
- [ ] `curl http://localhost:8080/health`
