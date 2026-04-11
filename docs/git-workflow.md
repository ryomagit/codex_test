# Git workflow

## Branches

- `main`: 安定版を置くブランチです。
- `develop`: 開発中の変更を統合するブランチです。
- `feature/*`: 機能単位の作業ブランチです。

## Basic flow

```bash
git switch develop
git pull
git switch -c feature/<name>
```

作業後は GitHub Pull Request を作成し、`feature/<name>` から `develop` に merge します。
ローカルで PR merge 相当の merge は行わず、GitHub 上の PR merge を正とします。

## Project startup order

1. `main` で初回コミットを作成する。
2. `develop` を作成する。
3. `feature/api` で API を実装し、`develop` に PR / merge する。
4. `feature/front` でフロントエンドを実装し、`develop` に PR / merge する。

## Session boundaries

- Session 1: `main` で Git 初期化と初回コミット。
- Session 2: `develop` 作成と `feature/api` 実装、PR 作成準備。
- Session 3: GitHub 上で `feature/api` が merge 済みになった後、`develop` を pull して確認。
- Session 4: 別セッションで `develop` から `feature/front` を作成し、フロントエンドを実装。

## Important rule

`feature/api` 完了後は、ローカルで `develop` に merge しません。
GitHub 上で `feature/api` から `develop` への Pull Request を作成し、PR merge 後に次セッションで `develop` を最新化します。
