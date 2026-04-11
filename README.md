# try_codex

Java API と Vue.js フロントエンドを同じリポジトリで管理する新規プロジェクトです。

## Repository layout

```text
.
├── api/    # Java API
├── front/  # Vue.js frontend
└── docs/   # Project documentation
```

## Git workflow

このリポジトリは次のブランチ運用を前提にします。

- `main`: 安定版
- `develop`: 統合ブランチ
- `feature/*`: 機能開発ブランチ

機能開発は `develop` から `feature/*` を作成し、GitHub Pull Request で `develop` に merge します。

## Stack

- API: Java + Jersey + Google Guice
- Frontend: Vue.js + Vite + npm

## Development order

1. `main` で初回コミットを作成する。
2. `develop` を作成する。
3. `develop` から `feature/api` を作成し、API を実装する。
4. `feature/api` から `develop` 向けに GitHub Pull Request を作成する。
5. PR merge 後、別セッションで `develop` から `feature/front` を作成してフロントエンドを実装する。
