package mta.computional.slanguage.smodel.api.program;

import java.util.Map;

public interface SProgramRunner extends ExecutionContext {

    long run(Long... input);
    Map<String, Long> variableState();
}
