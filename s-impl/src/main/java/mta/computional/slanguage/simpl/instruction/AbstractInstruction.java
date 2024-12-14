package mta.computional.slanguage.simpl.instruction;

import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;

import java.util.List;

import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public abstract class AbstractInstruction implements SInstruction {

    private Label label;
    protected String variableName;
    private SInstruction nextInstructionInOrder;
    private SInstruction deriveFromInstruction;

    public AbstractInstruction(String variableName) {
        this(EMPTY, variableName);
    }

    public AbstractInstruction(Label label, String variableName) {
        this.label = label;
        this.variableName = variableName;
        deriveFromInstruction = null;
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
    public List<String> getVariables() {
        return List.of(variableName);
    }

    @Override
    public String toVerboseString() {
        String verboseString = String.format("[ %2s ] %s", getLabel().toVerboseString(), internalToVerboseString());
        if (deriveFromInstruction != null) {
            verboseString += "      << " + deriveFromInstruction.toVerboseString();
        }
        return verboseString;
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

    @Override
    public void replaceVariable(String oldVariable, String newVariable) {
        if (variableName.equals(oldVariable)) {
            variableName = newVariable;
        }
    }

    @Override
    public void replaceLabel(Label oldLabel, Label newLabel) {
        if (label.equals(oldLabel)) {
            label = newLabel;
        }
    }

    @Override
    public void setDerivedFrom(SInstruction derivedFrom) {
        deriveFromInstruction = derivedFrom;
    }
}
