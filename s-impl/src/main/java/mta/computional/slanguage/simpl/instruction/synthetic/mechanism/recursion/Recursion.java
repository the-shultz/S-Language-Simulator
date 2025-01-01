package mta.computional.slanguage.simpl.instruction.synthetic.mechanism.recursion;

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

import static mta.computional.slanguage.simpl.instruction.SInstructionRegistry.RECURSION;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public class Recursion extends AbstractSyntheticInstruction {

    private final SProgram breakingCondition;
    private final SProgram stepFunction;

    // invariant: first input is the recursion variable
    private final List<String> inputs;

    public Recursion(String variableName, SProgram breakingCondition, SProgram stepFunction, List<String> inputs) {
        this(EMPTY, variableName, breakingCondition, stepFunction, inputs);
    }

    public Recursion(Label label, String variableName, SProgram breakingCondition, SProgram stepFunction, List<String> inputs) {
        super(label, variableName);
        this.breakingCondition = breakingCondition;
        this.stepFunction = stepFunction;
        this.inputs = inputs;
    }

    @Override
    protected List<SInstruction> internalExpand(ProgramActions context) {
        return List.of();
    }

    @Override
    protected String internalToVerboseString() {
        return variableName + " <- Recursive(" + ")";
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

        long currentResult = runProgram(breakingCondition, inputs, context);

        long recursionDepth = context.getVariable(inputs.get(0)); // first input is the recursion variable
        int iterationNumber = 0;

        // next step loop. currentResult & iterationNumber are of the last performed iteration
        while (recursionDepth > 0) {

            // preparing inputs such that first is iteration number. second is current result and then all the rest of the inputs.
            // step function assumes that and was created in accordance
            List<String> newInputs = new ArrayList<>();
            newInputs.add(String.valueOf(iterationNumber));
            newInputs.add(String.valueOf(currentResult));
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
