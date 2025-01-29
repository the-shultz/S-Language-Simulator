package mta.computional.slanguage.simpl.instruction.synthetic.mechanism.sum;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.function.factory.FunctionFactory;
import mta.computional.slanguage.simpl.instruction.function.factory.SFunction;
import mta.computional.slanguage.simpl.instruction.synthetic.AbstractSyntheticInstruction;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ExecutionContext;
import mta.computional.slanguage.smodel.api.program.ProgramActions;
import mta.computional.slanguage.smodel.api.program.SProgram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static mta.computional.slanguage.simpl.instruction.SInstructionRegistry.*;
import static mta.computional.slanguage.simpl.instruction.function.factory.SFunction.ADD;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public class Sum extends AbstractSyntheticInstruction {

    private final AdditionalArguments.FunctionCallData functionToInvoke;

    public Sum(String variableName, AdditionalArguments.FunctionCallData functionToInvoke) {
        this(EMPTY, variableName, functionToInvoke);
    }

    public Sum(Label label, String variableName, AdditionalArguments.FunctionCallData functionToInvoke) {
        super(label, variableName);
        this.functionToInvoke = functionToInvoke;
    }

    @Override
    protected List<SInstruction> internalExpand(ProgramActions context) {

        Label L1 = context.createAvailableLabel();
        String counter = context.createFreeWorkingVariable();
        String total = context.createFreeWorkingVariable();
        String functionInputs = functionToInvoke.getSourceFunctionInputs().stream().skip(1).collect(Collectors.joining(","));
        functionInputs = counter + "," + functionInputs;
        String functionInvocationTemplate = String.format("(%s,%s)", functionToInvoke.getSourceFunctionName(), functionInputs);

        AdditionalArguments sumFunction = AdditionalArguments.builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(ADD.toString())
                        .functionsImplementations(Map.of(
                                ADD.toString(), FunctionFactory.createFunction(ADD)))
                        .sourceFunctionInputs(List.of("y", functionInvocationTemplate))
                        .build())
                .build();
        return List.of(
                SComponentFactory.createInstruction(ASSIGNMENT, total, AdditionalArguments.builder().assignedVariableName("x1").build()),
                SComponentFactory.createInstruction(INCREASE, total), // to include also running on t = 0
                SComponentFactory.createInstructionWithLabel(L1, APPLY_FUNCTION, "y", sumFunction),
                SComponentFactory.createInstruction(INCREASE, counter),
                SComponentFactory.createInstruction(DECREASE, total),
                SComponentFactory.createInstruction(JUMP_NOT_ZERO, total, AdditionalArguments.builder().jumpNotZeroLabel(L1).build())
        );
    }

    @Override
    protected String internalToVerboseString() {
        List<String> sourceFunctionInputs = new ArrayList<>(functionToInvoke.getSourceFunctionInputs());
        sourceFunctionInputs.set(0, "t");
        return "SUM[t:0->x1]: " + functionToInvoke.getSourceFunctionName() + "(" + String.join(",", sourceFunctionInputs) +")";
    }

    @Override
    public String getName() {
        return SUM.getName();
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    public Label execute(ExecutionContext context) {

        SProgram program = SComponentFactory.createEmptyProgram("Sum-Function-Wrapper");
        program.addInstruction(SComponentFactory.createInstruction(APPLY_FUNCTION, "y", AdditionalArguments.builder().functionCallData(functionToInvoke).build()));

        long limit = context.getVariable("x1");
        long counter = 0;
        long sum = 0;

        List<Long> functionInputs = functionToInvoke
                .getSourceFunctionInputs()
                .stream()
                .map(context::getVariable)
                .collect(Collectors.toList());

        do {
            functionInputs.set(0, counter);
            long result = SComponentFactory.createProgramRunner(program).run(functionInputs.toArray(new Long[0]));
            sum += result;
            counter++;
        } while (counter <= limit);

        context.updateVariable(variableName, sum);
        return EMPTY;
    }
}