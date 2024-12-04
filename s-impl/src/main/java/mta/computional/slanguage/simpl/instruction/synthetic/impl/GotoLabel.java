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

import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public class GotoLabel extends AbstractSyntheticInstruction {

    private final Label jumpLabel;

    public GotoLabel(String variableName, Label jumpLabel) {
        this(EMPTY, variableName, jumpLabel);
    }

    public GotoLabel(Label label, String variableName, Label jumpLabel) {
        super(label, variableName);
        this.jumpLabel = jumpLabel;
    }

    @Override
    public String getName() {
        return SInstructionRegistry.GOTO_LABEL.getName();
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    protected String internalToVerboseString() {
        return "GOTO " + jumpLabel.toVerboseString();
    }

    @Override
    public List<SInstruction> expand(ProgramActions context) {
        String freeWorkingVariable = context.createFreeWorkingVariable();
        AdditionalArguments additionalArguments = AdditionalArguments.builder().jumpNotZeroLabel(jumpLabel).build();
        return List.of(
                SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, freeWorkingVariable),
                SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, freeWorkingVariable, additionalArguments)
        );
    }

    @Override
    public Label execute(ExecutionContext context) {
        return jumpLabel;
    }
}
