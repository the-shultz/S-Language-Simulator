package mta.computional.slanguage.smodel.api.program;

import java.util.List;

public interface SProgramRunner {

    void updateVariable(String name, long value);

    long getVariable(String variableName);

    long run(List<Long> input);
}
