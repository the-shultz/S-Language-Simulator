package mta.computional.slanguage.facade;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.function.factory.FunctionFactory;
import mta.computional.slanguage.simpl.instruction.function.factory.SFunction;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.SProgram;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EXIT;

public class Main {

    public static final Comparator<Map.Entry<String, Long>> VARIABLES_COMPARATOR = (e1, e2) -> {
        String k1 = e1.getKey();
        String k2 = e2.getKey();

        // if y appears - then its always a winner
        if (k1.equals("y")) {
            return -1;
        }

        if (k2.equals("y")) {
            return 1;
        }

        if (k1.startsWith("x") && k2.startsWith("x")) {
            return k1.compareTo(k2);
        }
        if (k1.startsWith("x")) {
            return -1;
        }
        if (k2.startsWith("x")) {
            return 1;
        }

        // but now they are both not y and not x - so we can compare them (They are Zs)
        return k1.compareTo(k2);
    };

    public static void main(String[] args) {
//        sanityProgram();
//        id();
//        syntheticSugars();
//        idAndSuccessor();
//        projection();
        constFunction();
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
        long result = programRunner.run(0L);
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
        long input = 0L;
        System.out.println("Executing ID on input: " + input);
        long result = programRunner.run(input);
        System.out.printf("Result (y): %d\n", result);

        return program;

    }

    private static SProgram syntheticSugars() {

        /*
        ID
            if x1 != 0 goto L1
            z1 <- z1 + 1
            if z1 != 0 goto EXIT
        L1: x1 <- x1 - 1
            y <- y + 1
            if x1 != 0 goto L1
        * */
        SProgram idProgram = SComponentFactory.createEmptyProgram("ID");

        Label idL1 = SComponentFactory.createLabel("L1");
        AdditionalArguments idAdditionalArguments = AdditionalArguments.builder().jumpNotZeroLabel(idL1).build();
        idProgram.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "x1", idAdditionalArguments));
        idProgram.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "z1"));
        idProgram.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "z1", AdditionalArguments.builder().jumpNotZeroLabel(EXIT).build()));
        idProgram.addInstruction(SComponentFactory.createInstructionWithLabel(idL1, SInstructionRegistry.DECREASE, "x1"));
        idProgram.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "y"));
        idProgram.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "x1", idAdditionalArguments));
/*        System.out.println(idProgram.toVerboseString());
        executeProgram(idProgram, 7);

        System.out.println();
        SProgram expandedProgram = idProgram.expand();
        System.out.println(expandedProgram.toVerboseString());
        executeProgram(expandedProgram, 7);
*/

/*        Synthetic Sugars
                goto L1
            L1: z1 <- z1 + 1
                y <- x1
            L2: x1 <- 0
                if z3 != 0 goto L1
                z3 <- 3
                z4 <- ID( ID(z3) )
        */
        SProgram program = SComponentFactory.createEmptyProgram("Synthetic Sugars");
        Label L1 = SComponentFactory.createLabel("L1");
        Label L2 = SComponentFactory.createLabel("L2");
        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .jumpNotZeroLabel(L1)
                .gotoLabel(L1)
                .assignedVariableName("x1")
                .constantValue(3)
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(idProgram.getName())
                        .functionsImplementations(Map.of(idProgram.getName(), idProgram))
                        .sourceFunctionInputs(List.of("(ID, z3)"))
//                        .sourceFunctionInputs(List.of("z3"))
                        .build())
                .build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.GOTO_LABEL, "", additionalArguments));
        program.addInstruction(SComponentFactory.createInstructionWithLabel(L1, SInstructionRegistry.INCREASE, "z1"));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ASSIGNMENT, "y", additionalArguments));
        program.addInstruction(SComponentFactory.createInstructionWithLabel(L2, SInstructionRegistry.ZERO_VARIABLE, "x1"));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "z3", additionalArguments));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.CONSTANT_ASSIGNMENT, "z3", additionalArguments));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "z4", additionalArguments));

        System.out.println(program.toVerboseString());
        executeProgram(program, 7);

        System.out.println();
        SProgram expandedProgram = program.expand();
        System.out.println(expandedProgram.toVerboseString());
        executeProgram(expandedProgram, 7);
        return idProgram;
    }

    private static void idAndSuccessor() {
        SProgram program = SComponentFactory.createEmptyProgram("id & successor");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(SFunction.SUCCESSOR.toString())
                        .functionsImplementations(Map.of(
                                SFunction.SUCCESSOR.toString(), FunctionFactory.createFunction(SFunction.SUCCESSOR),
                                SFunction.ID.toString(), FunctionFactory.createFunction(SFunction.ID))
                        )
//                        .sourceFunctionInputs(List.of("x1"))
//                        .sourceFunctionInputs(List.of("(" + SFunction.SUCCESSOR.toString() + ", x1)")) // (SUCCESSOR, x1)
//                        .sourceFunctionInputs(List.of("(SUCCESSOR,(SUCCESSOR,(SUCCESSOR,(SUCCESSOR,x1))))")) // (SUCCESSOR, (SUCCESSOR,(SUCCESSOR,(SUCCESSOR,(SUCCESSOR,x1)))))
//                        .sourceFunctionInputs(List.of("(" + SFunction.SUCCESSOR.toString() + ",(" + SFunction.ID.toString() + ",x1))")) // (SUCCESSOR,(ID,x1))
                        .sourceFunctionInputs(List.of("(" + SFunction.ID.toString() + ",(" + SFunction.SUCCESSOR.toString() + ",x1))")) // (ID,(SUCCESSOR,x1))
                        .build())
                .build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", additionalArguments));
        System.out.println(program.toVerboseString());
        executeProgram(program, 7);

        System.out.println();
        SProgram expandedProgram = program.expand(1);
        System.out.println(expandedProgram.toVerboseString());
        executeProgram(expandedProgram, 7);

    }

    private static void projection() {
        SProgram program = SComponentFactory.createEmptyProgram("projection");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(SFunction.PROJECTION.toString())
                        .functionsImplementations(Map.of(
                                SFunction.PROJECTION.toString(), FunctionFactory.createProjectionFunction(2))
                        )
                        .sourceFunctionInputs(List.of("x1", "x2", "x3"))
                        .build())
                .build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "z1", additionalArguments));
        System.out.println(program.toVerboseString());
        executeProgram(program, 7, 3, 5);

        System.out.println();
        SProgram expandedProgram = program.expand();
        System.out.println(expandedProgram.toVerboseString());
        executeProgram(expandedProgram, 7, 3, 5);
    }

    private static void constFunction() {
        SProgram program = SComponentFactory.createEmptyProgram("const function");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(SFunction.CONST.toString())
                        .functionsImplementations(Map.of(
                                SFunction.CONST.toString(), FunctionFactory.createConstFunction(2))
                        )
                        .sourceFunctionInputs(List.of("x1"))
                        .build())
                .build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "z1", additionalArguments));
        System.out.println(program.toVerboseString());
        executeProgram(program, 7);

        System.out.println();
        SProgram expandedProgram = program.expand();
        System.out.println(expandedProgram.toVerboseString());
        executeProgram(expandedProgram, 7, 3, 5);

    }

    private static void executeProgram(SProgram program, long... inputs) {
        SProgramRunner programRunner = SComponentFactory.createProgramRunner(program);
        System.out.println();
        System.out.println("Executing program [" + program.getName() + "] on inputs: " + Arrays.toString(inputs));

        programRunner.run(Arrays
                        .stream(inputs)
                        .boxed()
                        .toArray(Long[]::new));

        System.out.println("Variable values:");
        if (!programRunner.variableState().containsKey("y")) {
            programRunner.variableState().put("y", 0L);
        }

        programRunner
                .variableState()
                .entrySet()
                .stream()
                .sorted(VARIABLES_COMPARATOR)
                .map(e -> String.format("Variable [%s] = %d", e.getKey(), e.getValue()))
                .forEach(System.out::println);
    }
}
