package mta.computional.slanguage.simpl.instruction.basic.impl;

import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.basic.AbstractBasicInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ExecutionContext;

import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public class JumpNoZero extends AbstractBasicInstruction {

    private Label jumpLabel;

    public JumpNoZero(String variable, Label jumpLabel) {
        this(EMPTY, variable, jumpLabel);
    }

    public JumpNoZero(Label label, String variable, Label jumpLabel) {
        super(label, variable);
        this.jumpLabel = jumpLabel;
    }

    @Override
    public String getName() {
        return SInstructionRegistry.INCREASE.getName();
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    protected String internalToVerboseString() {
        return "IF " + variableName + " != 0 GOTO " + jumpLabel.toVerboseString();
    }

    @Override
    public void replaceLabel(Label oldLabel, Label newLabel) {
        super.replaceLabel(oldLabel, newLabel);
        if (jumpLabel.equals(oldLabel)) {
            jumpLabel = newLabel;
        }
    }

    @Override
    public Label execute(ExecutionContext context) {
        long variable = context.getVariable(variableName);

        if (variable != 0) {
            return jumpLabel;
        } else {
            return EMPTY;
        }
    }

}