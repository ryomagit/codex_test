# Frontend handoff

Vue.js + Vite フロントエンドの引き継ぎメモです。作業開始時に `docs/api-handoff.md` と `docs/book-app-requirements.md` も確認し、API 側の Jersey + Google Guice 構成を変更しないでください。

## Current stack

- Framework: Vue.js 3
- Build tool: Vite
- Package manager: npm
- Entry point: `front/src/main.js`
- Main UI: `front/src/App.vue`
- Styles: `front/src/styles.css`
- Dev server proxy: `front/vite.config.js`

## Run

```bash
cd front
npm install
npm run dev
```

本番ビルド確認:

```bash
cd front
npm run build
```

プレビュー:

```bash
cd front
npm run preview
```

## API contract

API はデフォルトで `http://localhost:8080` に起動する。

```json
{"status":"ok"}
```

開発時は Vite proxy で API へ転送する。

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

現在の `App.vue` は単一画面で次の機能を扱う。

- ログイン、登録、ログアウト、ログイン中ユーザー表示
- Google Books API 経由の書籍検索結果表示
- 検索結果の保存、保存時の固定ジャンルとタグ指定
- 書籍詳細、著者名、画像、ISBN、説明、出版社、出版日の表示
- お気に入り追加、削除、一覧表示
- レビュー本文と 1〜5 の星評価の登録、更新、削除、一覧表示
- 固定ジャンル別ランキング表示
- タグクリックによる検索
- ユーザープロフィール、フォロー、フォロー解除、フォロワー数順ユーザー一覧
- 読み込み中、成功、失敗の状態表示

## Notes

- API 呼び出しは `fetch` を直接使い、`Content-Type: application/json` を既定で付与する。
- Cookie 認証を同一 origin proxy 経由で扱うため、開発時は Vite dev server と API server を同時に起動する。
- 画面はまず実用 UI として作られており、ランディングページや説明ページではない。
- 今後画面を分割する場合も、API 契約と docs の要件を先に確認してから component / composable へ分ける。
