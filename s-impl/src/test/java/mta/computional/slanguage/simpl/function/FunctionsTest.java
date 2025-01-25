package mta.computional.slanguage.simpl.function;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.function.factory.FunctionFactory;
import mta.computional.slanguage.simpl.instruction.function.factory.SFunction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.SProgram;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static mta.computional.slanguage.simpl.instruction.function.factory.SFunction.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class FunctionsTest {

    public static final Comparator<Map.Entry<String, Long>> VARIABLES_COMPARATOR = (e1, e2) -> {
        String k1 = e1.getKey();
        String k2 = e2.getKey();

        // if y appears - then it's always a winner
        if (k1.equals("y")) {
            return -1;
        }

        if (k2.equals("y")) {
            return 1;
        }

        if (k1.startsWith("x") && k2.startsWith("x")) {
            return compareByVariablesIndex(k1, k2);
        }
        if (k1.startsWith("x")) {
            return -1;
        }
        if (k2.startsWith("x")) {
            return 1;
        }

        // by now they are both not y and not x - so we can compare them (They are Zs)
        return compareByVariablesIndex(k1, k2);
    };

    private static int compareByVariablesIndex(String first, String second) {
        int firstIndex = Integer.parseInt(first.trim().substring(1));
        int secondIndex = Integer.parseInt(second.trim().substring(1));
        return firstIndex - secondIndex;
    }

    private final static int EXPANSION_DEGREE = 15;

    @Test
    @DisplayName("Jump Zero: IF X1 = 0 GOTO L1")
    void jumpZero() {

        SProgram program = SComponentFactory.createEmptyProgram("Jump Zero");
        Label L1 = SComponentFactory.createLabel("L1");
        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .jumpZeroLabel(L1)
                .build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_ZERO, "x1", additionalArguments));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "y", additionalArguments));
        program.addInstruction(SComponentFactory.createInstructionWithLabel(L1, SInstructionRegistry.INCREASE, "x1", additionalArguments));
        System.out.println(program.toVerboseString());

        SProgram expandedProgram = performExpansion(program);

        // x1 = 7
        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 7);
        Map<String, Long> expectedSnapshot = Map.of("y", 1L, "x1", 8L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 7);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        // x1 = 0
        originalExecutionSnapshot = executeProgram(program, 0);
        expectedSnapshot = Map.of("y", 0L, "x1", 1L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 0);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("Jump Equal Constant: IF X1 = 1 GOTO L1")
    void jumpEqualConstant() {

        final int CONSTANT_VALUE = 1;
        SProgram program = SComponentFactory.createEmptyProgram("Jump Equal Constant");
        Label L1 = SComponentFactory.createLabel("L1");
        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .jumpConstantLabel(L1)
                .jumpConstantValue(CONSTANT_VALUE)
                .build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_EQUAL_CONSTANT, "x1", additionalArguments));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "y", additionalArguments));
        program.addInstruction(SComponentFactory.createInstructionWithLabel(L1, SInstructionRegistry.INCREASE, "x1", additionalArguments));
        System.out.println(program.toVerboseString());

        SProgram expandedProgram = performExpansion(program);

        // x1 > CONSTANT_VALUE
        Map<String, Long> originalExecutionSnapshot = executeProgram(program, CONSTANT_VALUE + 1);
        Map<String, Long> expectedSnapshot = Map.of("y", 1L, "x1", (long)(CONSTANT_VALUE + 2));
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, CONSTANT_VALUE + 1);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        // x1 = CONSTANT_VALUE
        originalExecutionSnapshot = executeProgram(program, CONSTANT_VALUE);
        expectedSnapshot = Map.of("y", 0L, "x1", CONSTANT_VALUE + 1L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, CONSTANT_VALUE);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        // x1 < CONSTANT_VALUE
        originalExecutionSnapshot = executeProgram(program, CONSTANT_VALUE - 1);
        expectedSnapshot = Map.of("y", 1L, "x1", (long)CONSTANT_VALUE);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, CONSTANT_VALUE - 1);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("Jump Variables Equality IF X1 = X2 GOTO L1")
    void jumpVariablesEquality() {

        SProgram program = SComponentFactory.createEmptyProgram("Jump Variable Equality");
        Label L1 = SComponentFactory.createLabel("L1");
        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .variableEqualityName("x2")
                .jumpVariableEqualityLabel(L1)
                .build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_EQUAL_VARIABLE, "x1", additionalArguments));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "y", additionalArguments));
        program.addInstruction(SComponentFactory.createInstructionWithLabel(L1, SInstructionRegistry.NEUTRAL, "y"));
        System.out.println(program.toVerboseString());

        SProgram expandedProgram = performExpansion(program);

        // x1 > x2
        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 3, 2);
        Map<String, Long> expectedSnapshot = Map.of("y", 1L, "x1", 3L, "x2", 2L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 3, 2);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        // x1 = x2
        originalExecutionSnapshot = executeProgram(program, 3, 3);
        expectedSnapshot = Map.of("y", 0L, "x1", 3L, "x2", 3L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 3, 3);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        // x1 < x2
        originalExecutionSnapshot = executeProgram(program, 2, 3);
        expectedSnapshot = Map.of("y", 1L, "x1", 2L, "x2", 3L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 2, 3);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("Jump Function Equality: IF X1 = S(X2) GOTO L1")
    void jumpFunctionEquality() {
        SProgram program = SComponentFactory.createEmptyProgram("Jump Function Equality");
        Label L1 = SComponentFactory.createLabel("L1");
        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .jumpFunctionEqualityLabel(L1)
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(SUCCESSOR.toString())
                        .functionsImplementations(Map.of(
                                SUCCESSOR.toString(), FunctionFactory.createFunction(SUCCESSOR)
                        ))
                        .sourceFunctionInputs(List.of("x2"))
                        .build())
                .build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_EQUAL_FUNCTION, "x1", additionalArguments));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "y", additionalArguments));
        program.addInstruction(SComponentFactory.createInstructionWithLabel(L1, SInstructionRegistry.NEUTRAL, "y"));
        System.out.println(program.toVerboseString());

        SProgram expandedProgram = performExpansion(program);

        // x1 != S(x2)
        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 3, 1);
        Map<String, Long> expectedSnapshot = Map.of("y", 1L, "x1", 3L, "x2", 1L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 3, 1);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        // x1 = S(x2)
        originalExecutionSnapshot = executeProgram(program, 3, 2);
        expectedSnapshot = Map.of("y", 0L, "x1", 3L, "x2", 2L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 3, 2);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        // x1 < S(x2)
        originalExecutionSnapshot = executeProgram(program, 2, 2);
        expectedSnapshot = Map.of("y", 1L, "x1", 2L, "x2", 2L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 2, 2);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("Jump compound Functions Equality: IF X1 = S(S(ID(X2))) GOTO L1")
    void jumpCompoundFunctionEquality() {
        SProgram program = SComponentFactory.createEmptyProgram("Jump Function Equality");
        Label L1 = SComponentFactory.createLabel("L1");
        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .jumpFunctionEqualityLabel(L1)
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(SUCCESSOR.toString())
                        .functionsImplementations(Map.of(
                                SUCCESSOR.toString(), FunctionFactory.createFunction(SUCCESSOR),
                                SFunction.ID.toString(), FunctionFactory.createFunction(SFunction.ID)
                        ))
                        .sourceFunctionInputs(List.of("(S,(ID,x2))"))
                        .build())
                .build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_EQUAL_FUNCTION, "x1", additionalArguments));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "y", additionalArguments));
        program.addInstruction(SComponentFactory.createInstructionWithLabel(L1, SInstructionRegistry.NEUTRAL, "y"));
        System.out.println(program.toVerboseString());

        SProgram expandedProgram = performExpansion(program);

        // x1 != S(x2)
        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 4, 1);
        Map<String, Long> expectedSnapshot = Map.of("y", 1L, "x1", 4L, "x2", 1L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 4, 1);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        // x1 = S(x2)
        originalExecutionSnapshot = executeProgram(program, 3, 1);
        expectedSnapshot = Map.of("y", 0L, "x1", 3L, "x2", 1L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 3, 1);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        // x1 < S(x2)
        originalExecutionSnapshot = executeProgram(program, 2, 2);
        expectedSnapshot = Map.of("y", 1L, "x1", 2L, "x2", 2L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 2, 2);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("const function: z1 <- Const2( 7 )")
    void constFunctionAppliance() {

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

        SProgram expandedProgram = performExpansion(program);

        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 7);
        Map<String, Long> expectedSnapshot = Map.of("y", 0L, "x1", 7L, "z1", 2L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 7, 3, 5);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("Projection function: z1 <- U2(7,3,5)")
    void projectionFunctionAppliance() {

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

        SProgram expandedProgram = performExpansion(program);

        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 7, 3, 5);
        Map<String, Long> expectedSnapshot = Map.of("y", 0L, "x1", 7L, "x2", 3L, "x3", 5L,"z1", 3L);

        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 7, 3, 5);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

    }

    @Test
    @DisplayName("Successor function: y <- S(7)")
    void successorFunctionAppliance() {

        SProgram program = SComponentFactory.createEmptyProgram("Successor");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(SUCCESSOR.toString())
                        .functionsImplementations(Map.of(
                                SUCCESSOR.toString(), FunctionFactory.createFunction(SUCCESSOR)
                        ))
                        .sourceFunctionInputs(List.of("x1"))
                        .build())
                .build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", additionalArguments));
        System.out.println(program.toVerboseString());

        SProgram expandedProgram = performExpansion(program);

        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 7);
        Map<String, Long> expectedSnapshot = Map.of("y", 8L, "x1", 7L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 7, 3, 5);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("expansion with label (using successor applied several times)")
    void expansionWithLabelUsingSuccessor() {

        SProgram program = SComponentFactory.createEmptyProgram("Successor");
        Label L1 = SComponentFactory.createLabel("L1");
        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .assignedVariableName("x1")
                .jumpNotZeroLabel(L1)
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(SUCCESSOR.toString())
                        .functionsImplementations(Map.of(
                                SUCCESSOR.toString(), FunctionFactory.createFunction(SUCCESSOR)
                        ))
//                        .sourceFunctionInputs(List.of("z1"))
                        .sourceFunctionInputs(List.of("(S,z1)"))
                        .build())
                .build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ASSIGNMENT, "z1", additionalArguments));
        program.addInstruction(SComponentFactory.createInstructionWithLabel(L1, SInstructionRegistry.APPLY_FUNCTION, "y", additionalArguments));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ASSIGNMENT, "z1", AdditionalArguments.builder().assignedVariableName("y").build()));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.DECREASE, "x2"));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.JUMP_NOT_ZERO, "x2", additionalArguments));
        System.out.println(program.toVerboseString());

        SProgram expandedProgram = performExpansion(program);

        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 7, 3);
        Map<String, Long> expectedSnapshot = Map.of("y", 13L, "x1", 7L, "x2", 0L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 7, 3);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("Add function: y <- ADD(4,6)")
    void addFunctionYAdd46() {
        SProgram program = SComponentFactory.createEmptyProgram("Add");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(SFunction.ADD.toString())
                        .functionsImplementations(Map.of(
                                SFunction.ADD.toString(), FunctionFactory.createFunction(SFunction.ADD)
                        ))
                        .sourceFunctionInputs(List.of("x1","x2"))
                        .build())
                .build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", additionalArguments));
        System.out.println(program.toVerboseString());

        SProgram expandedProgram = performExpansion(program);

        // 0 + 4
        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 0, 4);
        Map<String, Long> expectedSnapshot = Map.of(
                "y", 4L,
                "x1", 0L,
                "x2", 4L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 0, 4);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        // 4 + 6
        originalExecutionSnapshot = executeProgram(program, 4, 6);
        expectedSnapshot = Map.of(
                "y", 10L,
                "x1", 4L,
                "x2", 6L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 4, 6);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        // 4 + 0
        originalExecutionSnapshot = executeProgram(program, 4, 0);
        expectedSnapshot = Map.of(
                "y", 4L,
                "x1", 4L,
                "x2", 0L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 4, 0);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        // 0 + 0
        originalExecutionSnapshot = executeProgram(program, 0, 0);
        expectedSnapshot = Map.of(
                "y", 0L,
                "x1", 0L,
                "x2", 0L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 0, 0);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("Minus function: y <- Minus(6,4)")
    void minusFunctionYMinus64() {
        SProgram program = SComponentFactory.createEmptyProgram("Minus");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(SFunction.MINUS.toString())
                        .functionsImplementations(Map.of(
                                SFunction.MINUS.toString(), FunctionFactory.createFunction(SFunction.MINUS)
                        ))
                        .sourceFunctionInputs(List.of("x1","x2"))
                        .build())
                .build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", additionalArguments));
        System.out.println(program.toVerboseString());

        SProgram expandedProgram = performExpansion(program);

        // x1 > x2
        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 6, 4);
        Map<String, Long> expectedSnapshot = Map.of("y", 2L, "x1", 6L, "x2", 4L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 6, 4);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        // x1 > x2 (== 0)
        originalExecutionSnapshot = executeProgram(program, 4, 6);
        expectedSnapshot = Map.of("y", 0L, "x1", 4L, "x2", 6L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 4, 6);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        // x1 = x2 (== 0)
        originalExecutionSnapshot = executeProgram(program, 4, 4);
        expectedSnapshot = Map.of("y", 0L, "x1", 4L, "x2", 4L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 4, 4);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("complex program with multiple functions")
    void complexProgramWithMultipleFunctions() {

        SProgram program = SComponentFactory.createEmptyProgram("multiple");

        AdditionalArguments addArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(SFunction.ADD.toString())
                        .functionsImplementations(Map.of(
                                SFunction.ADD.toString(), FunctionFactory.createFunction(SFunction.ADD)
                        ))
                        .sourceFunctionInputs(List.of("x1","x2"))
                        .build())
                .build();

        AdditionalArguments minusArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(SFunction.MINUS.toString())
                        .functionsImplementations(Map.of(
                                SFunction.MINUS.toString(), FunctionFactory.createFunction(SFunction.MINUS)
                        ))
                        .sourceFunctionInputs(List.of("y","x2"))
                        .build())
                .build();
        //program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "x1"));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", addArguments));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "y"));
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", minusArguments));
        System.out.println(program.toVerboseString());

        SProgram expandedProgram = performExpansion(program);
        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 4, 6);
        Map<String, Long> expectedSnapshot = Map.of(
                "y", 5L,
                "x1", 4L,
                "x2", 6L);
//        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 0, 0);
//        Map<String, Long> expectedSnapshot = Map.of("y", 1L, "x1", 0L, "x2", 0L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 4, 6);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
//        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 0, 0);
//        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("Multiply function: y <- MUL(4,6)")
    void MultiplyFunctionYMul46() {
        SProgram program = SComponentFactory.createEmptyProgram("Multiply");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(MULTIPLY.toString())
                        .functionsImplementations(Map.of(
                                MULTIPLY.toString(), FunctionFactory.createFunction(MULTIPLY)
                        ))
                        .sourceFunctionInputs(List.of("x1","x2"))
                        .build())
                .build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", additionalArguments));
        System.out.println(program.toVerboseString());
        SProgram expandedProgram = performExpansion(program);

        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 4, 6);
        Map<String, Long> expectedSnapshot = Map.of(
                "y", 24L,
                "x1", 4L,
                "x2", 6L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 4, 6);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 0, 6);
        expectedSnapshot = Map.of(
                "y", 0L,
                "x1", 0L,
                "x2", 6L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 0, 6);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program,  6, 0);
        expectedSnapshot = Map.of(
                "y", 0L,
                "x1", 6L,
                "x2", 0L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 6, 0);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

    }

    @Test
    @DisplayName("Recursion: f(x) = 2x")
    void recursionFX2X() {

        SProgram breakingConditionFunction = SComponentFactory.createEmptyProgram("CONST-0");
        AdditionalArguments breakingConditionArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData
                        .builder()
                        .sourceFunctionName(CONST.toString())
                        .functionsImplementations(Map.of(
                                CONST.toString(), FunctionFactory.createConstFunction(0)
                        ))
                        .sourceFunctionInputs(List.of("x1"))
                        .build())
                .build();
        breakingConditionFunction.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", breakingConditionArguments));


        SProgram twoxStepFunction = SComponentFactory.createEmptyProgram("g:[x+2]");
        twoxStepFunction.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "x2"));
        twoxStepFunction.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.INCREASE, "x2"));
        twoxStepFunction.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.ASSIGNMENT, "y", AdditionalArguments.builder().assignedVariableName("x2").build()));

        SProgram program = SComponentFactory.createEmptyProgram("Recursion f(x) = 2x");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .recursiveData(AdditionalArguments.RecursiveData.builder()
                        .breakingCondition(breakingConditionFunction)
                        .stepFunction(twoxStepFunction)
                        .recursiveArgument("x1")
                        .nativeInputs(List.of())
                        .build())
                .build();

        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.RECURSION, "y", additionalArguments));
        System.out.println(program.toVerboseString());

        SProgram expandedProgram = performExpansion(program);

        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 3);
        Map<String, Long> expectedSnapshot = Map.of("y", 6L, "x1", 3L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 3);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("Recursion: f(x) = x!")
    void recursionFactorial() {

        SProgram breakingConditionFunction = SComponentFactory.createEmptyProgram("CONST-1");
        AdditionalArguments breakingConditionArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData
                        .builder()
                        .sourceFunctionName(CONST.toString())
                        .functionsImplementations(Map.of(
                                CONST.toString(), FunctionFactory.createConstFunction(1)
                        ))
                        .sourceFunctionInputs(List.of("x1"))
                        .build())
                .build();
        breakingConditionFunction.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", breakingConditionArguments));

        SProgram factorialStepFunction = SComponentFactory.createEmptyProgram("g:[(t+1) * t!]");
        AdditionalArguments stepFunctionAdditionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData
                        .builder()
                        .sourceFunctionName(MULTIPLY.toString())
                        .functionsImplementations(Map.of(
                                MULTIPLY.toString(), FunctionFactory.createFunction(MULTIPLY),
                                SUCCESSOR.toString(), FunctionFactory.createFunction(SUCCESSOR)
                        ))
                        .sourceFunctionInputs(List.of("(S,x1)","x2"))
                        .build())
                .build();
        factorialStepFunction.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", stepFunctionAdditionalArguments));

        SProgram program = SComponentFactory.createEmptyProgram("Recursion f(x) = x!");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .recursiveData(AdditionalArguments.RecursiveData.builder()
                        .breakingCondition(breakingConditionFunction)
                        .stepFunction(factorialStepFunction)
                        .recursiveArgument("x1")
                        .nativeInputs(List.of())
                        .build())
                .build();

        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.RECURSION, "y", additionalArguments));
        System.out.println(program.toVerboseString());
        SProgram expandedProgram = performExpansion(program);

        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 4);
        Map<String, Long> expectedSnapshot = Map.of("y", 24L, "x1", 4L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 4);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("Recursion: f(x,y) = x^y")
    void recursionPow() {

        SProgram breakingConditionFunction = SComponentFactory.createEmptyProgram("CONST-1");
        AdditionalArguments breakingConditionArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData
                        .builder()
                        .sourceFunctionName(CONST.toString())
                        .functionsImplementations(Map.of(
                                CONST.toString(), FunctionFactory.createConstFunction(1)
                        ))
                        .sourceFunctionInputs(List.of("x1","x2"))
                        .build())
                .build();
        breakingConditionFunction.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", breakingConditionArguments));

        SProgram powStepFunction = SComponentFactory.createEmptyProgram("g:[(x^y) * x]");
        AdditionalArguments stepFunctionAdditionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData
                        .builder()
                        .sourceFunctionName(MULTIPLY.toString())
                        .functionsImplementations(Map.of(
                                MULTIPLY.toString(), FunctionFactory.createFunction(MULTIPLY)
                        ))
                        .sourceFunctionInputs(List.of("x2","x4"))
                        .build())
                .build();
        powStepFunction.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", stepFunctionAdditionalArguments));

        SProgram program = SComponentFactory.createEmptyProgram("Recursion f(x,y) = x^y");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .recursiveData(AdditionalArguments.RecursiveData.builder()
                        .breakingCondition(breakingConditionFunction)
                        .stepFunction(powStepFunction)
                        .recursiveArgument("x2")
                        .nativeInputs(List.of("x1"))
                        .build())
                .build();

        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.RECURSION, "y", additionalArguments));
        System.out.println(program.toVerboseString());
        SProgram expandedProgram = performExpansion(program);

        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 2, 3);
        Map<String, Long> expectedSnapshot = Map.of(
                "y", 8L,
                "x1", 2L,
                "x2", 3L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 2, 3);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("Recursion: f(x,y) = x-y")
    void recursionMinus() {

        SProgram breakingConditionFunction = SComponentFactory.createEmptyProgram("U-2");
        AdditionalArguments breakingConditionArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData
                        .builder()
                        .sourceFunctionName(PROJECTION.toString())
                        .functionsImplementations(Map.of(
                                    PROJECTION.toString(), FunctionFactory.createProjectionFunction(2)
                        ))                         // x1 here is the recursive variable (y); x2 is the constant from which we start the descent (x)
                        .sourceFunctionInputs(List.of("x1","x2"))
                        .build())
                .build();
        breakingConditionFunction.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", breakingConditionArguments));

        SProgram minusStepFunction = SComponentFactory.createEmptyProgram("g:[(x-y) - 1]");
        AdditionalArguments stepFunctionAdditionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData
                        .builder()
                        .sourceFunctionName(MINUS.toString())
                        .functionsImplementations(Map.of(
                                MINUS.toString(), FunctionFactory.createFunction(MINUS)
                        ))
                        .sourceFunctionInputs(List.of("x2","1"))
                        .build())
                .build();
        minusStepFunction.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", stepFunctionAdditionalArguments));

        SProgram program = SComponentFactory.createEmptyProgram("Recursion f(x,y) = x-y");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .recursiveData(AdditionalArguments.RecursiveData.builder()
                        .breakingCondition(breakingConditionFunction)
                        .stepFunction(minusStepFunction)
                        .recursiveArgument("x2")
                        .nativeInputs(List.of("x1"))
                        .build())
                .build();

        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.RECURSION, "y", additionalArguments));
        System.out.println(program.toVerboseString());
        SProgram expandedProgram = performExpansion(program);

        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 5, 3);
        Map<String, Long> expectedSnapshot = Map.of(
                "y", 2L,
                "x1", 5L,
                "x2", 3L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 5, 3);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 3, 5);
        expectedSnapshot = Map.of(
                "y", 0L,
                "x1", 3L,
                "x2", 5L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 3, 5);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 3, 3);
        expectedSnapshot = Map.of(
                "y", 0L,
                "x1", 3L,
                "x2", 3L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 3, 3);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 3, 0);
        expectedSnapshot = Map.of(
                "y", 3L,
                "x1", 3L,
                "x2", 0L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 3, 0);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 0, 0);
        expectedSnapshot = Map.of(
                "y", 0L,
                "x1", 0L,
                "x2", 0L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 0, 0);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("Predicate Alpha (NOT)")
    void predicateAlpha() {

        SProgram program = SComponentFactory.createEmptyProgram("Alpha (NOT)");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(NOT.toString())
                        .functionsImplementations(Map.of(
                                NOT.toString(), FunctionFactory.createFunction(NOT)
                        ))
                        .sourceFunctionInputs(List.of("x1"))
                        .build())
                .build();

        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", additionalArguments));
        System.out.println(program.toVerboseString());
        SProgram expandedProgram = performExpansion(program);

        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 5);
        Map<String, Long> expectedSnapshot = Map.of(
                "y", 0L,
                "x1", 5L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 5);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 0);
        expectedSnapshot = Map.of(
                "y", 1L,
                "x1", 0L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 0);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("Predicate SMALLER EQUAL THAN (<=)")
    void predicateSmallerEqualThan() {

        SProgram program = SComponentFactory.createEmptyProgram("<=");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(SMALLER_EQUAL_THAN.toString())
                        .functionsImplementations(Map.of(
                                SMALLER_EQUAL_THAN.toString(), FunctionFactory.createFunction(SMALLER_EQUAL_THAN)
                        ))
                        .sourceFunctionInputs(List.of("x1","x2"))
                        .build())
                .build();

        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", additionalArguments));
        System.out.println(program.toVerboseString());
        SProgram expandedProgram = performExpansion(program);

        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 2, 3);
        Map<String, Long> expectedSnapshot = Map.of(
                "y", 1L,
                "x1", 2L,
                "x2", 3L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 2, 3);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 3, 2);
        expectedSnapshot = Map.of(
                "y", 0L,
                "x1", 3L,
                "x2", 2L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 3, 2);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 3, 3);
        expectedSnapshot = Map.of(
                "y", 1L,
                "x1", 3L,
                "x2", 3L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 3, 3);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("Predicate AND")
    void predicateAnd() {
        // should return true when x1==0 && x2==0
        SProgram program = SComponentFactory.createEmptyProgram("AND");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(AND.toString())
                        .functionsImplementations(Map.of(
                                AND.toString(), FunctionFactory.createFunction(AND)
                        ))
                        .sourceFunctionInputs(List.of("x1","x2"))
                        .build())
                .build();

        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", additionalArguments));
        System.out.println(program.toVerboseString());
        SProgram expandedProgram = performExpansion(program);

        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 0, 0);
        Map<String, Long> expectedSnapshot = Map.of(
                "y", 0L,
                "x1", 0L,
                "x2", 0L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 0, 0);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 0, 1);
        expectedSnapshot = Map.of(
                "y", 0L,
                "x1", 0L,
                "x2", 1L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 0, 1);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 1, 0);
        expectedSnapshot = Map.of(
                "y", 0L,
                "x1", 1L,
                "x2", 0L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 1, 0);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 1, 1);
        expectedSnapshot = Map.of(
                "y", 1L,
                "x1", 1L,
                "x2", 1L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 1, 1);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

    }

    @Test
    @DisplayName("Predicate OR")
    void predicateOr() {
        // should return false only when x1==0 && x2==0
        SProgram program = SComponentFactory.createEmptyProgram("OR");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(OR.toString())
                        .functionsImplementations(Map.of(
                                OR.toString(), FunctionFactory.createFunction(OR)
                        ))
                        .sourceFunctionInputs(List.of("x1","x2"))
                        .build())
                .build();

        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", additionalArguments));
        System.out.println(program.toVerboseString());
        SProgram expandedProgram = performExpansion(program);

        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 0, 0);
        Map<String, Long> expectedSnapshot = Map.of(
                "y", 0L,
                "x1", 0L,
                "x2", 0L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 0, 0);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 0, 1);
        expectedSnapshot = Map.of(
                "y", 1L,
                "x1", 0L,
                "x2", 1L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 0, 1);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 1, 0);
        expectedSnapshot = Map.of(
                "y", 1L,
                "x1", 1L,
                "x2", 0L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 1, 0);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 1, 1);
        expectedSnapshot = Map.of(
                "y", 1L,
                "x1", 1L,
                "x2", 1L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 1, 1);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("compound predicates")
    void compoundPredicates() {
        /*
            !x1 && (x2 || (x3 && !x4)
            it will be true when:
            x1=0
            x2=1|0
            x3=0|1
            x4=1|0
        */
        SProgram program = SComponentFactory.createEmptyProgram("Boolean Expression");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(AND.toString())
                        .functionsImplementations(Map.of(
                                AND.toString(), FunctionFactory.createFunction(AND),
                                OR.toString(), FunctionFactory.createFunction(OR),
                                NOT.toString(), FunctionFactory.createFunction(NOT)
                        ))
                        .sourceFunctionInputs(List.of("(!,x1)","(||,x2,(&&,x3,(!,x4)))"))
                        .build())
                .build();

        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", additionalArguments));
        System.out.println(program.toVerboseString());
        SProgram expandedProgram = performExpansion(program);

        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 0, 1, 0, 1);
        Map<String, Long> expectedSnapshot = Map.of(
                "y", 1L,
                "x1", 0L,
                "x2", 1L,
                "x3", 0L,
                "x4", 1L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = executeProgram(expandedProgram, 0, 1, 0, 1);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 0, 0, 1, 0);
        expectedSnapshot = Map.of(
                "y", 1L,
                "x1", 0L,
                "x2", 0L,
                "x3", 1L,
                "x4", 0L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 0, 0, 1, 0);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 1, 1, 1, 1);
        expectedSnapshot = Map.of(
                "y", 0L,
                "x1", 1L,
                "x2", 1L,
                "x3", 1L,
                "x4", 1L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 1, 1, 1, 1);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

        originalExecutionSnapshot = executeProgram(program, 0, 0, 0, 1);
        expectedSnapshot = Map.of(
                "y", 0L,
                "x1", 0L,
                "x2", 0L,
                "x3", 0L,
                "x4", 1L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        expandedExecutionSnapshot = executeProgram(expandedProgram, 0, 0, 0, 1);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

    }

    private SProgram performExpansion(SProgram program) {
        return performExpansion(program, EXPANSION_DEGREE);
    }

    private SProgram performExpansion(SProgram program, int degree) {
        System.out.println();
        SProgram expandedProgram = program.expand(degree);
        System.out.println(expandedProgram.toVerboseString());
        return expandedProgram;
    }

    public static boolean isMapContained(Map<String, Long> contained, Map<String, Long> contains) {
        for (Map.Entry<String, Long> entry : contained.entrySet()) {
            if (!contains.containsKey(entry.getKey()) || !contains.get(entry.getKey()).equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private Map<String, Long> executeProgram(SProgram program, long... inputs) {
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

        return programRunner.variableState();
    }
}
