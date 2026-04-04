HBM's Nuclear Tech Mod - 1.20.1 port
 ====================================

 This repo is an ongoing Forge 1.20.1 restoration port of HBM's Nuclear Tech Mod.

 The goal is pretty simple: bring the old mod forward without gutting what made it HBM in the first place. I am not trying to turn it into a loose remake. The plan is to rebuild the old systems one by one on a modern codebase so the result is easier to maintain, easier to debug, and a lot less fragile than dragging 1.7.10 code forward wholesale.

 Right now this is still an early port, not a finished playable release. A decent amount of groundwork is done though. The material/item pipeline exists, the radiation system is in, the Geiger tools and several hazard blocks are working, the anvil and stamp foundation is there, part of the circuit chain is in, and the first press support pieces have started to land.

 Current baseline
 ----------------
 - Minecraft Forge 1.20.1
 - Forge 47.4.18
 - Java 17
 - Mod ID: `hbmntm`
 - Base package: `com.hbm.ntm`

 Setup
 -----
 IntelliJ:
 1. Open the folder as a Gradle project.
 2. If run configs are missing, run `./gradlew genIntellijRuns`.

 Eclipse:
 1. Run `./gradlew genEclipseRuns`.
 2. Import the folder as an existing Gradle project.

 If Gradle or your IDE gets weird, the usual fixes are still the usual fixes:
 - `./gradlew --refresh-dependencies`
 - `./gradlew clean`

 A few useful commands
 ---------------------
 - `./gradlew compileJava`
 - `./gradlew runData`
 - `./gradlew runClient`

 Project layout
 --------------
 - `src/main/java/com/hbm/ntm/` - code
 - `src/main/resources/` - hand-written assets and metadata
 - `src/generated/resources/` - generated recipes, tags, loot, models, and lang output

 Licensing
 ---------
 The repo currently declares `GPL-3.0-or-later`.

 The full GPL text is in `LICENSE`. `LICENSE.txt` and `CREDITS.txt` are just companion files for attribution and extra context.

 Reference docs
 --------------
 - Forge docs: https://docs.minecraftforge.net/en/1.20.1/
 - Java 17 docs: https://docs.oracle.com/en/java/javase/17/
