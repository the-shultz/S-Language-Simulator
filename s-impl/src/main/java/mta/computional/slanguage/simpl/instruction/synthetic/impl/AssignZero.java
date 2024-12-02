package mta.computional.slanguage.simpl.instruction.synthetic.impl;

import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.synthetic.AbstractSyntheticInstruction;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ExecutionContext;
import mta.computional.slanguage.smodel.api.program.ProgramActions;

import java.util.List;

import static mta.computional.slanguage.simpl.instruction.SInstructionRegistry.ZERO_VARIABLE;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public class AssignZero extends AbstractSyntheticInstruction {

    public AssignZero(String variableName) {
        super(variableName);
    }

    public AssignZero(Label label, String variableName) {
        super(label, variableName);
    }

    @Override
    public String getName() {
        return ZERO_VARIABLE.getName();
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    public String toVerboseString() {
        return super.toVerboseString() + variableName + " <- 0";
    }

    @Override
    public List<SInstruction> expand(ProgramActions context) {
        Label L = context.createAvailableLabel();
        return List.of(
                SComponentFactory.createInstructionWithLabel(L, SInstructionRegistry.DECREASE, variableName),
                SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, variableName, L)
        );
    }

    @Override
    public Label execute(ExecutionContext context) {
        context.updateVariable(variableName, 0);
        return EMPTY;
    }
}
