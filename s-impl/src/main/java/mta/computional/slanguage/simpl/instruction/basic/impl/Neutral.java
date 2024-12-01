package mta.computional.slanguage.simpl.instruction.basic.impl;

import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.basic.AbstractBasicInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;

public class Neutral extends AbstractBasicInstruction {

    // V <- V
    public Neutral(String variable) {
        super(variable);
    }

    public Neutral(Label label, String variable) {
        super(label, variable);
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
    public String toVerboseString() {
        return super.toVerboseString() + variableName + " <- " + variableName;
    }

    @Override
    public Label execute(SProgramRunner programRunner) {
        // V <- V does nothing
        return getLabel();
    }
}