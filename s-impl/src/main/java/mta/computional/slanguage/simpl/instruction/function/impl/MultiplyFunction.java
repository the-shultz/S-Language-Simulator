package mta.computional.slanguage.simpl.instruction.function.impl;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.function.factory.FunctionFactory;
import mta.computional.slanguage.simpl.instruction.function.factory.SFunction;
import mta.computional.slanguage.simpl.program.SProgramImpl;
import mta.computional.slanguage.smodel.api.label.Label;

import java.util.List;
import java.util.Map;

import static mta.computional.slanguage.simpl.instruction.function.factory.SFunction.MULTIPLY;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EXIT;

public class MultiplyFunction extends SProgramImpl {

    public MultiplyFunction() {
        super(MULTIPLY.toString());

        Label A = createAvailableLabel();
        String z2 = createFreeWorkingVariable();

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .jumpZeroLabel(EXIT)
                .assignedVariableName("x2")
                .gotoLabel(A)
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(SFunction.ADD.toString())
                        .functionsImplementations(Map.of(
                                SFunction.ADD.toString(), FunctionFactory.createFunction(SFunction.ADD)
                        ))
                        .sourceFunctionInputs(List.of("y","x1"))
                        .build())
                .build();

        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ZERO_VARIABLE, "y"));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ASSIGNMENT, z2, additionalArguments));
        addInstruction(SComponentFactory.createInstructionWithLabel(A, SInstructionRegistry.JUMP_ZERO, z2, additionalArguments));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", additionalArguments));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.DECREASE, z2));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.GOTO_LABEL, "", additionalArguments));

    }

}
