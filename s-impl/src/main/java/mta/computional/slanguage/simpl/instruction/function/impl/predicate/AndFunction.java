package mta.computional.slanguage.simpl.instruction.function.impl.predicate;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.program.SProgramImpl;

import static mta.computional.slanguage.simpl.instruction.function.factory.SFunction.AND;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EXIT;

public class AndFunction extends SProgramImpl {

    public AndFunction() {
        super(AND.toString());

        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ZERO_VARIABLE, "y"));

        AdditionalArguments JZArgs = AdditionalArguments.builder().jumpZeroLabel(EXIT).build();
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_ZERO, "x1", JZArgs));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_ZERO, "x2", JZArgs));
        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "y"));

    }

    @Override
    public String getName() {
        return "x1 " + super.getName() + " x2";
    }

}