package mta.computional.slanguage.simpl.instruction.function.impl;

import com.sun.source.tree.BreakTree;
import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.AbstractInstruction;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.synthetic.AbstractSyntheticInstruction;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ExecutionContext;
import mta.computional.slanguage.smodel.api.program.ProgramActions;
import mta.computional.slanguage.smodel.api.program.SProgram;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static mta.computional.slanguage.simpl.instruction.SInstructionRegistry.APPLY_FUNCTION;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public class ApplyFunction extends AbstractSyntheticInstruction {

    private enum FunctionInputType {
        VARIABLE,
        CONSTANT,
        FUNCTION
    }
    private final String sourceFunctionName;
    private final Map<String, SProgram> functions;
    private final List<String> inputs;

    public ApplyFunction(String variableName, String sourceFunctionName, Map<String, SProgram> functions, List<String> inputs) {
        this(EMPTY, variableName, sourceFunctionName, functions, inputs);
    }

    public ApplyFunction(Label label, String variableName, String sourceFunctionName, Map<String, SProgram> functions, List<String> inputs) {
        super(label, variableName);
        this.functions = functions;
        this.inputs = inputs;
        this.sourceFunctionName = sourceFunctionName;
    }

    @Override
    protected String internalToVerboseString() {
        return variableName + " <- (" + sourceFunctionName + ", " + String.join(",", inputs) + ")";
    }

    @Override
    public String getName() {
        return APPLY_FUNCTION.getName();
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    public List<SInstruction> expand(ProgramActions context) {
        return List.of();
    }

    @Override
    public Label execute(ExecutionContext context) {

        SProgram function = functions.get(sourceFunctionName);

        // prepare and evaluate all the inputs for the function
        List<Long> sourceProgramInputs =
                inputs
                        .stream()
                        .map(input -> evaluateInput(context, input))
                        .toList();

        SProgramRunner programRunner = SComponentFactory.createProgramRunner(function);
        long result = programRunner.run(sourceProgramInputs.toArray(new Long[0]));
        context.updateVariable(variableName, result);

        return EMPTY;
    }

    private long evaluateInput(ExecutionContext context, String input) {
        FunctionInputType functionInputType = analyzeInput(input);

        return switch (functionInputType) {
            case VARIABLE -> context.getVariable(input);
            case CONSTANT -> Long.parseLong(input);
            case FUNCTION -> applyFunction(context, input);

        };
    }

    private long applyFunction(ExecutionContext context, String input) {

        input = input.substring(1, input.length() - 1);
        String[] functionCallParts = input.split(",");

        String functionName = functionCallParts[0];

        List<String> subFunctionInputs = List.of(functionCallParts)
                .subList(1, functionCallParts.length)
                .stream()
                .map(String::trim)
                .toList();

        SProgram subFunctionProgram = functions.get(functionName);
        AdditionalArguments additionalArguments = AdditionalArguments.builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(functionName)
                        .functionsImplementations(Map.of(functionName, subFunctionProgram))
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
}
