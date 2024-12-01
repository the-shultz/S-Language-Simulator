package mta.computional.slanguage.facade;

import mta.computional.slanguage.facade.factory.InstructionsFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.program.SProgram;

public class Main {

    public static void main(String[] args) {
        SInstruction i1 = InstructionsFactory.createInstruction(SInstructionRegistry.DECREASE, "x1");
        SInstruction i2 = InstructionsFactory.createInstruction(SInstructionRegistry.INCREASE, "Y");
        SInstruction i3 = InstructionsFactory.createInstructionWithLabel(InstructionsFactory.createLabel("L1"), SInstructionRegistry.DECREASE, "x1");
        SInstruction i4 = InstructionsFactory.createInstruction(SInstructionRegistry.INCREASE, "Y");

        SProgram program = InstructionsFactory.createEmptyProgram();
        program.addInstruction(i1);
        program.addInstruction(i2);
        program.addInstruction(i3);
        program.addInstruction(i4);

        System.out.println(program.toVerboseString());
    }
}
