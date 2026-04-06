package com.hbm.ntm.common.item;

public enum CircuitItemType {
    VACUUM_TUBE("circuit_vacuum_tube", "Vacuum Tube", "item/circuit_vacuum_tube"),
    CAPACITOR("circuit_capacitor", "Capacitor", "item/circuit_capacitor"),
    CAPACITOR_TANTALIUM("circuit_capacitor_tantalium", "Tantalum Capacitor", "item/circuit_capacitor_tantalium"),
    PCB("circuit_pcb", "Printed Circuit Board", "item/circuit_pcb"),
    SILICON("circuit_silicon", "Printed Silicon Wafer", "item/circuit_silicon"),
    CHIP("circuit_chip", "Microchip", "item/circuit_chip"),
    CHIP_BISMOID("circuit_chip_bismoid", "Versatile Integrated Circuit", "item/circuit_chip_bismoid"),
    ANALOG("circuit_analog", "Analog Circuit Board", "item/circuit_analog"),
    BASIC("circuit_basic", "Integrated Circuit Board", "item/circuit_basic"),
    ADVANCED("circuit_advanced", "Military Grade Circuit Board", "item/circuit_advanced"),
    CAPACITOR_BOARD("circuit_capacitor_board", "Capacitor Board", "item/circuit_capacitor_board"),
    BISMOID("circuit_bismoid", "Versatile Circuit Board", "item/circuit_bismoid"),
    CONTROLLER_CHASSIS("circuit_controller_chassis", "Control Unit Casing", "item/circuit_controller_chassis"),
    CONTROLLER("circuit_controller", "Control Unit", "item/circuit_controller"),
    CONTROLLER_ADVANCED("circuit_controller_advanced", "Advanced Control Unit", "item/circuit_controller_advanced"),
    QUANTUM("circuit_quantum", "Quantum Processing Unit", "item/circuit_quantum"),
    CHIP_QUANTUM("circuit_chip_quantum", "Solid State Quantum Processor", "item/circuit_chip_quantum"),
    CONTROLLER_QUANTUM("circuit_controller_quantum", "Quantum Computer", "item/circuit_controller_quantum"),
    ATOMIC_CLOCK("circuit_atomic_clock", "Atomic Clock", "item/circuit_atomic_clock"),
    NUMITRON("circuit_numitron", "Incandescent Seven Segment Display", "item/circuit_numitron");

    private final String itemId;
    private final String displayName;
    private final String defaultTexturePath;

    CircuitItemType(final String itemId, final String displayName, final String defaultTexturePath) {
        this.itemId = itemId;
        this.displayName = displayName;
        this.defaultTexturePath = defaultTexturePath;
    }

    public String itemId() {
        return this.itemId;
    }

    public String displayName() {
        return this.displayName;
    }

    public String defaultTexturePath() {
        return this.defaultTexturePath;
    }
}
