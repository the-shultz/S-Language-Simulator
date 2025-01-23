package mta.computional.slanguage.simpl.instruction.function.impl.predicate;

import mta.computional.slanguage.simpl.factory.AdditionalArguments;
import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.function.factory.FunctionFactory;
import mta.computional.slanguage.simpl.instruction.function.impl.MinusFunction;
import mta.computional.slanguage.simpl.program.SProgramImpl;

import java.util.List;
import java.util.Map;

import static mta.computional.slanguage.simpl.instruction.function.factory.SFunction.*;

public class SmallerEqualThanFunction extends SProgramImpl {

    public SmallerEqualThanFunction() {
        super(SMALLER_EQUAL_THAN.toString());

        AdditionalArguments additionalArguments = AdditionalArguments
                .builder()
                .functionCallData(AdditionalArguments.FunctionCallData.builder()
                        .sourceFunctionName(NOT.toString())
                        .functionsImplementations(Map.of(
                                NOT.toString(), FunctionFactory.createFunction(NOT),
                                MINUS.toString(), FunctionFactory.createFunction(MINUS)))
                        .sourceFunctionInputs(List.of("(" + MINUS + ",x1,x2)"))
                        .build())
                .build();

        addInstruction(SComponentFactory.createInstruction(SInstructionRegistry.APPLY_FUNCTION, "y", additionalArguments));

    }
}
