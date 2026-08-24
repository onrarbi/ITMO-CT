#!/bin/bash
javac \
    -encoding utf-8 \
    -d __out \
    RunJS.java \
  && java -ea \
    --enable-native-access=org.graalvm.truffle \
    -Dsun.misc.unsafe.memory.access=allow \
    --module-path=graal \
    --class-path __out \
    RunJS $@
