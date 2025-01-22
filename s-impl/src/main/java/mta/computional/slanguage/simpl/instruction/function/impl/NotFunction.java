package mta.computional.slanguage.simpl.instruction.function.impl;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.function.factory.SFunction;
import mta.computional.slanguage.simpl.program.SProgramImpl;

import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EXIT;

public class NotFunction extends SProgramImpl {

    public NotFunction() {
        super(SFunction.NOT.toString());

        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ZERO_VARIABLE, "y"));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "x1", AdditionalArguments.builder().jumpNotZeroLabel(EXIT).build()));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "y"));
    }

}
