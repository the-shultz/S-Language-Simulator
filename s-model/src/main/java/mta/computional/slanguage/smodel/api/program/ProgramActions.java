package mta.computional.slanguage.smodel.api.program;

import mta.computional.slanguage.smodel.api.label.Label;

import java.util.Map;

public interface ProgramActions {
    Label createAvailableLabel();
    String createFreeWorkingVariable();

    void replaceVariable(Map<String, String> workingVariablesReplacements);
    void replaceLabels(Map<Label, Label> labelsReplacements);
}
