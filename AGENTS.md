# AGENTS.md

Fabric client mod (ElytraKey) — single Gradle module, Fabric Loom, **Java 25** toolchain required.

## Commands

- Build: `./gradlew build --no-daemon`
- Dev client: `./gradlew runClient --no-daemon`
- There are **no tests**; verification = successful build + client starting.

## Version bumps

All versions live in `gradle.properties` (`minecraft_version`, `loader_version`, `loom_version`, `fabric_api_version`,
`modmenu_version`). The `minecraft` dependency in `src/main/resources/fabric.mod.json` must be updated in sync (format:
`~<minecraft_version>`).

Use the repo-local skill instead of doing this by hand: `opencode update-minecraft` (see
`.agents/skills/update-minecraft/SKILL.md`). Its verification flow: `./gradlew build --no-daemon`, then
`./gradlew runClient --no-daemon` limited to ~20s; on a version-mismatch error, parse the expected version from the
error and fix `fabric.mod.json`.

## Conventions

- All commits MUST use a gitmoji prefix (e.g., `:sparkles: Add feature`, `:bug: Fix bug`);
  see [gitmoji.dev](https://gitmoji.dev).
- Tabs for indentation (size 4), LF line endings — enforced by `.editorconfig`.
- ModMenu is a `compileOnly` dependency (optional at runtime); don't make it required.
- `mod_version` in `gradle.properties` is expanded into `fabric.mod.json` at build time via `processResources` — don't
  hardcode a version there.
- Built jar name includes the MC version: `elytrakey-fabric-mc<version>-<mod_version>.jar`.

## Structure

- Entrypoint: `eu.packsolite.elytrakey.ElytraKey` (`ModInitializer`, registered in `fabric.mod.json`); all game logic
  lives in this one class.
- `options/` — config model + JSON loader; `ui/` — options screen; `ModMenuIntegration` — optional ModMenu hook.
- `elytrakey.mixins.json` exists but defines no mixins; add mixin classes under `eu.packsolite.elytrakey.mixin` if ever
  needed.
