package mta.computional.slanguage.simpl.instruction.basic.impl;

import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.basic.AbstractBasicInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ExecutionContext;

import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public class Decrease extends AbstractBasicInstruction {

    public Decrease(String variable) {
        super(variable);
    }

    public Decrease(Label label, String variable) {
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
        return variableName + " <- " + variableName + " - 1";
    }

    @Override
    public Label execute(ExecutionContext context) {
        long variable = context.getVariable(variableName);

        variable--;
        if (variable < 0) {
            variable = 0;
        }

        context.updateVariable(this.variableName, variable);

        return EMPTY;
    }
}
