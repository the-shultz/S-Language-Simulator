package mta.computional.slanguage.facade;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
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
        SInstruction i3 = SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "x1", AdditionalArguments.builder().jumpNotZeroLabel(a1).build());

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
        AdditionalArguments additionalArguments = AdditionalArguments.builder().jumpNotZeroLabel(A1).build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "x1", additionalArguments));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "z1"));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "z1", AdditionalArguments.builder().jumpNotZeroLabel(EXIT).build()));
        program.addInstruction(SComponentFactory.createInstructionWithLabel(A1, SInstructionRegistry.DECREASE, "x1"));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "y"));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "x1", additionalArguments));

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
        Label L1 = SComponentFactory.createLabel("L1");
        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .jumpNotZeroLabel(L1)
                .gotoLabel(L1)
                .assignedVariableName("x1")
                .build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.GOTO_LABEL, "", additionalArguments));
        program.addInstruction(SComponentFactory.createInstructionWithLabel(L1, SInstructionRegistry.INCREASE, "z1"));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ASSIGNMENT, "y", additionalArguments));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ZERO_VARIABLE, "x1"));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "z3", additionalArguments));

        System.out.println(program.toVerboseString());
        executeProgram(program, 7);

        System.out.println();
        SProgram expandedProgram = program.expand();
        System.out.println(expandedProgram.toVerboseString());
        executeProgram(expandedProgram, 7);

        return program;
    }

    private static void executeProgram(SProgram program, int input) {
        SProgramRunner programRunner = SComponentFactory.createProgramRunner(program);
        System.out.println();
        System.out.println("Executing program [" + program.getName() + "] on input: " + input);
        long result = programRunner.run(input);
        System.out.printf("Result (y): %d\n", result);
    }
}
