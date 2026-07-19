---
name: update-minecraft
description: Update this Fabric mod to the latest Minecraft, Fabric Loader, Fabric API, and Loom versions
---

Update this Fabric mod to the latest minecraft and fabric versions. Fetch them from:

- **Game:** `https://meta.fabricmc.net/v2/versions/game` — first entry is the latest
- **Loader:** `https://meta.fabricmc.net/v2/versions/loader` — first `"stable": true` entry
- **Fabric API:** `https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml` — `<latest>` tag
- **Loom:** `https://maven.fabricmc.net/net/fabricmc/fabric-loom/maven-metadata.xml` — `<release>` tag

Update `gradle.properties` with these values. Update `src/main/resources/fabric.mod.json`'s "minecraft" dependency to
`~<minecraft_version>`.

Then:

1. `./gradlew build --no-daemon` — fix compilation errors until it succeeds.
2. `./gradlew runClient --no-daemon` — limit command to 20s so it auto-terminates. If it fails with a version mismatch,
   parse the actual version from the error, fix `fabric.mod.json`, and re-run.
3. If it succeeds, you're done.

Do not modify unrelated code. Keep the diff minimal.

Summarize version changes, files modified, and whether both build and runClient succeeded.
