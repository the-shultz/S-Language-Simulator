package mta.computional.slanguage.simpl.program;

import mta.computional.slanguage.smodel.api.program.SProgram;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;

import java.util.HashMap;
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
    public long run(Integer... input) {
        for (int i = 0; i < input.length; i++) {
            variables.put("X" + (i+1), (long)input[i]);
        }

        for (int i = 0; i < program.length(); i++) {
            program.getInstructionAt(i).execute(this);
        }

        // Y is the output variable
        return variables.get("Y");
    }
}
