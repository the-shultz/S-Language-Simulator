package mta.computional.slanguage.simpl.function;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.function.factory.FunctionFactory;
import mta.computional.slanguage.simpl.instruction.function.factory.SFunction;
import mta.computional.slanguage.smodel.api.program.SProgram;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class FunctionsTest {

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

    @Test
    @DisplayName("const function appliance")
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

        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 7);
        Map<String, Long> expectedSnapshot = Map.of("y", 0L, "x1", 7L, "z1", 2L);
        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = testForExpansion(program, 7, 3, 5);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));
    }

    @Test
    @DisplayName("Projection function appliance")
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
        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 7, 3, 5);
        Map<String, Long> expectedSnapshot = Map.of("y", 0L, "x1", 7L, "x2", 3L, "x3", 5L,"z1", 3L);

        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = testForExpansion(program, 7, 3, 5);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

    }

    @Test
    @DisplayName("Successor function appliance")
    void successorFunctionAppliance() {

        SProgram program = SComponentFactory.createEmptyProgram("id & successor");

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(SFunction.SUCCESSOR.toString())
                        .functionsImplementations(Map.of(
                                SFunction.SUCCESSOR.toString(), FunctionFactory.createFunction(SFunction.SUCCESSOR)
                        ))
                        .sourceFunctionInputs(List.of("x1"))
                        .build())
                .build();
        program.addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", additionalArguments));
        System.out.println(program.toVerboseString());
        Map<String, Long> originalExecutionSnapshot = executeProgram(program, 7);
        Map<String, Long> expectedSnapshot = Map.of("y", 8L, "x1", 7L);

        assertTrue(isMapContained(expectedSnapshot, originalExecutionSnapshot));

        Map<String, Long> expandedExecutionSnapshot = testForExpansion(program, 7, 3, 5);
        assertTrue(isMapContained(originalExecutionSnapshot, expandedExecutionSnapshot));

    }

    private Map<String, Long> testForExpansion(SProgram program, long... inputs) {
        System.out.println();
        SProgram expandedProgram = program.expand(1);
        System.out.println(expandedProgram.toVerboseString());
        return executeProgram(expandedProgram, inputs);
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
