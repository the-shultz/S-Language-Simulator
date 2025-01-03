package mta.computional.slanguage.simpl.program;

import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ExecutionContext;
import mta.computional.slanguage.smodel.api.program.SProgram;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;
import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EXIT;

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
    public long getAndRemoveVariable(String variableName) {
        return Optional
                .ofNullable(variables.remove(variableName))
                .orElse(0L);
    }

    @Override
    public ExecutionContext duplicate() {
        return generateDuplicateFrom(variables);
    }

    private ExecutionContext generateDuplicateFrom(Map<String, Long> source) {
        return new ExecutionContext() {

            private final Map<String, Long> variables = new HashMap<>(source);

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
            public long getAndRemoveVariable(String variableName) {
                return Optional
                        .ofNullable(variables.remove(variableName))
                        .orElse(0L);
            }

            @Override
            public ExecutionContext duplicate() {
                return generateDuplicateFrom(variables);
            }
        };
    }

    @Override
    public long run(Long... input) {

        // empty program edge case
        if (program.length() == 0) {
            return 0;
        }

        // initialize input variables
        for (int i = 0; i < input.length; i++) {
            variables.put("x" + (i+1), (long)input[i]);
        }

        // fetch first instruction
        SInstruction nextInstruction = program.getInstructionAt(1);

        // work the program until reaching STOP command
        while (nextInstruction != SInstruction.STOP) {

            Label nextInstructionLabel = nextInstruction.execute(this);
            switch (nextInstructionLabel) {
                case EXIT:
                    nextInstruction = SInstruction.STOP;
                case EMPTY:
                    nextInstruction = nextInstruction.next();
                    break;
                default:
                    nextInstruction = program.getFirstInstructionByLabel(nextInstructionLabel);
            }
        }

        // y is the output variable
        return Optional.ofNullable(variables.get("y")).orElse(0L);
    }

    @Override
    public Map<String, Long> variableState() {
        return variables;
    }
}
