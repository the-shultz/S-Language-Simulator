package mta.computional.slanguage.simpl.instruction;


public enum SInstructionRegistry {

    // BASIC
    INCREASE("Increase"),
    DECREASE("Decrease"),
    NEUTRAL("Neutral"),
    JUMP_NOT_ZERO("Jump Not Zero"),

    // SYNTHETIC
    ZERO_VARIABLE("Assign Zero"),
    GOTO_LABEL("Go To Label"),
    ASSIGNMENT("Variable Assignment"),
    CONSTANT_ASSIGNMENT("Constant Assignment"),

    // FUNCTIONS
    APPLY_FUNCTION("Apply Function");

    private final String name;

    SInstructionRegistry(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
