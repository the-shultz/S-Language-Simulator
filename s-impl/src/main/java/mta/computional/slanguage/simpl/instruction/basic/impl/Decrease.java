package mta.computional.slanguage.simpl.instruction.basic.impl;

import mta.computional.slanguage.simpl.instruction.basic.AbstractBasicInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;

public class Decrease extends AbstractBasicInstruction {

    public Decrease(String variable) {
        super(variable);
    }

    public Decrease(Label label, String variable) {
        super(label, variable);
    }

    @Override
    public String getName() {
        return "Decrease";
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    public Label execute(SProgramRunner programRunner) {
        int variable = programRunner.getVariable(variableName);

        variable--;
        if (variable < 0) {
            variable = 0;
        }

        programRunner.updateVariable(this.variableName, variable);

        return getLabel();
    }
}
