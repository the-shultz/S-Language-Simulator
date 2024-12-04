package mta.computional.slanguage.simpl.instruction.synthetic.impl;

import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.synthetic.AbstractSyntheticInstruction;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ExecutionContext;
import mta.computional.slanguage.smodel.api.program.ProgramActions;

import java.util.ArrayList;
import java.util.List;

import static mta.computional.slanguage.simpl.instruction.SInstructionRegistry.CONSTANT_ASSIGNMENT;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public class ConstantAssignment extends AbstractSyntheticInstruction {

    private final int constantValue;

    public ConstantAssignment(String variableName, int constantValue) {
        this(EMPTY, variableName, constantValue);
    }

    public ConstantAssignment(Label label, String variableName, int constantValue) {
        super(label, variableName);
        this.constantValue = constantValue;
    }

    @Override
    public String getName() {
        return CONSTANT_ASSIGNMENT.getName();
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    protected String internalToVerboseString() {
        return variableName + " <- " + constantValue;
    }

    @Override
    public List<SInstruction> expand(ProgramActions context) {

        List<SInstruction> result = new ArrayList<>();
        result.add(SComponentFactory.createInstruction(SInstructionRegistry.ZERO_VARIABLE, variableName));
        for (int i = 0; i < constantValue; i++) {
            result.add(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, variableName));
        }

        return result;
    }

    @Override
    public Label execute(ExecutionContext context) {
        context.updateVariable(variableName, constantValue);
        return EMPTY;
    }
}
