#!/bin/sh
set -eu

cd "$(dirname "$0")/.."

rm -rf build/test-classes
mkdir -p build/test-classes

javac -d build/test-classes $(find src/main/java src/test/java -name '*.java')
java -cp build/test-classes com.example.api.JsonResponsesTest

