package mta.computional.slanguage.simpl.instruction.synthetic.mechanism;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.program.ExecutionContext;
import mta.computional.slanguage.smodel.api.program.SProgram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mta.computional.slanguage.simpl.instruction.SInstructionRegistry.APPLY_FUNCTION;

public class FunctionStructureUtil {

    private static final String FUNCTION_STRUCTURE_REGEX = "\\((\\b[A-Z]+\\b),((?:[^,()]+|\\((?:[^()]+|\\([^()]*\\))*\\))*)\\)";
    private static final Pattern FUNCTION_STRUCTURE_PATTERN = Pattern.compile(FUNCTION_STRUCTURE_REGEX);
    private static final String FUNCTION_ARGUMENTS_REGEX = "([^,()]+|\\((?:[^()]+|\\([^()]*\\))*\\))";
    private static final Pattern FUNCTION_ARGUMENTS_PATTERN = Pattern.compile(FUNCTION_ARGUMENTS_REGEX);

    private enum FunctionInputType {
        VARIABLE,
        CONSTANT,
        FUNCTION
    }

    public long evaluateInput(ExecutionContext context, String input, Map<String, SProgram> functions) {
        FunctionInputType functionInputType = analyzeInput(input);

        return switch (functionInputType) {
            case VARIABLE -> context.getVariable(input);
            case CONSTANT -> Long.parseLong(input);
            case FUNCTION -> applyFunction(context, input, functions);
        };
    }

    private long applyFunction(ExecutionContext context, String input, Map<String, SProgram> functions) {

        List<String> functionParts = parseFunctionStructure(input);
        String functionName = functionParts.get(0);
        List<String> subFunctionInputs = functionParts.subList(1, functionParts.size());

        AdditionalArguments additionalArguments = AdditionalArguments.builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(functionName)
                        .functionsImplementations(functions)
                        .sourceFunctionInputs(subFunctionInputs)
                        .build())
                .build();

        SInstruction subFunctionInstruction = SComponentFactory.createInstruction(APPLY_FUNCTION, "y", additionalArguments);

        // create and execute sub function.
        ExecutionContext subFunctionContext = context.duplicate();
        subFunctionInstruction.execute(subFunctionContext);
        return subFunctionContext.getVariable("y");
    }

    private FunctionInputType analyzeInput(String input) {

        if (input.startsWith("(")) {
            return FunctionInputType.FUNCTION;
        }

        if (input.startsWith("x") || input.startsWith("y") || input.startsWith("z")) {
            return FunctionInputType.VARIABLE;
        }

        return FunctionInputType.CONSTANT;
    }

    private List<String> parseFunctionStructure(String source) {
        Matcher matcher = FUNCTION_STRUCTURE_PATTERN.matcher(source);

        List<String> functionParts = new ArrayList<>();
        if (matcher.find()) {
            functionParts.add(matcher.group(1));

            String arguments = matcher.group(2);
            Matcher argMatcher = FUNCTION_ARGUMENTS_PATTERN.matcher(arguments);

            while (argMatcher.find()) {
                functionParts.add(argMatcher.group(1).trim());
            }
        }

        return functionParts;
    }

}
