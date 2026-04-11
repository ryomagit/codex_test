# DB

MySQL + Docker Compose による書籍管理アプリのDB基盤です。

## 起動

```bash
cd db
docker compose up -d
```

初回起動時に `init/` 配下のSQLが実行され、`book_app` データベースにスキーマと初期ジャンルが作成されます。

## 接続情報

- Host: `127.0.0.1`
- Port: `3306`
- Database: `book_app`
- User: `book_app`
- Password: `book_app_password`
- Root password: `root_password`

## 停止

```bash
cd db
docker compose down
```

データを含めて初期化する場合:

```bash
cd db
docker compose down -v
```

## マイグレーション方針

- DB関連ファイルは `db/` 配下に置きます。
- 初期化SQLは `db/init/NNN_name.sql` の連番で追加します。
- `docker-entrypoint-initdb.d` のSQLはボリューム初期作成時のみ実行されます。既存DBへ後続変更を反映する場合は、API実装ブランチで正式なマイグレーションツール導入を検討します。
- APIレスポンス定義はDB基盤に含めません。ここでは永続化対象と制約のみを扱います。
