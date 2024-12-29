package mta.computional.slanguage.simpl.instruction.synthetic.impl;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.synthetic.AbstractSyntheticInstruction;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ExecutionContext;
import mta.computional.slanguage.smodel.api.program.ProgramActions;

import java.util.ArrayList;
import java.util.List;

import static mta.computional.slanguage.simpl.instruction.SInstructionRegistry.JUMP_EQUAL_CONSTANT;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public class JumpEqualsConstant extends AbstractSyntheticInstruction {

    private final Label jumpLabel;
    private final int constantValue;

    public JumpEqualsConstant(String variableName, Label jumpLabel, int constantValue) {
        this(EMPTY, variableName, jumpLabel, constantValue);
    }

    public JumpEqualsConstant(Label label, String variableName, Label jumpLabel, int constantValue) {
        super(label, variableName);
        this.jumpLabel = jumpLabel;
        this.constantValue = constantValue;
    }

    @Override
    protected List<SInstruction> internalExpand(ProgramActions context) {
        Label A = context.createAvailableLabel();
        String z1 = context.createFreeWorkingVariable();

        AdditionalArguments additionalArguments =
                AdditionalArguments
                        .builder()
                        .assignedVariableName(variableName)
                        .jumpZeroLabel(A)
                        .jumpNotZeroLabel(A)
                        .gotoLabel(jumpLabel)
                        .build();

        List<SInstruction> expandedInstructions = new ArrayList<>();
        expandedInstructions.add(SComponentFactory.createInstruction(SInstructionRegistry.ASSIGNMENT, z1, additionalArguments));

        for (int i = 0; i < constantValue; i++) {
            expandedInstructions.add(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_ZERO, z1, additionalArguments));
            expandedInstructions.add(SComponentFactory.createInstruction(SInstructionRegistry.DECREASE, z1));
        }
        expandedInstructions.add(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, z1, additionalArguments));
        expandedInstructions.add(SComponentFactory.createInstruction(SInstructionRegistry.GOTO_LABEL, z1, additionalArguments));
        expandedInstructions.add(SComponentFactory.createInstructionWithLabel(A, SInstructionRegistry.NEUTRAL, "y"));

        return expandedInstructions;
    }

    @Override
    protected String internalToVerboseString() {
        return "IF " + variableName + " = " + constantValue + " GOTO " + jumpLabel.toVerboseString();
    }

    @Override
    public String getName() {
        return JUMP_EQUAL_CONSTANT.getName();
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    public Label execute(ExecutionContext context) {
        long variable = context.getVariable(variableName);

        if (variable == constantValue) {
            return jumpLabel;
        } else {
            return EMPTY;
        }
    }

}