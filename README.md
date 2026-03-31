# HBM's Nuclear Tech Mod Modern Port

An unofficial architectural modernization port of **HBM's Nuclear Tech Mod** from Minecraft **1.7.10** to **Minecraft 1.20.1** on **Forge**.

This repository is being built as a phased rewrite-by-system project, not a line-by-line code translation.

## Project Status

The project is currently in the **foundation and architecture** stage.

Completed so far:
- Forge 1.20.1 workspace scaffolded
- Java 17 build toolchain pinned
- Gradle wrapper and IDE runs validated
- Minimal mod entrypoint created
- CI workflow added

## Technical Baseline

- Minecraft: `1.20.1`
- Mod loader: `Forge 47.4.18`
- Java toolchain: `17`
- Gradle wrapper: project-managed
- Base package: `com.hbm.ntm`
- Mod ID: `hbmntm`

## Development Principles

- Keep `main` buildable
- Prefer phased subsystem rewrites over direct legacy translation
- Preserve gameplay identity while replacing obsolete technical foundations
- Favor data-driven content where practical
- Keep client-only code isolated from common/server logic
- Validate on both client and dedicated server paths

## Current Scope

Near-term scope:
- registry architecture
- items/materials foundation
- block and recipe pipelines
- machine framework
- hazard/radiation framework
- power, heat, and reactor primitives

Out-of-scope for the earliest milestones:
- full content parity
- legacy renderer parity
- advanced compat integrations
- dimensions and large world event systems

## Building

Use the Gradle wrapper from the project root.

Typical commands:
- `gradlew.bat genIntellijRuns`
- `gradlew.bat build`
- `gradlew.bat runClient`
- `gradlew.bat runServer`

## Repository Policy

- Small, reviewable commits
- CI must stay green on active branches
- New systems should be introduced behind clear architectural boundaries
- No bulk legacy dumps into the modern source tree

## Attribution and License

This project is an unofficial modernization effort based on the original HBM project by **HbMinecraft** and contributors.

Attribution to the original project should be preserved in documentation, release notes, and repository history where applicable.

See `ROADMAP.md` for the phased migration strategy.
