# try_codex

Java API、MySQL DB、Vue.js フロントエンドを同じリポジトリで管理するプログラマー向け書籍管理アプリです。

## Repository layout

```text
.
├── api/    # Java + Jersey + Google Guice API
├── db/     # MySQL + Docker Compose database
├── front/  # Vue.js + Vite frontend
└── docs/   # Project documentation
```

## Stack

- API: Java 17+、Jersey、Google Guice、Grizzly、EclipseLink JPA、QueryDSL
- DB: MySQL 8.4、Docker Compose
- Frontend: Vue.js 3、Vite、npm
- External API: Google Books API

## Quick start

### Requirements

- Java 17+
- Docker / Docker Compose
- Node.js + npm
- Git

ローカルに `gradle` を入れていても、このプロジェクトでは API の実行とテストに `api/gradlew` を使います。

### 1. Clone and switch to develop

```bash
git clone git@github.com:ryomagit/codex_test.git
cd codex_test
git fetch origin
git switch develop
git pull --ff-only origin develop
```

HTTPS で clone する場合:

```bash
git clone https://github.com/ryomagit/codex_test.git
cd codex_test
git fetch origin
git switch develop
git pull --ff-only origin develop
```

### 2. Start the database

```bash
cd db
docker compose up -d
```

初回起動時に `db/init/` 配下の SQL が実行され、`book_app` データベースに schema と初期 genre が作成されます。

DB 接続情報:

- Host: `127.0.0.1`
- Port: `3306`
- Database: `book_app`
- User: `book_app`
- Password: `book_app_password`

### 3. Start the API

別ターミナルで実行します。

```bash
cd api
./gradlew run
```

API はデフォルトで `http://localhost:8080` に起動します。DB 接続設定は `DB_HOST`、`DB_PORT`、`DB_NAME`、`DB_USER`、`DB_PASSWORD`、`DB_JDBC_URL` で上書きできます。

### 4. Start the frontend

さらに別ターミナルで実行します。

```bash
cd front
npm install
npm run dev
```

Vite dev server が表示する localhost URL をブラウザで開きます。開発時は Vite proxy により、フロントエンドからの `/api` と `/health` が `http://localhost:8080` に転送されます。

## Verify setup

API の疎通確認:

```bash
curl http://localhost:8080/health
```

期待する response:

```json
{"status":"ok"}
```

ブラウザでは、`npm run dev` が表示する Vite の URL を開きます。ログイン、書籍検索、保存、レビューなどの操作には API と DB が起動している必要があります。

## Stop and reset

API と frontend は、それぞれ起動中の terminal で停止します。

DB を停止する場合:

```bash
cd db
docker compose down
```

DB データも含めて初期化する場合:

```bash
cd db
docker compose down -v
```

`down -v` 後に再度 `docker compose up -d` すると、`db/init/` の SQL が再実行されます。

## Useful checks

API のテスト:

```bash
cd api
./gradlew test
```

API の Java format 確認:

```bash
cd api
./gradlew spotlessCheck
```

Frontend の build 確認:

```bash
cd front
npm run build
```

## Documentation

- `docs/architecture.md`: API / DB / frontend の全体構成。
- `docs/task-list.md`: 完了状況と次の作業候補。
- `docs/learning-guide.md`: 技術選定や要件を学ぶための説明。
- `docs/api-handoff.md`: API 構成を維持するための引き継ぎ。
- `docs/frontend-handoff.md`: フロントエンド構成の引き継ぎ。
- `docs/git-workflow.md`: ブランチ運用。

## Git workflow

このリポジトリは次のブランチ運用を前提にします。

- `main`: 安定版
- `develop`: 統合ブランチ
- `feature/*` または `codex/*`: 機能・作業単位のブランチ

作業は `develop` からブランチを作成し、GitHub Pull Request で `develop` に merge します。ローカルで PR merge 相当の merge は行いません。
