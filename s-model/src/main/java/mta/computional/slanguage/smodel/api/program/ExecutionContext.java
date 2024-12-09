package mta.computional.slanguage.smodel.api.program;

public interface ExecutionContext {

    void updateVariable(String name, long value);

    long getVariable(String variableName);

    ExecutionContext duplicate();
}
