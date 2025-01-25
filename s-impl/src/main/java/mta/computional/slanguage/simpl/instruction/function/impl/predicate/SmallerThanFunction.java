package mta.computional.slanguage.simpl.instruction.function.impl.predicate;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.function.factory.FunctionFactory;
import mta.computional.slanguage.simpl.program.SProgramImpl;

import java.util.List;
import java.util.Map;

import static mta.computional.slanguage.simpl.instruction.function.factory.SFunction.*;
import static mta.computional.slanguage.simpl.instruction.function.factory.SFunction.SMALLER_EQUAL_THAN;

public class SmallerThanFunction extends SProgramImpl {

    public SmallerThanFunction() {
        super(SMALLER_THAN.toString());

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(AND.toString())
                        .functionsImplementations(Map.of(
                                AND.toString(), FunctionFactory.createFunction(AND),
                                NOT.toString(), FunctionFactory.createFunction(NOT),
                                EQUALITY.toString(), FunctionFactory.createFunction(EQUALITY),
                                SMALLER_EQUAL_THAN.toString(), FunctionFactory.createFunction(SMALLER_EQUAL_THAN)
                        ))
                        .sourceFunctionInputs(List.of("(<=,x1,x2)","(!,(==,x2,x1))"))
                        .build())
                .build();

        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", additionalArguments));

    }
}
