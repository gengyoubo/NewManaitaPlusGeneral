# ManaitaPlusGeneral Architectury workspace

This branch targets Minecraft 1.20.1 on Fabric and Forge.

## Regarding Source Code Prior to Version 3.0
This is the source code repository for version 3.0 and later. If you want to view the source code repository for versions prior to 3.0, please follow this link:
https://github.com/gengyoubo/ManaitaPlusGeneral

## Modules

- `common`: shared gameplay, inventories, menus, recipes and rendering.
- `fabric`: Fabric registration, events, networking and optional Trinkets integration.
- `forge`: Forge registration, events, networking and optional Curios integration.

Both loaders use Architectury API 9.2.14 and bundle the shared module.
Only the Fabric and Forge source modules are used on this branch.

## Build

Use Java 21 to run Gradle 9.5.1 and install a Java 17 toolchain. The mod jars
target Java 17, which is also the minimum game runtime.

```powershell
.\gradlew.bat build
```

Platform jars are written under `fabric/build/libs` and `forge/build/libs`.
Use Minecraft 1.20.1, Architectury API 9.2.14, and either Fabric Loader 0.16.3+
with Fabric API 0.92.2+1.20.1, or Forge 47.4.x.

Rings appear in the creative tab, JEI and crafting recipes only when the matching
accessory mod is installed: Curios 5.9.1+1.20.1 on Forge, or Trinkets 3.7.2 on
Fabric. Sneak-use equips a ring; the normal Manaita key opens an equipped ring's
menu. The built-in Baubles inventory is disabled on this branch.

For development runs with the optional accessory mod, add `-PwithAccessories`.
