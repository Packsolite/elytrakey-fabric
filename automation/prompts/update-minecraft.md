Update this Fabric mod to the latest (snapshot) versions from https://fabricmc.net/develop/.

Use the latest values for:

* minecraft_version
* loader_version
* loom_version
* fabric_api_version

Update `gradle.properties` with these values. Also update `fabric.mod.json` with the same `minecraft_version` from `gradle.properties`.

**IMPORTANT: The minecraft version in `gradle.properties` (e.g. `26.3-snapshot-1`) may differ from the actual runtime version of the game (e.g. `26.3-alpha.1`). You must verify and correct this.**

Then run:

```sh
./gradlew build
```

If the build fails, fix the code until it succeeds. Update imports, Fabric API usage, and other Minecraft API changes as necessary. Make only the minimal changes required for compatibility.

**After `build` succeeds, verify the mod actually loads at runtime:**

```sh
./gradlew runClient
```

Terminate the process after a few seconds once it's clear the game started (look for "Setting user:" or the game window appearing).

- If `runClient` fails with a mod resolution error mentioning an incompatible Minecraft version, parse the actual Minecraft version from the error message (e.g. `26.3-alpha.1`) and update the `"minecraft"` dependency in `fabric.mod.json` to match that actual version. Then re-run `runClient` to confirm it starts.
- If `runClient` fails for any other reason, fix the issue and re-run until it succeeds.

Do not modify unrelated code or introduce new dependencies unless required by the Fabric update.

When finished, summarize:

* the version changes
* the files modified
* any code changes made
* whether the build and runtime verification succeeded
