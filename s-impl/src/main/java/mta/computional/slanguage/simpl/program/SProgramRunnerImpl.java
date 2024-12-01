package mta.computional.slanguage.simpl.program;

import mta.computional.slanguage.smodel.api.program.SProgram;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SProgramRunnerImpl implements SProgramRunner {

    private final SProgram program;
    private final Map<String, Long> variables;

    public SProgramRunnerImpl(SProgram program) {
        this.program = program;
        variables = new HashMap<>();
    }

    @Override
    public void updateVariable(String name, long value) {
        variables.put(name, value);
    }

    @Override
    public long getVariable(String variableName) {
        return Optional
                .ofNullable(variables.get(variableName))
                .orElse(0L);
    }

    @Override
    public long run(List<Long> input) {
        return 0;
    }
}
