package mta.computional.slanguage.facade;

import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.SProgram;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;

import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EXIT;

public class Main {

    public static void main(String[] args) {
//        sanityProgram();
//        id();
        syntheticSugars();
    }

    private static SProgram sanityProgram() {
        Label a1 = SComponentFactory.createLabel("A1");
        SInstruction i1 = SComponentFactory.createInstructionWithLabel(a1, SInstructionRegistry.DECREASE, "x1");
        SInstruction i2 = SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "y");
        SInstruction i3 = SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "x1", a1);

        SProgram program = SComponentFactory.createEmptyProgram("P");
        program.addInstruction(i1);
        program.addInstruction(i2);
        program.addInstruction(i3);

        System.out.println(program.toVerboseString());

        SProgramRunner programRunner = SComponentFactory.createProgramRunner(program);
        System.out.println();
        System.out.println("Executing on input: 0");
        long result = programRunner.run(0);
        System.out.printf("Result (y): %d\n", result);

        return program;
    }

    private static SProgram id() {
        SProgram program = SComponentFactory.createEmptyProgram("ID");

        Label A1 = SComponentFactory.createLabel("A1");
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "x1", A1));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "z1"));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "z1", EXIT));
        program.addInstruction(SComponentFactory.createInstructionWithLabel(A1, SInstructionRegistry.DECREASE, "x1"));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "y"));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "x1", A1));

        System.out.println(program.toVerboseString());

        SProgramRunner programRunner = SComponentFactory.createProgramRunner(program);
        System.out.println();
        int input = 0;
        System.out.println("Executing ID on input: " + input);
        long result = programRunner.run(input);
        System.out.printf("Result (y): %d\n", result);

        return program;

    }

    private static SProgram syntheticSugars() {
        SProgram program = SComponentFactory.createEmptyProgram("Synthetic Sugars");
        Label A1 = SComponentFactory.createLabel("A1");
        program.addInstruction(SComponentFactory.createInstructionWithLabel(A1, SInstructionRegistry.INCREASE, "z2"));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ZERO_VARIABLE, "x1"));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "z3", A1));

        System.out.println(program.toVerboseString());
        System.out.println();
        System.out.println(program.expand().toVerboseString());

        SProgramRunner programRunner = SComponentFactory.createProgramRunner(program);
        System.out.println();
        int input = 7;
        System.out.println("Executing Synthetic Sugars on input: " + input);
        long result = programRunner.run(input);
        System.out.printf("Result (y): %d\n", result);

        return program;

    }
}
