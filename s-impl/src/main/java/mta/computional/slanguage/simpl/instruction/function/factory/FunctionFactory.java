package mta.computional.slanguage.simpl.instruction.function.factory;


import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.instruction.function.impl.*;
import mta.computional.slanguage.simpl.instruction.function.impl.predicate.*;
import mta.computional.slanguage.smodel.api.program.SProgram;

public class FunctionFactory {

    public static SProgram createFunction(SFunction functionName) {
        return switch (functionName) {
            case ID -> new IDFunction();
            case SUCCESSOR -> new SuccessorFunction();
            case ADD -> new AddFunction();
            case MINUS -> new MinusFunction();
            case MULTIPLY -> new MultiplyFunction();
            case DIV -> new DivFunction();
            case REMAINDER -> new RemainingFunction();
            case NOT -> new NotFunction();
            case OR -> new OrFunction();
            case AND -> new AndFunction();
            case EQUALITY -> new EqualityFunction();
            case SMALLER_EQUAL_THAN -> new SmallerEqualThanFunction();
            case SMALLER_THAN -> new SmallerThanFunction();
            case BIGGER_EQUAL_THAN -> new BiggerEqualThanFunction();
            case BIGGER_THAN -> new BiggerThanFunction();
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