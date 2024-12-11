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

    private String assignedVariableName;

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
    public void replaceVariable(String oldVariable, String newVariable) {
        super.replaceVariable(oldVariable, newVariable);
        if (assignedVariableName.equals(oldVariable)) {
            assignedVariableName = newVariable;
        }
    }

    @Override
    protected String internalToVerboseString() {
        return variableName + " <- " + assignedVariableName;
    }

    @Override
    public List<SInstruction> internalExpand(ProgramActions context) {
        Label A = context.createAvailableLabel();
        Label B = context.createAvailableLabel();
        Label L = context.createAvailableLabel();
        String Z = context.createFreeWorkingVariable();
        AdditionalArguments additionalArguments =
                AdditionalArguments
                        .builder()
                        .jumpNotZeroLabel(A)
                        .gotoLabel(L)
                        .build();

        return List.of(
                SComponentFactory.createInstruction(SInstructionRegistry.ZERO_VARIABLE, variableName),
                SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, assignedVariableName, additionalArguments),
                SComponentFactory.createInstruction(SInstructionRegistry.GOTO_LABEL, variableName, additionalArguments),
                SComponentFactory.createInstructionWithLabel(A, SInstructionRegistry.DECREASE, assignedVariableName),
                SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, Z),
                SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, assignedVariableName, additionalArguments),
                SComponentFactory.createInstructionWithLabel(B, SInstructionRegistry.DECREASE, Z),
                SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, variableName),
                SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, assignedVariableName),
                SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, Z, AdditionalArguments.builder().jumpNotZeroLabel(B).build()),
                SComponentFactory.createInstructionWithLabel(L, SInstructionRegistry.NEUTRAL, variableName)
                );
    }

    @Override
    public Label execute(ExecutionContext context) {
        context.updateVariable(variableName, context.getVariable(assignedVariableName));
        return EMPTY;
    }
}
