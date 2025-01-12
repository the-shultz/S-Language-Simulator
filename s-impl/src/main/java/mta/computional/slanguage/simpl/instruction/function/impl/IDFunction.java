package mta.computional.slanguage.simpl.instruction.function.impl;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.function.factory.SFunction;
import mta.computional.slanguage.simpl.program.SProgramImpl;
import mta.computional.slanguage.smodel.api.label.Label;

import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EXIT;

public class IDFunction extends SProgramImpl {

    public IDFunction() {
        super(SFunction.ID.toString());

        String z1 = createFreeWorkingVariable();
        Label L1 = SComponentFactory.createLabel("L1");

        AdditionalArguments arguments = AdditionalArguments.builder().jumpNotZeroLabel(L1).build();
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "x1", arguments));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, z1));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, z1, AdditionalArguments.builder().jumpNotZeroLabel(EXIT).build()));
        addInstruction(SComponentFactory.createInstructionWithLabel(L1, SInstructionRegistry.DECREASE, "x1"));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "y"));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "x1", arguments));
    }
}
