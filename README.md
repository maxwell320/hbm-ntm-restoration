# HBM Nuclear Tech Modern Port (Forge 1.20.1)

Port of HBM Nuclear Tech from 1.7.10 to modern Forge. Work in progress.

Not a 1:1 line-for-line port. Legacy gameplay is the spec; the systems
are rebuilt against 1.20.1 instead of dragged forward.

## Stack

- Minecraft 1.20.1
- Forge 47.4.18
- Java 17
- Mod id: `hbmntm`
- Package root: `com.hbm.ntm`

## Status

Parity is mid-port. Expect missing machines, missing weapons, empty
worldgen, and an unfinished GUI pass.

Landed:
- shared barrel family, including explosive and waste variants
- fluid container restoration (disperser, glyphid)
- full furnace family (iron, steel, brick, combination, rotary multiblock)
- heat-source bridge so heat-pulling furnaces run
- combination recipe layer
- core machine base, sync, menu, and slot parity

Open:
- remaining machine families (chem plant, crystallizer, compressor, mixer)
- cable, duct, and fluid network parity
- reactors beyond the current baseline
- weapons, projectiles, missiles, turrets
- worldgen (ores, structures, biomes)
- explosion and fallout systems
- vehicles, mobs, other entities

## Build

From the repo root:

    gradlew.bat compileJava
    gradlew.bat runData
    gradlew.bat runClient
    gradlew.bat runServer

CI runs `compileJava` and `runData`. Both should pass before opening a PR.

## Releases

Official builds are on Modrinth and CurseForge. Only jars linked from this
repo or from the maintainer's project pages are official. Anything else is
a third-party upload. See `NOTICE.md`.

## License

GPL-3.0-or-later. See `LICENSE` and `LICENSE.txt`.

Before you fork, mirror, repackage, or redistribute, read `NOTICE.md`. It
covers what has to be kept (LICENSE, NOTICE, the required attribution
block, maintainer and contributor credits) and what is not allowed
(stripping credits, publishing under the project name, implying
endorsement). Clause form, unambiguous.

## Credits

Original HBM Nuclear Tech: HbMinecraft and the original HBM contributors.
This repo is an independent restoration. It is not the original 1.7.10
code and is not maintained by the original HBM authors.

Contributor list: `CREDITS.txt`.

## Issues

Bug reports and parity regressions go on GitHub issues. Include:
- machine or system name
- repro steps
- observed behavior
- expected (legacy) behavior
- log snippet if relevant

Feature requests for anything not in legacy HBM: no by default. Scope is
legacy restoration. Post-parity feature work happens after the port is
complete.
