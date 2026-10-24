# ManaitaPlusGeneral Architectury workspace

This workspace consolidates the Minecraft 1.19.2 Fabric and Forge sources
into one Gradle build.

## Regarding Source Code Prior to Version 3.0
This is the source code repository for version 3.0 and later. If you want to view the source code repository for versions prior to 3.0, please follow this link:
https://github.com/gengyoubo/ManaitaPlusGeneral

## Modules

- `common`: shared Architectury bootstrap and loader-neutral game logic.
- `fabric`: the Fabric implementation, connected to `common`.
- `forge`: the Forge implementation, connected to `common`.

## Build

Java 17 is required to run the mod; Gradle itself runs on the JDK set in
`gradle.properties`.

```powershell
.\gradlew.bat build
```

Platform jars are written under each platform module's `build/libs` directory.
