package mta.computional.slanguage.simpl.instruction.basic;

import mta.computional.slanguage.simpl.instruction.AbstractInstruction;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ProgramActions;

import java.util.List;

public abstract class AbstractBasicInstruction extends AbstractInstruction {

    public AbstractBasicInstruction(String variable) {
        super(variable);
    }

    public AbstractBasicInstruction(Label label, String variable) {
        super(label, variable);
    }

    @Override
    public List<SInstruction> expand(ProgramActions context) {
        return List.of(this);
    }
}
