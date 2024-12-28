package mta.computional.slanguage.simpl.instruction.function.impl;


import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.function.factory.SFunction;
import mta.computional.slanguage.simpl.program.SProgramImpl;

public class ProjectionFunction extends SProgramImpl {

    public ProjectionFunction(int projectionIndex) {
        super(SFunction.PROJECTION.toString());

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .assignedVariableName("x" + projectionIndex)
                .build();
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ASSIGNMENT, "y", additionalArguments));
    }
}
