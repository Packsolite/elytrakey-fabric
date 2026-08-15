# ElytraKey Mod

[![Modrinth](https://img.shields.io/badge/Modrinth-ElytraKey-1bd96a?logo=modrinth&logoColor=white)](https://modrinth.com/mod/elytrakey)
[![Fabric](https://img.shields.io/badge/Loader-Fabric-db2f2f)](https://fabricmc.net/)

ElytraKey is a small, client sided Fabric mod focused on making the Elytra more enjoyable through keybinds and
lightweight automation.

## Features

- Keybind to swap between Elytra and chestplate (Default: R)
- Optional auto-equip and auto-unequip on configurable events
- Easy take-off support using fireworks

⚠️ **Note**: This mod may be considered an unfair advantage and may not be allowed on some multiplayer servers. Always
refer to the server rules.

## Installation

1. Install **Fabric Loader** and **Fabric API**
2. Download ElytraKey from:
	- **Modrinth:** https://modrinth.com/mod/elytrakey
	- or **GitHub Releases**
3. Place the `.jar` file in your `mods` folder

## Building from Source

```bash
./gradlew build
```

### Or run in dev mode:

```bash
./gradlew runClient
```

## Automated Minecraft Version Update

```bash
opencode update-minecraft
```

This invokes an agentic process that updates all version references to the latest snapshot, fixes any breaking changes,
and verifies the mod compiles and starts correctly.
