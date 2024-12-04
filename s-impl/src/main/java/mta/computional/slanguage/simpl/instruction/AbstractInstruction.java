package mta.computional.slanguage.simpl.instruction;

import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;

import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public abstract class AbstractInstruction implements SInstruction {

    private final Label label;
    protected final String variableName;
    private SInstruction nextInstructionInOrder;

    public AbstractInstruction(String variableName) {
        this(EMPTY, variableName);
    }

    public AbstractInstruction(Label label, String variableName) {
        this.label = label;
        this.variableName = variableName;
    }

    @Override
    public boolean hasLabel() {
        return label != EMPTY;
    }

    @Override
    public Label getLabel() {
        return label;
    }

    @Override
    public String getVariable() {
        return variableName;
    }

    @Override
    public String toVerboseString() {
        return String.format("[ %2s ] %s", getLabel().toVerboseString(), internalToVerboseString());
    }

    abstract protected String internalToVerboseString();

    @Override
    public boolean isSynthetic() {
        return false;
    }

    @Override
    public SInstruction next() {
        return nextInstructionInOrder;
    }

    @Override
    public void setNextInstructionInOrder(SInstruction nextInstructionInOrder) {
        this.nextInstructionInOrder = nextInstructionInOrder;
    }
}
