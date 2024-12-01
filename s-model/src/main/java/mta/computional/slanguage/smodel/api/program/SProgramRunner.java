package mta.computional.slanguage.smodel.api.program;

public interface SProgramRunner {

    void updateVariable(String name, long value);

    long getVariable(String variableName);

    long run(Integer... input);
}
