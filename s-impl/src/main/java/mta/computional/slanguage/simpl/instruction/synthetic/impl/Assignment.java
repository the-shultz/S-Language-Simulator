package mta.computional.slanguage.simpl.instruction.synthetic.impl;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.synthetic.AbstractSyntheticInstruction;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ExecutionContext;
import mta.computional.slanguage.smodel.api.program.ProgramActions;

import java.util.List;

import static mta.computional.slanguage.simpl.instruction.SInstructionRegistry.ASSIGNMENT;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public class Assignment extends AbstractSyntheticInstruction {

    private final String assignedVariableName;

    public Assignment(String variableName, String assignedVariableName) {
        this(EMPTY, variableName, assignedVariableName);
    }

    public Assignment(Label label, String variableName, String assignedVariableName) {
        super(label, variableName);
        this.assignedVariableName = assignedVariableName;
    }

    @Override
    public String getName() {
        return ASSIGNMENT.getName();
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    public String toVerboseString() {
        return super.toVerboseString() + variableName + " <- " + assignedVariableName;
    }

    @Override
    public List<SInstruction> expand(ProgramActions context) {
        Label L1 = context.createAvailableLabel();
        AdditionalArguments additionalArguments = AdditionalArguments.builder().jumpNotZeroLabel(L1).build();

        return List.of(
//                SComponentFactory.createInstruction(SInstructionRegistry.ZERO_VARIABLE, variableName),
//                SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, variableName, additionalArguments)
        );
    }

    @Override
    public Label execute(ExecutionContext context) {
        context.updateVariable(variableName, context.getVariable(assignedVariableName));
        return EMPTY;
    }
}
