package mta.computional.slanguage.simpl.instruction.function.factory;


import mta.computional.slanguage.simpl.instruction.function.impl.IDFunction;
import mta.computional.slanguage.simpl.instruction.function.impl.SuccessorFunction;
import mta.computional.slanguage.smodel.api.program.SProgram;

public class FunctionFactory {

    public static SProgram createFunction(SFunction functionName) {
        return switch (functionName) {
            case ID -> new IDFunction();
            case SUCCESSOR -> new SuccessorFunction();
        };
    }
}
