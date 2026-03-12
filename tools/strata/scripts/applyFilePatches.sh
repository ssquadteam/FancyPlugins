#!/usr/bin/env bash
set -euo pipefail

VERSION="26.1-pre-1"
PATCH_DIR="../strata-sources/${VERSION}/patches/files"
SOURCE_DIR="../strata-sources/${VERSION}/src/main/java"

echo "Applying patches from $PATCH_DIR"
echo "Source repo: $SOURCE_DIR"

REAL_PATCH_DIR="$(realpath "$PATCH_DIR")"
REAL_SOURCE_DIR="$(realpath "$SOURCE_DIR")"

git -C "$SOURCE_DIR" reset --hard strata/decompiled-sources

find "$REAL_PATCH_DIR" -name '*.patch' | sort | while read -r patch; do
    echo "Applying $patch"
    git -C "$SOURCE_DIR" apply --3way "$patch"

done

git -C "$SOURCE_DIR" commit -am "Apply file patches"
git tag strata/file-patches

echo "All patches applied."