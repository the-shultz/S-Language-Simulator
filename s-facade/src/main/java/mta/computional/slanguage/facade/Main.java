package mta.computional.slanguage.facade;

import mta.computional.slanguage.facade.factory.InstructionsFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.SProgram;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;

import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EXIT;

public class Main {

    public static void main(String[] args) {
//        sanityProgram();
        id();
    }

    private static SProgram sanityProgram() {
        Label a1 = InstructionsFactory.createLabel("A1");
        SInstruction i1 = InstructionsFactory.createInstructionWithLabel(a1, SInstructionRegistry.DECREASE, "x1");
        SInstruction i2 = InstructionsFactory.createInstruction(SInstructionRegistry.INCREASE, "y");
        SInstruction i3 = InstructionsFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "x1", a1);

        SProgram program = InstructionsFactory.createEmptyProgram("P");
        program.addInstruction(i1);
        program.addInstruction(i2);
        program.addInstruction(i3);

        System.out.println(program.toVerboseString());

        SProgramRunner programRunner = InstructionsFactory.createProgramRunner(program);
        System.out.println();
        System.out.println("Executing on input: 0");
        long result = programRunner.run(0);
        System.out.printf("Result (y): %d\n", result);

        return program;
    }

    private static SProgram id() {
        SProgram program = InstructionsFactory.createEmptyProgram("ID");

        Label A1 = InstructionsFactory.createLabel("A1");
        program.addInstruction(InstructionsFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "x1", A1));
        program.addInstruction(InstructionsFactory.createInstruction(SInstructionRegistry.INCREASE, "z1"));
        program.addInstruction(InstructionsFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "z1", EXIT));
        program.addInstruction(InstructionsFactory.createInstructionWithLabel(A1, SInstructionRegistry.DECREASE, "x1"));
        program.addInstruction(InstructionsFactory.createInstruction(SInstructionRegistry.INCREASE, "y"));
        program.addInstruction(InstructionsFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "x1", A1));

        System.out.println(program.toVerboseString());

        SProgramRunner programRunner = InstructionsFactory.createProgramRunner(program);
        System.out.println();
        int input = 0;
        System.out.println("Executing ID on input: " + input);
        long result = programRunner.run(input);
        System.out.printf("Result (y): %d\n", result);

        return program;

    }
}
