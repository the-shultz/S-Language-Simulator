package mta.computional.slanguage.simpl.instruction.function.impl;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.function.factory.SFunction;
import mta.computional.slanguage.simpl.program.SProgramImpl;

public class SuccessorFunction extends SProgramImpl {

    public SuccessorFunction() {
        super(SFunction.SUCCESSOR.toString());

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .assignedVariableName("x1")
                .build();

        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "x1"));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ASSIGNMENT, "y", additionalArguments));
    }

}