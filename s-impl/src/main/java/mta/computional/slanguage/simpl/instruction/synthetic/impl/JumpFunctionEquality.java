package mta.computional.slanguage.simpl.instruction.synthetic.impl;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.function.ApplyFunction;
import mta.computional.slanguage.simpl.instruction.synthetic.AbstractSyntheticInstruction;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ExecutionContext;
import mta.computional.slanguage.smodel.api.program.ProgramActions;
import mta.computional.slanguage.smodel.api.program.SProgram;

import java.util.List;
import java.util.Map;

import static mta.computional.slanguage.simpl.instruction.SInstructionRegistry.JUMP_EQUAL_FUNCTION;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public class JumpFunctionEquality extends AbstractSyntheticInstruction {

    private final Label jumpLabel;
    private final String sourceFunctionName;
    private final Map<String, SProgram> functions;
    private final List<String> inputs;

    public JumpFunctionEquality(String variableName, Label jumpLabel, String sourceFunctionName, Map<String, SProgram> functions, List<String> inputs) {
        this(EMPTY, variableName, jumpLabel, sourceFunctionName, functions, inputs);
    }

    public JumpFunctionEquality(Label label, String variableName, Label jumpLabel, String sourceFunctionName, Map<String, SProgram> functions, List<String> inputs) {
        super(label, variableName);
        this.jumpLabel = jumpLabel;
        this.sourceFunctionName = sourceFunctionName;
        this.functions = functions;
        this.inputs = inputs;
    }

    @Override
    protected List<SInstruction> internalExpand(ProgramActions context) {
        String z1 = context.createFreeWorkingVariable();

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .variableEqualityName(z1)
                .jumpVariableEqualityLabel(jumpLabel)
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(sourceFunctionName)
                        .functionsImplementations(functions)
                        .sourceFunctionInputs(inputs)
                        .build())
                .build();

        return List.of(
                SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, z1, additionalArguments),
                SComponentFactory.createInstruction(SInstructionRegistry.JUMP_EQUAL_VARIABLE, variableName, additionalArguments)
        );
    }

    @Override
    protected String internalToVerboseString() {
        return "IF " + variableName + " = (" + functions.get(sourceFunctionName).getName() + "," + String.join(",", inputs) + ") GOTO " + jumpLabel.toVerboseString();
    }

    @Override
    public String getName() {
        return JUMP_EQUAL_FUNCTION.getName();
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    public Label execute(ExecutionContext context) {
        long variable = context.getVariable(variableName);

        ApplyFunction applyFunction = new ApplyFunction("f", sourceFunctionName, functions, inputs);
        applyFunction.execute(context);

        long functionResult = context.getAndRemoveVariable("f");

        if (variable == functionResult) {
            return jumpLabel;
        } else {
            return EMPTY;
        }
    }

}