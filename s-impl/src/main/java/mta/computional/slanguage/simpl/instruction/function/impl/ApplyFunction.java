package mta.computional.slanguage.simpl.instruction.function.impl;

import mta.computional.slanguage.simpl.instruction.AbstractInstruction;
import mta.computional.slanguage.simpl.instruction.synthetic.AbstractSyntheticInstruction;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ExecutionContext;
import mta.computional.slanguage.smodel.api.program.ProgramActions;
import mta.computional.slanguage.smodel.api.program.SProgram;

import java.util.List;
import java.util.Map;

import static mta.computional.slanguage.simpl.instruction.SInstructionRegistry.APPLY_FUNCTION;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public class ApplyFunction extends AbstractSyntheticInstruction {

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
        return EMPTY;
    }
}
