# Task list

現時点のプロジェクトを引き継ぐためのタスクリストです。完了済みの大枠は `develop` に取り込み済みです。

## Done

- Java 17+、Jersey、Google Guice、Grizzly の API 基盤。
- MySQL + Docker Compose の DB 基盤。
- EclipseLink JPA entity、Repository、QueryDSL 生成設定。
- Google Books API 連携。
- メールアドレス + パスワード認証、DB 保存セッション、Cookie 認証。
- 書籍検索、保存、詳細、お気に入り、レビュー、ジャンルランキング、おすすめリスト、ユーザー、フォロー API。
- Vue.js + Vite の単一画面フロントエンド。

## Next documentation tasks

- README と docs の導線を揃え、最初に読む順番を明確にする。
- API request / response のサンプルを増やす。
- 画面操作手順と API 確認手順を一つの開発者向けチェックリストにまとめる。

## Next API tasks

- API の統合テストを増やし、認証 Cookie を含む主要フローを自動化する。
- Google Books API 失敗時や検索結果欠落時の挙動をテストで固定する。
- おすすめリストの visibility、所有者権限、削除時の cascade を重点的に確認する。
- QueryDSL を使うランキング・一覧取得の並び順をテストで固定する。

## Next frontend tasks

- `App.vue` に集まっている状態管理と API 呼び出しを、必要に応じて composable / component に分割する。
- おすすめリストの作成・編集 UI が API scope と一致しているか確認する。
- 入力エラー、未ログイン時、空結果、API 停止時の表示を改善する。
- `npm run build` を CI または手元検証の標準手順に入れる。

## Next DB tasks

- 既存 DB へ後続変更を入れるためのマイグレーション方針を決める。
- 初期データを増やす場合は `db/init/NNN_name.sql` の連番で追加する。
- 本番相当環境を考える段階で、DB パスワードや接続情報を環境変数・secret 管理へ寄せる。

## Verification checklist

- `cd db && docker compose up -d`
- `cd api && ./gradlew test`
- `cd api && ./gradlew spotlessCheck`
- `cd api && ./gradlew run`
- `curl http://localhost:8080/health`
- `cd front && npm install`
- `cd front && npm run build`
- `cd front && npm run dev`
