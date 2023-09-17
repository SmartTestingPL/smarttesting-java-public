#!/usr/bin/env bash

WRAPPER_VERSION="8.2.1"

shopt -s nullglob globstar
dir="$( pwd )"
for i in "$dir"/**/gradlew
do
    folderName="$(dirname "$i")/"
    echo "Updating Gradle in folder [${folderName}]"
    pushd "${folderName}"
        ./gradlew wrapper --gradle-version "${WRAPPER_VERSION}"
    popd
done
