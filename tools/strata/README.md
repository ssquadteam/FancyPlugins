# Strata

Strata is a tool to download and view the Minecraft server source code.
You can also apply patches to the source code and try to compile it.

## Patch system

### File patches

Apply file patches:

```bash
$ cd tools/strata/scripts
$ chmod +x applyFilePatches.sh
$ ./applyFilePatches.sh
```

Modify file patches:

1. Apply patches (see above)
2. Modify Minecraft server source code
3. Commit changes (`git commit --amend --no-edit`) while being in `tools/strata/strata-sources/{VERSION}/src/main/java`
4. Recreate patches (`./createFilePatches.sh` while being in `tools/strata/scripts`)