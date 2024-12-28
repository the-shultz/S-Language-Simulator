package mta.computional.slanguage.simpl.instruction.function.impl;


import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.function.factory.SFunction;
import mta.computional.slanguage.simpl.program.SProgramImpl;

public class ProjectionFunction extends SProgramImpl {

    private final int projectionIndex;
    public ProjectionFunction(int projectionIndex) {
        super(SFunction.PROJECTION.toString());
        this.projectionIndex = projectionIndex;

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .assignedVariableName("x" + projectionIndex)
                .build();
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ASSIGNMENT, "y", additionalArguments));
    }

    @Override
    public String getName() {
        return super.getName() + projectionIndex;
    }
}
