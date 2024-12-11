package mta.computional.slanguage.simpl.instruction.synthetic;


import mta.computional.slanguage.simpl.instruction.AbstractInstruction;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ProgramActions;

import java.util.List;

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

    @Override
    public List<SInstruction> expand(ProgramActions context) {
        List<SInstruction> expandedListOfInstructions = internalExpand(context);
        expandedListOfInstructions.forEach(i -> i.setDerivedFrom(this));
        return expandedListOfInstructions;
    }
    
    protected abstract List<SInstruction> internalExpand(ProgramActions context);
}
