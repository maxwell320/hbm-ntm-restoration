# HBM Modern Port Roadmap

## Phase 0: Repo and project baseline
- get the GitHub repo online
- keep `main` building
- keep CI running on changes
- write down architecture decisions as they come up

## Phase 1: Skeleton and registration
- settle the package layout
- set up registries for items, blocks, block entities, menus, recipes, sounds, and tabs
- define config and shared constants
- get basic datagen in place

## Phase 2: Materials and item groundwork
- bring over the main materials, parts, and intermediates first
- set naming and tag conventions early
- make sure item models, lang, and recipes all flow through a clean pipeline

## Phase 3: Basic blocks
- add ores, storage blocks, structural blocks, and simple utility blocks
- keep helper code small and justified
- verify loot tables, tool requirements, and generated data as we go

## Phase 4: Recipe systems
- build the modern recipe types and serializers we actually need
- stop relying on old hardcoded processing logic where a proper data-backed system makes more sense
- keep a rough mapping between old systems and new ones

## Phase 5: Machine foundation
- build the shared machine base classes
- define clean boundaries for items, fluids, and energy
- add menu and screen scaffolding
- keep networking and sync predictable

## Phase 6: Early progression loop
- port the first useful machine chains before the flashy stuff
- make sure progression and throughput feel right
- keep each family testable on its own

## Phase 7: Radiation and hazards
- rebuild radiation, contamination, and related survival mechanics for modern Forge
- decide what belongs in player state, world state, or both
- add protective gear and environmental interactions after the core rules are stable

## Phase 8: Heat, power, and reactor basics
- define the low-level rules for heat, power, coolant, and failure states
- get the reactor groundwork right before adding the big reactor content
- keep the simulation readable and debuggable

## Phase 9: Logistics
- port ducts, pipes, cables, and transport systems
- keep chunk behavior and update rules under control
- avoid anything that turns into runaway ticking cost

## Phase 10: Explosives and weapons
- rebuild projectiles and explosion-heavy systems in a server-first way
- keep visuals separate from actual simulation
- only move the larger destructive content once the framework is proven stable

## Phase 11: Advanced machines and multiblocks
- add multiblock controllers and the more complicated industrial loops
- standardize structure validation and assembly logic
- hold higher-tier progression until the common systems are solid

## Phase 12: World systems
- move structures, world events, and worldgen-heavy content later in the project
- treat dimensions as a late feature, not an early one
- avoid blowing up project scope too early

## Phase 13: Compat, balance, and release prep
- add optional integrations later, not now
- do balance passes after the big systems exist
- prepare release notes, changelogs, and public test builds when the project is actually ready

## Ground rules
- no giant legacy code dump
- no broken branch merged into `main`
- no client-only code leaking into common logic
- no rushing advanced content before the basics are working
