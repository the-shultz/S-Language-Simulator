package mta.computional.slanguage.simpl.instruction.function.impl;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.program.SProgramImpl;
import mta.computional.slanguage.smodel.api.label.Label;

import static mta.computional.slanguage.simpl.factory.SComponentFactory.createInstruction;
import static mta.computional.slanguage.simpl.factory.SComponentFactory.createInstructionWithLabel;
import static mta.computional.slanguage.simpl.instruction.SInstructionRegistry.*;
import static mta.computional.slanguage.simpl.instruction.function.factory.SFunction.MINUS;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EXIT;

public class MinusFunction extends SProgramImpl {

    public MinusFunction() {
        super(MINUS.toString());

        Label A = createAvailableLabel();
        Label B = createAvailableLabel();
        Label C = createAvailableLabel();

        String z1 = createFreeWorkingVariable();

        addInstruction(createInstruction(ASSIGNMENT, "y", AdditionalArguments.builder().assignedVariableName("x1").build()));
        addInstruction(createInstruction(ASSIGNMENT, z1, AdditionalArguments.builder().assignedVariableName("x2").build()));
        addInstruction(createInstructionWithLabel(C, JUMP_NOT_ZERO, z1, AdditionalArguments.builder().jumpNotZeroLabel(A).build()));
        addInstruction(createInstruction(GOTO_LABEL, "", AdditionalArguments.builder().gotoLabel(EXIT).build()));
        addInstruction(createInstructionWithLabel(A, JUMP_NOT_ZERO, "y", AdditionalArguments.builder().jumpNotZeroLabel(B).build()));
        addInstruction(createInstruction(GOTO_LABEL, "", AdditionalArguments.builder().gotoLabel(EXIT).build()));
        addInstruction(createInstructionWithLabel(B, DECREASE, "y"));
        addInstruction(createInstruction(DECREASE, z1));
        addInstruction(createInstruction(GOTO_LABEL, "", AdditionalArguments.builder().gotoLabel(C).build()));
    }
}