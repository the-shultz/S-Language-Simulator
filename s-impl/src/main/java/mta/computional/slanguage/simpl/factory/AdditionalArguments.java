package mta.computional.slanguage.simpl.factory;

import lombok.Builder;
import lombok.Getter;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.SProgram;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Builder
@Getter
public class AdditionalArguments implements Serializable {

    private Label jumpNotZeroLabel;
    private Label jumpZeroLabel;
    private Label gotoLabel;
    private String assignedVariableName;
    private int constantValue;
    private int jumpConstantValue;
    private Label jumpConstantLabel;
    private Label jumpVariableEqualityLabel;
    private String variableEqualityName;
    private Label jumpFunctionEqualityLabel;
    private FunctionCallData functionCallData;
    private RecursiveData recursiveData;

    static AdditionalArguments EMPTY = AdditionalArguments.builder().build();

    @Builder
    @Getter
    public static class FunctionCallData implements Serializable {
        private String sourceFunctionName;
        private Map<String, SProgram> functionsImplementations;
        private List<String> sourceFunctionInputs;
    }

    @Builder
    @Getter
    public static class RecursiveData implements Serializable{
        private SProgram breakingCondition;
        private SProgram stepFunction;
        private String recursiveArgument;
        private List<String> nativeInputs;
    }
}
