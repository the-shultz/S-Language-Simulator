package mta.computional.slanguage.simpl.instruction.function.factory;


import mta.computional.slanguage.simpl.instruction.function.impl.*;
import mta.computional.slanguage.simpl.instruction.function.impl.predicate.NotFunction;
import mta.computional.slanguage.simpl.instruction.function.impl.predicate.SmallerEqualThanFunction;
import mta.computional.slanguage.smodel.api.program.SProgram;

public class FunctionFactory {

    public static SProgram createFunction(SFunction functionName) {
        return switch (functionName) {
            case ID -> new IDFunction();
            case SUCCESSOR -> new SuccessorFunction();
            case ADD -> new AddFunction();
            case MINUS -> new MinusFunction();
            case MULTIPLY -> new MultiplyFunction();
            case NOT -> new NotFunction();
            case SMALLER_EQUAL_THAN -> new SmallerEqualThanFunction();
            default -> throw new IllegalStateException("Unexpected value: " + functionName);
        };
    }

    public static SProgram createProjectionFunction(int projectionIndex) {
        return new ProjectionFunction(projectionIndex);
    }

    public static SProgram createConstFunction(int constant) {
        return new ConstFunction(constant);
    }
}
