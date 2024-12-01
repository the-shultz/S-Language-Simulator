package mta.computional.slanguage.simpl.instruction;

import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;

public abstract class AbstractInstruction implements SInstruction {

    private final Label label;
    protected final String variableName;

    public AbstractInstruction(String variableName) {
        this(Label.EMPTY, variableName);
    }

    public AbstractInstruction(Label label, String variableName) {
        this.label = label;
        this.variableName = variableName;
    }

    @Override
    public boolean hasLabel() {
        return label != Label.EMPTY;
    }

    @Override
    public Label getLabel() {
        return label;
    }


}
