# Frontend handoff

`feature/front` は GitHub 上で `feature/api` が `develop` に merge された後、別セッションで開始します。

## Branch start

```bash
git switch develop
git pull
git switch -c feature/front
```

ローカルで `feature/api` を `develop` に merge しません。GitHub の Pull Request merge 後の `develop` を起点にします。

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

- API health の表示
- `/api/hello` の結果表示
- 読み込み中、成功、失敗の状態表示

