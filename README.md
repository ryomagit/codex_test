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

## Planned stack

- API: Java
- Frontend: Vue.js + Vite + npm

