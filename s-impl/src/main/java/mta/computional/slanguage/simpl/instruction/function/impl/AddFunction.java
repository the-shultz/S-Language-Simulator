package mta.computional.slanguage.simpl.instruction.function.impl;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.program.SProgramImpl;
import mta.computional.slanguage.smodel.api.label.Label;

import static mta.computional.slanguage.simpl.instruction.function.factory.SFunction.ADD;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EXIT;

public class AddFunction extends SProgramImpl {

    public AddFunction() {
        super(ADD.toString());

        Label A = createAvailableLabel();
        Label B = createAvailableLabel();
        Label C = createAvailableLabel();

        String z1 = createFreeWorkingVariable();
        String z2 = createFreeWorkingVariable();
        String z3 = createFreeWorkingVariable();

        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ZERO_VARIABLE, "y"));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ASSIGNMENT, z1, AdditionalArguments.builder().assignedVariableName("x1").build()));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ASSIGNMENT, z2, AdditionalArguments.builder().assignedVariableName("x2").build()));
        addInstruction(SComponentFactory.createInstructionWithLabel(C, SInstructionRegistry.JUMP_NOT_ZERO, z1, AdditionalArguments.builder().jumpNotZeroLabel(A).build()));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, z2, AdditionalArguments.builder().jumpNotZeroLabel(B).build()));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, z3));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, z3, AdditionalArguments.builder().jumpNotZeroLabel(EXIT).build()));
        addInstruction(SComponentFactory.createInstructionWithLabel(A, SInstructionRegistry.DECREASE, z1));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "y"));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, z1, AdditionalArguments.builder().jumpNotZeroLabel(A).build()));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.GOTO_LABEL, "", AdditionalArguments.builder().gotoLabel(C).build()));
        addInstruction(SComponentFactory.createInstructionWithLabel(B, SInstructionRegistry.DECREASE, z2));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "y"));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, z2, AdditionalArguments.builder().jumpNotZeroLabel(B).build()));
    }
}
