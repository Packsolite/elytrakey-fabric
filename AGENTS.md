# AGENTS.md

Fabric client mod (ElytraKey) — single Gradle module, Fabric Loom, **Java 25** toolchain required.

## Commands

- Build: `./gradlew build --no-daemon`
- Dev client: `./gradlew runClient --no-daemon`
- There are **no tests**; verification = successful build + client starting.

## Version bumps

All versions live in `gradle.properties`. The `minecraft` dependency in `fabric.mod.json` uses `alpha-<build>` where the
gradle property uses `snapshot-<build>`. Derive the correct version from `fabric_api_version`'s suffix or from build
error output.

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
- Lombok is a `compileOnly` + `annotationProcessor` dependency; use `@Slf4j(topic = MOD_ID)` for logging,
  `@With` for immutable mutation on records, and `@Getter`/`@Setter` for encapsulated fields.

## Publishing

Tag `v<mod_version>-mc<minecraft_version>` and push — the workflow builds,
creates a GitHub Release with auto-generated changelog, and publishes to Modrinth.

## Structure

- Entrypoint: `eu.packsolite.elytrakey.ElytraKey` (`ModInitializer`, registered in `fabric.mod.json`); handles
  initialization, keybind registration, tick orchestration, and creates the feature instances.
- `feature/` — `AutoSwapFeature` (auto-equip/unequip state, owns `wasAutoEquipped`) and `EasyTakeoffFeature`
  (firework boost state machine, owns `startFlying`/`boostNextTick`). Each has an `update()` called from
  `ElytraKey.tick()`.
- `util/InventoryHelper` — all inventory operations (equip/swap/search/score chestplates).
- `options/` — `ConfigModel` (`@With` record, immutable) + `ConfigLoader` (Gson, returns/takes `ConfigModel` directly).
- `ui/` — `ElytraKeyOptions` (options screen, mutates config via `withX()`).
- `ModConstants` — shared constants (`MOD_ID`).
- `integration/ModMenuIntegration` — optional ModMenu hook.
- `elytrakey.mixins.json` exists but defines no mixins; add mixin classes under `eu.packsolite.elytrakey.mixin` if ever
  needed.

### Chestplate scoring

`InventoryHelper.scoreChestplate(ItemStack)` evaluates chestplates by reading their baked-in attribute modifiers
(`DataComponents.ATTRIBUTE_MODIFIERS`) and enchantments (`DataComponents.ENCHANTMENTS`). Score formula:
`armor × 1000 + toughness × 100 + totalEnchantmentLevels`. This replaces the old hardcoded priority list.
