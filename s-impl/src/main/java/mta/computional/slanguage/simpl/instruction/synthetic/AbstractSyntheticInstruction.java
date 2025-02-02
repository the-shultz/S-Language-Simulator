package mta.computional.slanguage.simpl.instruction.synthetic;


import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.AbstractInstruction;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ProgramActions;

import java.util.ArrayList;
import java.util.List;

import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

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
        List<SInstruction> expandedListOfInstructions = new ArrayList<>(internalExpand(context));

        // if this instruction has a label on it - then it should appear on the first instruction of the expanded list (currently this instruction has no label on it)
        if (hasLabel()) {
            expandedListOfInstructions.add(0, SComponentFactory.createInstructionWithLabel(getLabel(), SInstructionRegistry.NEUTRAL, "y"));
        }

        expandedListOfInstructions.forEach(i -> i.setDerivedFrom(this));
        return expandedListOfInstructions;
    }
    
    protected abstract List<SInstruction> internalExpand(ProgramActions context);
}
