package mta.computional.slanguage.simpl.instruction.synthetic;

import mta.computional.slanguage.simpl.instruction.AbstractInstruction;
import mta.computional.slanguage.smodel.api.label.Label;

public abstract class AbstractSyntheticInstruction extends AbstractInstruction {

    public AbstractSyntheticInstruction(String variableName) {
        super(variableName);
    }

    public AbstractSyntheticInstruction(Label label, String variableName) {
        super(label, variableName);
    }

    @Override
    public boolean isSynthetic() {
        return true;
    }
}
