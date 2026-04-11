#!/bin/sh
set -eu

cd "$(dirname "$0")/.."

rm -rf build/classes
mkdir -p build/classes

javac -d build/classes $(find src/main/java -name '*.java')
java -cp build/classes com.example.api.Main

