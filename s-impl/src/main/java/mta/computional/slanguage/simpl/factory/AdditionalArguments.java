package mta.computional.slanguage.simpl.factory;

import lombok.Builder;
import lombok.Getter;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.SProgram;

import java.util.List;
import java.util.Map;

@Builder
@Getter
public class AdditionalArguments {

    private Label jumpNotZeroLabel;
    private Label gotoLabel;
    private String assignedVariableName;
    private int constantValue;
    private FunctionCallData functionCallData;

    static AdditionalArguments EMPTY = AdditionalArguments.builder().build();

    @Builder
    @Getter
    public static class FunctionCallData {
        private String sourceFunctionName;
        private Map<String, SProgram> functionsImplementations;
        private List<String> sourceFunctionInputs;
    }
}
