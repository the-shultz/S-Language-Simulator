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

import static mta.computional.slanguage.simpl.instruction.SInstructionRegistry.JUMP_EQUAL_VARIABLE;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public class JumpVariableEquality extends AbstractSyntheticInstruction {

    private Label jumpLabel;
    private String secondVariableName;

    public JumpVariableEquality(String variableName, String secondVariableName, Label jumpLabel) {
        this(EMPTY, variableName, secondVariableName, jumpLabel);

    }

    public JumpVariableEquality(Label label, String variableName, String secondVariableName, Label jumpLabel) {
        super(label, variableName);
        this.secondVariableName = secondVariableName;
        this.jumpLabel = jumpLabel;
    }

    @Override
    protected List<SInstruction> internalExpand(ProgramActions context) {
        Label A = context.createAvailableLabel();
        Label B = context.createAvailableLabel();
        Label C = context.createAvailableLabel();
        String z1 = context.createFreeWorkingVariable();
        String z2 = context.createFreeWorkingVariable();

        return List.of(
                SComponentFactory.createInstruction(SInstructionRegistry.ASSIGNMENT, z1, AdditionalArguments.builder().assignedVariableName("x1").build()),
                SComponentFactory.createInstruction(SInstructionRegistry.ASSIGNMENT, z2, AdditionalArguments.builder().assignedVariableName("x2").build()),
                SComponentFactory.createInstructionWithLabel(B, SInstructionRegistry.JUMP_ZERO, z1, AdditionalArguments.builder().jumpZeroLabel(C).build()),
                SComponentFactory.createInstruction(SInstructionRegistry.JUMP_ZERO, z2, AdditionalArguments.builder().jumpZeroLabel(A).build()),
                SComponentFactory.createInstruction(SInstructionRegistry.DECREASE, z1),
                SComponentFactory.createInstruction(SInstructionRegistry.DECREASE, z2),
                SComponentFactory.createInstruction(SInstructionRegistry.GOTO_LABEL, "", AdditionalArguments.builder().gotoLabel(B).build()),
                SComponentFactory.createInstructionWithLabel(C, SInstructionRegistry.JUMP_ZERO, z2, AdditionalArguments.builder().jumpZeroLabel(jumpLabel).build()),
                SComponentFactory.createInstructionWithLabel(A, SInstructionRegistry.NEUTRAL, "y")
        );
    }

    @Override
    public void replaceLabel(Label oldLabel, Label newLabel) {
        super.replaceLabel(oldLabel, newLabel);
        if (jumpLabel.equals(oldLabel)) {
            jumpLabel = newLabel;
        }
    }

    @Override
    public void replaceVariable(String oldVariable, String newVariable) {
        super.replaceVariable(oldVariable, newVariable);
        if (secondVariableName.equals(oldVariable)) {
            secondVariableName = newVariable;
        }
    }

    @Override
    protected String internalToVerboseString() {
        return "IF " + variableName + " = " + secondVariableName + " GOTO " + jumpLabel.toVerboseString();
    }

    @Override
    public String getName() {
        return JUMP_EQUAL_VARIABLE.getName();
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    public Label execute(ExecutionContext context) {
        long variable1 = context.getVariable(variableName);
        long variable2 = context.getVariable(secondVariableName);

        if (variable1 == variable2) {
            return jumpLabel;
        } else {
            return EMPTY;
        }
    }
}
