package mta.computional.slanguage.simpl.instruction.basic.impl;

import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.basic.AbstractBasicInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ExecutionContext;

import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

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
    protected String internalToVerboseString() {
        return variableName + " <- " + variableName;
    }

    @Override
    public Label execute(ExecutionContext context) {
        // V <- V does nothing
        return EMPTY;
    }
}