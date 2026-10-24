# ManaitaPlusGeneral — Minecraft 26.1.2

This branch targets **Fabric and NeoForge** on Minecraft 26.1.2.

## Modules

- `common`: shared gameplay, inventories, menus, recipes, data components and rendering.
- `fabric`: Fabric registration, events, networking and JEI integration.
- `neoforge`: NeoForge registration, events, networking, JEI and optional Curios integration.

Minecraft 26.1.2 requires Java 25 and uses unobfuscated names. Platform release
jars are produced by `shadowJar`; there is no remapping step or Architectury API
runtime dependency.

## Build

Install Java 25 and run:

```powershell
.\gradlew.bat build
```

Use the jars without `-dev` or `-sources` under `fabric/build/libs` and
`neoforge/build/libs`. The build also runs compatibility tests for inventory
persistence, tier model data and recipe decoding/network transfer.

Development dependencies are Fabric Loader 0.19.5, Fabric API 0.155.3+26.1.2,
NeoForge 26.1.2.122 and JEI 29.47.0.111. JEI is optional for players.

On NeoForge, rings are registered, displayed and craftable only with Curios
15.0.0-beta.2+26.1.2 installed. Curios remains optional; development runs can
include it with `-PwithAccessories`.

The original repository for releases before version 3.0 is
[ManaitaPlusGeneral](https://github.com/gengyoubo/ManaitaPlusGeneral).
