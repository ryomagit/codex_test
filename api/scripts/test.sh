#!/bin/sh
set -eu

cd "$(dirname "$0")/.."

if [ -x ./gradlew ]; then
  GRADLE=./gradlew
else
  GRADLE=gradle
fi

exec "$GRADLE" test
