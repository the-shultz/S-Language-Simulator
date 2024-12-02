package mta.computional.slanguage.simpl.factory;


import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.basic.impl.Decrease;
import mta.computional.slanguage.simpl.instruction.basic.impl.Increase;
import mta.computional.slanguage.simpl.instruction.basic.impl.JumpNoZero;
import mta.computional.slanguage.simpl.instruction.basic.impl.Neutral;
import mta.computional.slanguage.simpl.instruction.synthetic.impl.AssignZero;
import mta.computional.slanguage.simpl.label.LabelImpl;
import mta.computional.slanguage.simpl.program.SProgramImpl;
import mta.computional.slanguage.simpl.program.SProgramRunnerImpl;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.SProgram;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;

import java.util.List;

import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EMPTY;

public interface SComponentFactory {

    static Label createLabel(String labelName) {
        return new LabelImpl(labelName);
    }

    static SInstruction createInstruction(SInstructionRegistry instructionCode, String variableName) {
        return createInstruction(instructionCode, variableName, EMPTY);
    }

    static SInstruction createInstruction(SInstructionRegistry instructionCode, String variableName, Label jumpLabel) {
        return switch (instructionCode) {
            case NEUTRAL -> new Neutral(variableName);
            case INCREASE -> new Increase(variableName);
            case DECREASE -> new Decrease(variableName);
            case JUMP_NOT_ZERO -> new JumpNoZero(variableName, jumpLabel);
            case ZERO_VARIABLE -> new AssignZero(variableName);
        };
    }

    static SInstruction createInstructionWithLabel(Label instructionLabel, SInstructionRegistry instructionCode, String variableName) {
        return createInstructionWithLabel(instructionLabel, instructionCode, variableName, EMPTY);
    }

    static SInstruction createInstructionWithLabel(Label instructionLabel, SInstructionRegistry instructionCode, String variableName, Label jumpLabel) {
        return switch (instructionCode) {
            case NEUTRAL -> new Neutral(instructionLabel, variableName);
            case INCREASE -> new Increase(instructionLabel, variableName);
            case DECREASE -> new Decrease(instructionLabel, variableName);
            case JUMP_NOT_ZERO -> new JumpNoZero(instructionLabel, variableName, jumpLabel);
            case ZERO_VARIABLE -> new AssignZero(instructionLabel, variableName);
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
