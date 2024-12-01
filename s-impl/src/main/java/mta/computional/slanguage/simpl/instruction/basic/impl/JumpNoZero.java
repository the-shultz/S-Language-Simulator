package mta.computional.slanguage.simpl.instruction.basic.impl;

import mta.computional.slanguage.simpl.instruction.basic.AbstractBasicInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;

public class JumpNoZero extends AbstractBasicInstruction {

    private final Label jumpLabel;

    public JumpNoZero(String variable, Label jumpLabel) {
        this(Label.EMPTY, variable, jumpLabel);
    }

    public JumpNoZero(Label label, String variable, Label jumpLabel) {
        super(label, variable);
        this.jumpLabel = jumpLabel;
    }

    @Override
    public String getName() {
        return "Jump No Zero";
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    public Label execute(SProgramRunner programRunner) {
        int variable = programRunner.getVariable(variableName);

        if (variable != 0) {
            return jumpLabel;
        } else {
            return getLabel();
        }
    }

}