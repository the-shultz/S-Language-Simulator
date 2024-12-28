package mta.computional.slanguage.simpl.instruction.function.factory;

public enum SFunction {
    ID,
    SUCCESSOR {
        @Override
        public String toString() {
            return "S";
        }
    }
}
