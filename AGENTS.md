# AGENTS.md

このリポジトリで作業するエージェント向けの必須ルールです。

## Communication

- 応答は必ず日本語で行う。

## Required reading

作業開始時に、必要に応じて以下を確認する。

- `docs/book-app-requirements.md`
- `docs/api-handoff.md`
- `docs/frontend-handoff.md`
- `docs/git-workflow.md`

## API rules

- API は Java 17+、Jersey、Google Guice、Grizzly の構成を維持する。
- API の実行とテストは、ローカルの `gradle` コマンドではなく必ず `./gradlew` を使う。
- API の DB アクセスは EclipseLink JPA を使い、複雑な集計・結合クエリでは QueryDSL を使う。
- Java ファイル編集後は `cd api && ./gradlew spotlessApply` で google-java-format を適用する。
- Spring Boot へ置き換えない。
- 素の `com.sun.net.httpserver.HttpServer` 実装へ戻さない。
- Google Guice を外して手動生成だけの構成へ戻さない。

## Git workflow

- DB 基盤は `feature/db` で作業し、DB 関連ファイルは `db/` 配下に置く。
- DB は MySQL + Docker Compose を前提にする。
- 書籍管理 API は `feature/db` が GitHub 上で `develop` に merge された後に実装する。
- `feature/front` は、書籍管理 API が GitHub 上で `develop` に merge された後、別セッションで開始する。
- 各 feature ブランチ完了後に、ローカルで `develop` へ merge しない。
- GitHub Pull Request で `feature/*` から `develop` に merge する。
