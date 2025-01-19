package mta.computional.slanguage.simpl.instruction.synthetic.mechanism.recursion;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.synthetic.AbstractSyntheticInstruction;
import mta.computional.slanguage.simpl.instruction.synthetic.mechanism.FunctionStructureUtil;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ExecutionContext;
import mta.computional.slanguage.smodel.api.program.ProgramActions;
import mta.computional.slanguage.smodel.api.program.SProgram;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static mta.computional.slanguage.simpl.instruction.SInstructionRegistry.*;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EXIT;

public class Recursion extends AbstractSyntheticInstruction {

    private final SProgram breakingCondition;
    private final SProgram stepFunction;

    private final String recursiveArgument;
    private final List<String> inputs;

    public Recursion(String variableName, SProgram breakingCondition, SProgram stepFunction, List<String> inputs, String recursiveArgument) {
        this(EMPTY, variableName, breakingCondition, stepFunction, inputs, recursiveArgument);
    }

    public Recursion(Label label, String variableName, SProgram breakingCondition, SProgram stepFunction, List<String> inputs, String recursiveArgument) {
        super(label, variableName);
        this.breakingCondition = breakingCondition;
        this.stepFunction = stepFunction;
        this.recursiveArgument = recursiveArgument;
        this.inputs = inputs;
    }

    @Override
    protected List<SInstruction> internalExpand(ProgramActions context) {

        String Z1 = context.createFreeWorkingVariable();
        String RECURSIVE_ARGUMENT = context.createFreeWorkingVariable();
        Label L1 = context.createAvailableLabel();
        Label FINISH_EXPANSION_LABEL = context.createAvailableLabel();

        // apply base case:
        List<SInstruction> result = new ArrayList<>(breakingCondition.getInstructions());

        result.add(SComponentFactory.createInstruction(JUMP_ZERO, recursiveArgument, AdditionalArguments.builder().jumpZeroLabel(FINISH_EXPANSION_LABEL).build()));
        result.add(SComponentFactory.createInstruction(ASSIGNMENT, RECURSIVE_ARGUMENT, AdditionalArguments.builder().assignedVariableName(recursiveArgument).build()));

        // apply recursive case:
        List<String> newInputs = new ArrayList<>();
        newInputs.add(Z1);
        newInputs.add("y");
        newInputs.add(RECURSIVE_ARGUMENT);
        newInputs.addAll(inputs);

        SInstruction stepApplyFunction = SComponentFactory.createInstruction(APPLY_FUNCTION, "y", AdditionalArguments.builder().functionCallData(AdditionalArguments.FunctionCallData.builder()
                .sourceFunctionName(stepFunction.getName())
                .functionsImplementations(Map.of(stepFunction.getName(), stepFunction))
                .sourceFunctionInputs(newInputs)
                .build()).build());
        List<SInstruction> stepFunctionExpansion = stepApplyFunction.expand(context);

        stepFunctionExpansion.get(0).replaceLabel(EMPTY, L1);

        result.addAll(stepFunctionExpansion);

        result.add(SComponentFactory.createInstruction(INCREASE, Z1));
        result.add(SComponentFactory.createInstruction(DECREASE, RECURSIVE_ARGUMENT));
        result.add(SComponentFactory.createInstruction(JUMP_NOT_ZERO, RECURSIVE_ARGUMENT, AdditionalArguments.builder().jumpNotZeroLabel(L1).build()));
        result.add(SComponentFactory.createInstructionWithLabel(FINISH_EXPANSION_LABEL, NEUTRAL, "y"));

        return result;
    }

    @Override
    protected String internalToVerboseString() {
        return variableName + " <- Recursive(" + breakingCondition.getName() + "," + stepFunction.getName() + "," + String.join(",", inputs) + ")";
    }

    @Override
    public String getName() {
        return RECURSION.getName();
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    public Label execute(ExecutionContext context) {

        List<String> baseCaseInputs = new ArrayList<>();
        baseCaseInputs.add(recursiveArgument);
        baseCaseInputs.addAll(inputs);
        long currentResult = runProgram(breakingCondition, baseCaseInputs, context);

        long recursionDepth = context.getVariable(recursiveArgument); // first input is the recursion variable
        int iterationNumber = 0;

        // next step loop. currentResult & iterationNumber are of the last performed iteration
        while (recursionDepth > 0) {

            // preparing inputs such that first is iteration number. second is current result and then all the rest of the inputs.
            // step function assumes that and was created in accordance
            List<String> newInputs = new ArrayList<>();
            newInputs.add(String.valueOf(iterationNumber));
            newInputs.add(String.valueOf(currentResult));
            newInputs.add(recursiveArgument);
            newInputs.addAll(inputs);

            currentResult = runProgram(stepFunction, newInputs, context);
            recursionDepth--;
            iterationNumber++;
        }

        context.updateVariable(variableName, currentResult);
        return EMPTY;
    }

    private long runProgram(SProgram program, List<String> inputs, ExecutionContext context) {
        FunctionStructureUtil functionStructureUtil = new FunctionStructureUtil();
        List<Long> sourceProgramInputs =
                inputs
                        .stream()
                        .map(input -> functionStructureUtil.evaluateInput(context, input.trim(), Map.of())) //TODO: FOR NOW NOT SUPPORTING NESTED FUNCTIONS CALLS AS PART OF RECURSIVE FUNCTIONS
                        .toList();

        SProgramRunner programRunner = SComponentFactory.createProgramRunner(program);
        long result = programRunner.run(sourceProgramInputs.toArray(new Long[0]));
        return result;
    }
}
