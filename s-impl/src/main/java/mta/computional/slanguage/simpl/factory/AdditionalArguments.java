package mta.computional.slanguage.simpl.factory;

import lombok.Builder;
import lombok.Getter;
import mta.computional.slanguage.smodel.api.label.Label;

@Builder
@Getter
public class AdditionalArguments {

    private Label jumpNotZeroLabel;
    private Label gotoLabel;
    private String assignedVariableName;

    static AdditionalArguments EMPTY = AdditionalArguments.builder().build();
}
