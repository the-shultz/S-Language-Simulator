package mta.computional.slanguage.smodel.api.program;

public interface SProgramRunner {

    void updateVariable(String name, long value);

    int getVariable(String variableName);
}
