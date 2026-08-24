#!/bin/bash
set -euo pipefail

if [[ -z "$2" ]] ; then
    echo Usage: $(basename "$0") TEST-CLASS MODE VARIANT?
    exit 1
fi

CLASS="$1"
ARGS="$2 ${3-}"

OUT=__out
DIR="$(dirname "$0")"
LIB="$DIR/graal"

rm -rf "$OUT"

javac \
    -encoding utf-8 \
    -d "$OUT" \
    "--class-path=$LIB/*:$DIR/../common:$DIR" \
    "$DIR/${CLASS//\.//}.java" \
  && java -ea \
    --enable-native-access=org.graalvm.truffle \
    -Dsun.misc.unsafe.memory.access=allow \
    "--module-path=$LIB" \
    "--class-path=$OUT" \
    "$CLASS" $ARGS
