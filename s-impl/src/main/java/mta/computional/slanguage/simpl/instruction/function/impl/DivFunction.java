package mta.computional.slanguage.simpl.instruction.function.impl;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.function.factory.FunctionFactory;
import mta.computional.slanguage.simpl.program.SProgramImpl;
import mta.computional.slanguage.smodel.api.label.Label;

import java.util.List;
import java.util.Map;

import static mta.computional.slanguage.simpl.factory.SComponentFactory.createInstruction;
import static mta.computional.slanguage.simpl.instruction.SInstructionRegistry.*;
import static mta.computional.slanguage.simpl.instruction.function.factory.SFunction.*;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EXIT;

public class DivFunction extends SProgramImpl {

    public DivFunction() {
        super(DIV.toString());

        Label A = createAvailableLabel();
        String remaining = createFreeWorkingVariable();
        String counter = createFreeWorkingVariable();
        String z = createFreeWorkingVariable();
        String z2 = createFreeWorkingVariable();

        // if x1 < x2 then return 0
        AdditionalArguments edgeCaseArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(SMALLER_THAN.toString())
                        .functionsImplementations(Map.of(
                                SMALLER_THAN.toString(), FunctionFactory.createFunction(SMALLER_THAN)
                        ))
                        .sourceFunctionInputs(List.of("x1", "x2"))
                        .build())
                .build();
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, z2, edgeCaseArguments));
        addInstruction(SComponentFactory.createInstruction(JUMP_NOT_ZERO, z2, AdditionalArguments.builder().jumpNotZeroLabel(EXIT).build()));

        // if x2 = 0 then return 0
        addInstruction(createInstruction(JUMP_ZERO, "x2", AdditionalArguments.builder().jumpZeroLabel(EXIT).build()));

        addInstruction(createInstruction(ASSIGNMENT, remaining, AdditionalArguments.builder().assignedVariableName("x1").build()));

        AdditionalArguments minusArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(MINUS.toString())
                        .functionsImplementations(Map.of(
                                MINUS.toString(), FunctionFactory.createFunction(MINUS)
                        ))
                        .sourceFunctionInputs(List.of(remaining, "x2"))
                        .build())
                .build();

        addInstruction(SComponentFactory.createInstructionWithLabel(A, SInstructionRegistry.APPLY_FUNCTION, remaining, minusArguments));
        addInstruction(createInstruction(INCREASE, counter));

        AdditionalArguments conditionArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(AND.toString())
                        .functionsImplementations(Map.of(
                                AND.toString(), FunctionFactory.createFunction(AND),
                                BIGGER_EQUAL_THAN.toString(), FunctionFactory.createFunction(BIGGER_EQUAL_THAN),
                                EQUALITY.toString(), FunctionFactory.createFunction(EQUALITY),
                                CONST.toString(), FunctionFactory.createConstFunction(0),
                                NOT.toString(), FunctionFactory.createFunction(NOT)
                        ))
                        .sourceFunctionInputs(List.of("(>=," + remaining+ ",x2)","(!,(==," + remaining + ",(CONST,x2)))"))
                        .build())
                .build();

        addInstruction(SComponentFactory.createInstruction(APPLY_FUNCTION, z, conditionArguments));
        addInstruction(SComponentFactory.createInstruction(JUMP_NOT_ZERO, z, AdditionalArguments.builder().jumpNotZeroLabel(A).build()));

        addInstruction(createInstruction(ASSIGNMENT, "y", AdditionalArguments.builder().assignedVariableName(counter).build()));
    }
}
