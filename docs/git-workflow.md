# Git workflow

## Branches

- `main`: 安定版を置くブランチ。
- `develop`: 開発中の変更を統合するブランチ。
- `feature/*` または `codex/*`: 機能・作業単位のブランチ。

## Basic flow

```bash
git fetch origin
git switch -c codex/<task-name> origin/develop
```

作業後は GitHub Pull Request を作成し、作業ブランチから `develop` に merge する。ローカルで PR merge 相当の merge は行わず、GitHub 上の PR merge を正とする。

`develop` が別 worktree で checkout 済みの場合は、無理に `git switch develop` せず、`origin/develop` を起点に作業ブランチを直接作る。

## Completed project milestones

1. `main` で初回コミットを作成済み。
2. `develop` を作成済み。
3. 最小 API 基盤を GitHub PR 経由で `develop` に merge 済み。
4. MySQL + Docker Compose の DB 基盤を GitHub PR 経由で `develop` に merge 済み。
5. EclipseLink JPA を使う書籍管理 API を GitHub PR 経由で `develop` に merge 済み。
6. Vue.js + Vite フロントエンドを GitHub PR 経由で `develop` に merge 済み。

## Current docs flow

docs 整備のような横断的な変更も、直接 `develop` へ commit しない。

```bash
git fetch origin
git switch -c codex/docs-project-overview origin/develop
```

変更後は差分確認と必要な検証を行い、Pull Request で `develop` へ提案する。

## Important rule

`feature/api`、`feature/db`、書籍管理 API 用ブランチ、`feature/front`、`codex/*` はローカルで `develop` に merge しない。GitHub 上の Pull Request merge を正とする。
