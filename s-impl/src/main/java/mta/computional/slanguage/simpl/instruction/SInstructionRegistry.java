package mta.computional.slanguage.simpl.instruction;


public enum SInstructionRegistry {
    INCREASE("Increase"),
    DECREASE("Decrease"),
    NEUTRAL("Neutral"),
    JUMP_NOT_ZERO("Jump Not Zero"),
    ZERO_VARIABLE("Assign Zero");

    private final String name;

    SInstructionRegistry(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
