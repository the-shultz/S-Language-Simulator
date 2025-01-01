package mta.computional.slanguage.simpl.instruction.synthetic.impl;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.synthetic.AbstractSyntheticInstruction;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ExecutionContext;
import mta.computional.slanguage.smodel.api.program.ProgramActions;

import java.util.List;

import static mta.computional.slanguage.simpl.instruction.SInstructionRegistry.JUMP_ZERO;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public class JumpZero extends AbstractSyntheticInstruction {

    private Label jumpLabel;

    public JumpZero(String variableName, Label jumpLabel) {
        this(EMPTY, variableName, jumpLabel);
    }

    public JumpZero(Label label, String variableName, Label jumpLabel) {
        super(label, variableName);
        this.jumpLabel = jumpLabel;
    }

    @Override
    protected List<SInstruction> internalExpand(ProgramActions context) {
        Label A = context.createAvailableLabel();

        AdditionalArguments additionalArguments =
                AdditionalArguments
                        .builder()
                        .jumpNotZeroLabel(A)
                        .gotoLabel(jumpLabel)
                        .build();

        return List.of(
                SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, variableName, additionalArguments),
                SComponentFactory.createInstruction(SInstructionRegistry.GOTO_LABEL, variableName, additionalArguments),
                SComponentFactory.createInstructionWithLabel(A, SInstructionRegistry.NEUTRAL, "y")
        );
    }

    @Override
    public void replaceLabel(Label oldLabel, Label newLabel) {
        super.replaceLabel(oldLabel, newLabel);
        if (jumpLabel.equals(oldLabel)) {
            jumpLabel = newLabel;
        }
    }

    @Override
    protected String internalToVerboseString() {
        return "IF " + variableName + " = 0 GOTO " + jumpLabel.toVerboseString();
    }

    @Override
    public String getName() {
        return JUMP_ZERO.getName();
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    public Label execute(ExecutionContext context) {
        long variable = context.getVariable(variableName);

        if (variable == 0) {
            return jumpLabel;
        } else {
            return EMPTY;
        }

    }
}
