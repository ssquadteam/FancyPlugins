#!/usr/bin/env bash
set -euo pipefail

VERSION="26.1-pre-1"
PATCH_DIR="../strata-sources/${VERSION}/patches/features"
SOURCE_DIR="../strata-sources/${VERSION}/src/main/java"

# remove old patches
find "$PATCH_DIR" -name '*.patch' -delete

echo "Rebuilding feature patches ..."

git -C "$SOURCE_DIR" format-patch \
    strata/file-patches..HEAD \
    --no-signature \
    --subject-prefix="" \
    --output-directory ../../../patches/features \
    > /dev/null

echo "Successfully rebuilt feature patches"