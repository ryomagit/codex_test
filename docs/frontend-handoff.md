# Frontend handoff

`feature/front` は GitHub 上で書籍管理 API が `develop` に merge された後、別セッションで開始します。
作業開始時に `docs/api-handoff.md` を読み、API 側の Jersey + Google Guice 構成を変更しないでください。
あわせて `docs/book-app-requirements.md` を確認し、プログラマー向け書籍管理ツールとしての要件を維持してください。

## Branch start

```bash
git switch develop
git pull
git switch -c feature/front
```

ローカルで API ブランチや DB ブランチを `develop` に merge しません。GitHub の Pull Request merge 後の `develop` を起点にします。

## API contract

API はデフォルトで `http://localhost:8080` に起動します。

### GET /health

Response:

```json
{"status":"ok"}
```

### GET /api/hello

Response:

```json
{"message":"Hello from Java API"}
```

## Vite proxy

Vue + Vite 側では、開発時に API へ proxy します。

```js
export default {
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
      '/health': 'http://localhost:8080',
    },
  },
}
```

## Frontend scope

`feature/front` では以下を実装します。

- ログイン、ログアウト、ログイン中ユーザー表示
- Google Books API 経由の書籍検索結果表示
- 書籍詳細、著者名、画像、ISBN、説明、出版社、出版日の表示
- お気に入り追加、削除、一覧表示
- レビュー本文と 1〜5 の星評価の登録、更新、削除、一覧表示
- Java などの固定ジャンル別ランキング表示
- タグによる書籍発見導線
- ユーザープロフィール、フォロー、有名ユーザー一覧
- ユーザーごとのおすすめ書籍リスト表示、作成、編集
- 読み込み中、成功、失敗の状態表示
