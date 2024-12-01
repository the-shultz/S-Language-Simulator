package mta.computional.slanguage.simpl.instruction.basic.impl;

import mta.computional.slanguage.simpl.instruction.basic.AbstractBasicInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;

public class Increase extends AbstractBasicInstruction {

    public Increase(String variable) {
        super(variable);
    }

    public Increase(Label label, String variable) {
        super(label, variable);
    }

    @Override
    public String getName() {
        return "Increase";
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    public Label execute(SProgramRunner programRunner) {

        int variable = programRunner.getVariable(variableName);
        variable++;
        programRunner.updateVariable(this.variableName, variable);
        return getLabel();
    }
}
