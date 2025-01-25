package mta.computional.slanguage.simpl.instruction.function.impl.predicate;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.function.factory.FunctionFactory;
import mta.computional.slanguage.simpl.program.SProgramImpl;

import java.util.List;
import java.util.Map;

import static mta.computional.slanguage.simpl.instruction.function.factory.SFunction.*;

public class EqualityFunction extends SProgramImpl {

    public EqualityFunction() {
        super(EQUALITY.toString());

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(AND.toString())
                        .functionsImplementations(Map.of(
                                AND.toString(), FunctionFactory.createFunction(AND),
                                SMALLER_EQUAL_THAN.toString(), FunctionFactory.createFunction(SMALLER_EQUAL_THAN)
                        ))
                        .sourceFunctionInputs(List.of("(<=,x1,x2)","(<=,x2,x1)"))
                        .build())
                .build();

        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", additionalArguments));

    }
}
