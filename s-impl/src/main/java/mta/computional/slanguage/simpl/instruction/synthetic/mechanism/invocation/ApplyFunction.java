package mta.computional.slanguage.simpl.instruction.synthetic.mechanism.invocation;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.synthetic.AbstractSyntheticInstruction;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ExecutionContext;
import mta.computional.slanguage.smodel.api.program.ProgramActions;
import mta.computional.slanguage.smodel.api.program.SProgram;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static mta.computional.slanguage.simpl.instruction.SInstructionRegistry.*;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EXIT;

public class ApplyFunction extends AbstractSyntheticInstruction {

    private static final String FUNCTION_STRUCTURE_REGEX = "\\(([-!<>=_/#+*$%^|&A-Za-z0-9]+)((?:,([^,()]+|\\((?:[^()]+|\\([^()]*\\))*\\)))+)*\\)";
    private static final Pattern FUNCTION_STRUCTURE_PATTERN = Pattern.compile(FUNCTION_STRUCTURE_REGEX);
    private static final String FUNCTION_ARGUMENTS_REGEX = ",([^,()]+|\\((?:[^()]+|\\([^()]*\\))*\\))";
    private static final Pattern FUNCTION_ARGUMENTS_PATTERN = Pattern.compile(FUNCTION_ARGUMENTS_REGEX);
    public static final String INPUTS_DELIMETER = "<>";

    private enum FunctionInputType {
        VARIABLE,
        CONSTANT,
        FUNCTION
    }
    private final String sourceFunctionName;
    private final Map<String, SProgram> functions;
    private List<String> inputs;

    public ApplyFunction(String variableName, String sourceFunctionName, Map<String, SProgram> functions, List<String> inputs) {
        this(EMPTY, variableName, sourceFunctionName, functions, inputs);
    }

    public ApplyFunction(Label label, String variableName, String sourceFunctionName, Map<String, SProgram> functions, List<String> inputs) {
        super(label, variableName);
        this.functions = functions;
        this.inputs = new ArrayList<>(inputs); // to ensure the list is mutable
        this.sourceFunctionName = sourceFunctionName;
    }

    @Override
    protected String internalToVerboseString() {
        return variableName + " <- (" + functions.get(sourceFunctionName).getName() + "," + String.join(",", inputs) + ")";
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
    public List<SInstruction> internalExpand(ProgramActions context) {

        SProgram sourceProgram = functions.get(sourceFunctionName).duplicate();

        /*
         build list of used variables in the source program.
         it includes
              input variables (x1..xn | in the size of expected input);
              working variables (z1..zm | according to the program usage);
              change y to the variableName
        */
        Map<String, String> variablesReplacements = new HashMap<>();
        for (int i = 1; i <= inputs.size(); i++) {
            variablesReplacements.put("x" + i, context.createFreeWorkingVariable());
        }
        Set<String> usedVariables = sourceProgram.getUsedVariables();
        usedVariables
                // add only variables that weren't added beforehand as part of the input traversal (x1..xn) as program might not use all of its inputs
                // the variables must not be in used by the source program
                .forEach(variable -> variablesReplacements.computeIfAbsent(variable, v -> {
                    String freeWorkingVariable = context.createFreeWorkingVariable();
                    while (usedVariables.contains(freeWorkingVariable)) {
                        freeWorkingVariable = context.createFreeWorkingVariable();
                    }
                    return freeWorkingVariable;
                }));

        variablesReplacements.put("y", context.createFreeWorkingVariable());

        // change source program variables (input & y) to the list of free variables.
        sourceProgram.replaceVariable(variablesReplacements);

        // collect and replace labels in the source program
        Set<Label> sourceLabels = sourceProgram.getUsedLabels();
        Map<Label, Label> labelsReplacements =
            sourceLabels
                    .stream()
                    .collect(Collectors.toMap(l -> l, sourceLabel -> {
                        // find a perfectly new label to replace existing one. it has to be one that is not used in the source program.
                        Label availableLabel;
                        do {
                            availableLabel = context.createAvailableLabel();
                        } while ( sourceLabels.contains(availableLabel));
                        return availableLabel;
                    }));
        sourceProgram.replaceLabels(labelsReplacements);

        List<SInstruction> expansion = new ArrayList<>();

        // expand the source inputs (get a list of instructions derived from the source inputs) use the synthetic sugar for that v <- v'
        for (int i = 1; i <= inputs.size(); i++) {
            String input = inputs.get(i-1).trim();
            String freeVariable = variablesReplacements.get("x" + i);
            FunctionInputType functionInputType = analyzeInput(input);
            // convert the function inputs to instructions where the free variable will accept the argument such that the function will be able to use it.
            // here z1 is an example for that free variable.
            SInstruction inputInstruction = switch (functionInputType) {

                // INPUT: z3 --> z1 <- z3
                case VARIABLE ->
                        SComponentFactory.createInstruction(ASSIGNMENT, freeVariable, AdditionalArguments.builder().assignedVariableName(input).build());

                // INPUT: 4 --> z1 <- 4
                case CONSTANT ->
                        SComponentFactory.createInstruction(CONSTANT_ASSIGNMENT, freeVariable, AdditionalArguments.builder().constantValue(Integer.parseInt(input)).build());

                // INPUT: (ID, z3) --> z1 <- ID(z3)
                case FUNCTION -> {
                    List<String> functionParts = parseFunctionStructure(input);
                    String functionName = functionParts.get(0);
                    List<String> subFunctionInputs = functionParts.subList(1, functionParts.size());

                    yield SComponentFactory.createInstruction(APPLY_FUNCTION, freeVariable, AdditionalArguments.builder().
                            functionCallData(AdditionalArguments.FunctionCallData.builder()
                                    .sourceFunctionName(functionName)
                                    .functionsImplementations(functions)
                                    .sourceFunctionInputs(subFunctionInputs)
                                    .build())
                            .build());
                }
            };

            expansion.add(inputInstruction);
        }

        // add the list of instructions of the source (now altered) program
        expansion.addAll(sourceProgram.getInstructions());

        Label endLabel = context.createAvailableLabel();
        expansion.add(SComponentFactory.createInstructionWithLabel(endLabel, ASSIGNMENT, variableName, AdditionalArguments.builder().assignedVariableName(variablesReplacements.get("y")).build()));

        // in case there is an instruction that had a ... GOTO E inside it, as part of the expansion it should be replaced with GOTO endLabel that stands on the last expanded instruction
        // (and if that's the last instruction that it will be interpreted as end of program)
        expansion.forEach(instruction -> instruction.replaceLabel(EXIT, endLabel));
        return expansion;
    }

    // in case of a variable replacement, replace the variable in the applied function's inputs
    @Override
    public void replaceVariable(String oldVariable, String newVariable) {
        super.replaceVariable(oldVariable, newVariable);

        String concatenatedInputs = String.join(INPUTS_DELIMETER, inputs);
        concatenatedInputs = concatenatedInputs.replace(oldVariable, newVariable);
        inputs = List.of(concatenatedInputs.split(INPUTS_DELIMETER));
    }

    @Override
    public Label execute(ExecutionContext context) {

        SProgram function = functions.get(sourceFunctionName);

        // prepare and evaluate all the inputs for the function
        List<Long> sourceProgramInputs =
                inputs
                        .stream()
                        .map(input -> evaluateInput(context, input.trim()))
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
            if (arguments != null && !arguments.isEmpty()) {
                Matcher argMatcher = FUNCTION_ARGUMENTS_PATTERN.matcher(arguments);

                while (argMatcher.find()) {
                    functionParts.add(argMatcher.group(1).trim());
                }
            }
        }

        return functionParts;
    }
}
