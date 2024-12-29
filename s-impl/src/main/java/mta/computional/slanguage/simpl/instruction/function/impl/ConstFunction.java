package mta.computional.slanguage.simpl.instruction.function.impl;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.program.SProgramImpl;

import static mta.computional.slanguage.simpl.instruction.function.factory.SFunction.CONST;

public class ConstFunction extends SProgramImpl {

    private final int constant;

    public ConstFunction(int constant) {
        super(CONST.toString());
        this.constant = constant;

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .constantValue(constant)
                .build();

        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.CONSTANT_ASSIGNMENT, "y", additionalArguments));
    }

    @Override
    public String getName() {
        return super.getName() + constant;
    }
}
