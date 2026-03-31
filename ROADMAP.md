# HBM Modern Port Roadmap

## Phase 0: Project Governance and Baseline
- establish GitHub repository and contribution workflow
- keep `main` buildable at all times
- maintain CI validation for every meaningful change
- document architecture decisions early

## Phase 1: Modern Registration and Project Skeleton
- finalize package layout for common, client, data, and integration code
- establish registry modules for items, blocks, block entities, menus, recipes, sounds, and tabs
- define config strategy and project constants
- set up datagen baseline

## Phase 2: Materials and Basic Content Families
- port core materials, intermediates, and crafting components
- define tags and naming conventions
- establish item grouping strategy for the eventual content scale
- validate model, language, and recipe data pipelines

## Phase 3: Basic Blocks and Industrial Primitives
- port ores, storage blocks, structural blocks, and simple utility blocks
- introduce shared block property helpers only where justified
- validate loot tables, mining requirements, and data generation

## Phase 4: Recipe and Processing Framework
- design modern recipe types and serializers
- replace legacy hardcoded processing logic with structured recipe-driven systems where possible
- build migration tables from old systems to modern recipe definitions

## Phase 5: Core Machine Architecture
- create shared machine block entity base classes
- define item, fluid, and energy interaction boundaries
- implement menu and screen scaffolding
- implement clean sync and networking patterns

## Phase 6: Early Industrial Progression
- port the earliest useful machine chains first
- validate progression loops and recipe throughput
- keep each machine family isolated and testable

## Phase 7: Hazard and Survival Systems
- rewrite radiation and contamination systems for modern Forge patterns
- define player/world persistence boundaries
- port protective gear interactions and environmental effects

## Phase 8: Power, Heat, and Reactor Primitives
- define modern abstractions for heat, power, coolant, and failure states
- implement low-level reactor infrastructure before large reactor content
- keep simulation rules deterministic and debuggable

## Phase 9: Logistics and Industrial Networks
- port ducts, pipes, cables, and transport networks
- apply chunk-safe, performance-aware update rules
- avoid uncontrolled ticking and graph recalculation

## Phase 10: Explosions, Weapons, and Projectiles
- redesign explosive and projectile systems to be server-authoritative
- separate visuals from simulation
- port large-damage content only after the framework is proven stable

## Phase 11: Advanced Machines and Multiblocks
- port multiblock controllers and advanced industrial loops
- standardize structure validation and assembly logic
- introduce higher-tier progression only after the common framework is mature

## Phase 12: World Systems, Structures, and Dimensions
- port structures and world events with modern data/worldgen pipelines
- treat dimensions as a late-stage subsystem
- avoid early overreach into high-complexity world mechanics

## Phase 13: Compatibility, Balance, and Release Engineering
- add optional integrations only after core loops are stable
- run balancing passes after major systems are feature-complete
- prepare release, changelog, migration notes, and public testing cadence

## Ground Rules
- no line-by-line legacy dumping
- no merging broken branches into `main`
- no client-only logic in common/server paths
- no premature porting of advanced systems before foundations are stable
