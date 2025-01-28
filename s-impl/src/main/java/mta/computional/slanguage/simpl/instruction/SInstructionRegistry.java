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
    JUMP_ZERO("Jump Zero"),
    JUMP_EQUAL_CONSTANT("Jump Constant"),
    JUMP_EQUAL_VARIABLE("Jump Variable Equality"),
    JUMP_EQUAL_FUNCTION("Jump Function"),

    // MECHANISM
    APPLY_FUNCTION("Apply Function"),
    RECURSION("Recursion"),
    SUM("Sum");

    private final String name;

    SInstructionRegistry(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
