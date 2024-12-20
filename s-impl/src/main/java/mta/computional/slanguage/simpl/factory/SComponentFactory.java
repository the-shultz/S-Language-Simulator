package mta.computional.slanguage.simpl.factory;


import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.basic.impl.Decrease;
import mta.computional.slanguage.simpl.instruction.basic.impl.Increase;
import mta.computional.slanguage.simpl.instruction.basic.impl.JumpNoZero;
import mta.computional.slanguage.simpl.instruction.basic.impl.Neutral;
import mta.computional.slanguage.simpl.instruction.function.impl.ApplyFunction;
import mta.computional.slanguage.simpl.instruction.synthetic.impl.AssignZero;
import mta.computional.slanguage.simpl.instruction.synthetic.impl.Assignment;
import mta.computional.slanguage.simpl.instruction.synthetic.impl.ConstantAssignment;
import mta.computional.slanguage.simpl.instruction.synthetic.impl.GotoLabel;
import mta.computional.slanguage.simpl.label.LabelImpl;
import mta.computional.slanguage.simpl.program.SProgramImpl;
import mta.computional.slanguage.simpl.program.SProgramRunnerImpl;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.SProgram;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;

import java.util.List;

public interface SComponentFactory {

    static Label createLabel(String labelName) {
        return new LabelImpl(labelName);
    }

    static SInstruction createInstruction(SInstructionRegistry instructionCode, String variableName) {
        return createInstruction(instructionCode, variableName, AdditionalArguments.EMPTY);
    }

    static SInstruction createInstruction(SInstructionRegistry instructionCode, String variableName, AdditionalArguments additionalArguments) {
        return switch (instructionCode) {
            case NEUTRAL -> new Neutral(variableName);
            case INCREASE -> new Increase(variableName);
            case DECREASE -> new Decrease(variableName);
            case JUMP_NOT_ZERO -> new JumpNoZero(variableName, additionalArguments.getJumpNotZeroLabel());
            case ZERO_VARIABLE -> new AssignZero(variableName);
            case ASSIGNMENT -> new Assignment(variableName, additionalArguments.getAssignedVariableName());
            case GOTO_LABEL -> new GotoLabel(variableName, additionalArguments.getGotoLabel());
            case CONSTANT_ASSIGNMENT -> new ConstantAssignment(variableName, additionalArguments.getConstantValue());
            case APPLY_FUNCTION -> new ApplyFunction(variableName, additionalArguments.getFunctionCallData().getSourceFunctionName(), additionalArguments.getFunctionCallData().getFunctionsImplementations(), additionalArguments.getFunctionCallData().getSourceFunctionInputs());
        };
    }

    static SInstruction createInstructionWithLabel(Label instructionLabel, SInstructionRegistry instructionCode, String variableName) {
        return createInstructionWithLabel(instructionLabel, instructionCode, variableName, AdditionalArguments.EMPTY);
    }

    static SInstruction createInstructionWithLabel(Label instructionLabel, SInstructionRegistry instructionCode, String variableName, AdditionalArguments additionalArguments) {
        return switch (instructionCode) {
            case NEUTRAL -> new Neutral(instructionLabel, variableName);
            case INCREASE -> new Increase(instructionLabel, variableName);
            case DECREASE -> new Decrease(instructionLabel, variableName);
            case JUMP_NOT_ZERO -> new JumpNoZero(instructionLabel, variableName, additionalArguments.getJumpNotZeroLabel());
            case ZERO_VARIABLE -> new AssignZero(instructionLabel, variableName);
            case ASSIGNMENT -> new Assignment(instructionLabel, variableName, additionalArguments.getAssignedVariableName());
            case GOTO_LABEL -> new GotoLabel(instructionLabel, variableName, additionalArguments.getGotoLabel());
            case CONSTANT_ASSIGNMENT -> new ConstantAssignment(instructionLabel, variableName, additionalArguments.getConstantValue());
            case APPLY_FUNCTION -> new ApplyFunction(instructionLabel, variableName, additionalArguments.getFunctionCallData().getSourceFunctionName(), additionalArguments.getFunctionCallData().getFunctionsImplementations(), additionalArguments.getFunctionCallData().getSourceFunctionInputs());
        };
    }

    static SProgram createEmptyProgram(String name) {
        return new SProgramImpl(name);
    }

    static SProgram createProgramWithInstructions(String name, List<SInstruction> instructions) {
        return new SProgramImpl(name, instructions);
    }

    static SProgramRunner createProgramRunner(SProgram program) {
        return new SProgramRunnerImpl(program);
    }
}
