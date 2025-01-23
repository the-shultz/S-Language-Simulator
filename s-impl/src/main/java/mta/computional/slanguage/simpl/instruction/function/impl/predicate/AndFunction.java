package mta.computional.slanguage.simpl.instruction.function.impl.predicate;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.program.SProgramImpl;

import static mta.computional.slanguage.simpl.instruction.function.factory.SFunction.AND;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EXIT;

public class AndFunction extends SProgramImpl {

    private final AdditionalArguments.FunctionCallData leftPredicate;
    private final AdditionalArguments.FunctionCallData rightPredicate;

    public AndFunction(AdditionalArguments.FunctionCallData leftPredicate, AdditionalArguments.FunctionCallData rightPredicate) {
        super(AND.toString());

        this.leftPredicate = leftPredicate;
        this.rightPredicate = rightPredicate;

        String leftPredicateValue = createFreeWorkingVariable();
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, leftPredicateValue, AdditionalArguments.builder().functionCallData(leftPredicate).build()));

        String rightPredicateValue = createFreeWorkingVariable();
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, rightPredicateValue, AdditionalArguments.builder().functionCallData(rightPredicate).build()));

        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ZERO_VARIABLE, "y"));

        AdditionalArguments JZArgs = AdditionalArguments.builder().jumpZeroLabel(EXIT).build();
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_ZERO, leftPredicateValue, JZArgs));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_ZERO, rightPredicateValue, JZArgs));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "y"));

    }

    @Override
    public String getName() {
        return extractPredicateString(leftPredicate) + " " + super.getName() + " " + extractPredicateString(rightPredicate);
    }

    private String extractPredicateString(AdditionalArguments.FunctionCallData predicate) {
        return predicate.getSourceFunctionName() + "(" + String.join(",", predicate.getSourceFunctionInputs()) + ")";
    }
}
