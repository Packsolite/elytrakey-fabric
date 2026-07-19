---
name: update-minecraft
description: Update this Fabric mod to the latest Minecraft, Fabric Loader, Fabric API, and Loom versions
---
## What this does

Fetches the latest versions from Fabric's metadata APIs, updates `gradle.properties` and `fabric.mod.json`, then builds and runs the client to verify.

## Instructions

1. Fetch **game version** from `https://meta.fabricmc.net/v2/versions/game` — use the first entry
2. Fetch **loader version** from `https://meta.fabricmc.net/v2/versions/loader` — use the first `"stable": true` entry
3. Fetch **Fabric API** from `https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml` — use the `<latest>` tag
4. Fetch **Loom** from `https://maven.fabricmc.net/net/fabricmc/fabric-loom/maven-metadata.xml` — use the `<release>` tag
5. Update `gradle.properties` with these values
6. Update `fabric.mod.json`'s `"minecraft"` dependency to `~<version>`
7. Run `.\gradlew build --no-daemon`, fix compilation errors until it succeeds
8. Run `.\gradlew runClient --no-daemon` with a 60s timeout. If it fails with a version mismatch, parse the actual version from the error, fix `fabric.mod.json`, and re-run
9. Summarize version changes, files modified, and whether build/runClient succeeded

Do not modify unrelated code.
