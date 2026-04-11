#!/bin/sh
set -eu

cd "$(dirname "$0")/.."

if [ ! -x ./gradlew ]; then
  echo "error: ./gradlew is required. Run this script from a checkout that includes the Gradle Wrapper." >&2
  exit 1
fi

exec ./gradlew run
