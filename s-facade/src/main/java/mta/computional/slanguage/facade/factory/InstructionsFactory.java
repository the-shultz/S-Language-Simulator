package mta.computional.slanguage.facade.factory;


import mta.computional.slanguage.simpl.instruction.SInstructionRegistry;
import mta.computional.slanguage.simpl.instruction.basic.impl.Decrease;
import mta.computional.slanguage.simpl.instruction.basic.impl.Increase;
import mta.computional.slanguage.simpl.instruction.basic.impl.JumpNoZero;
import mta.computional.slanguage.simpl.instruction.basic.impl.Neutral;
import mta.computional.slanguage.simpl.label.LabelImpl;
import mta.computional.slanguage.simpl.program.SProgramImpl;
import mta.computional.slanguage.simpl.program.SProgramRunnerImpl;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.SProgram;
import mta.computional.slanguage.smodel.api.program.SProgramRunner;

public class InstructionsFactory {

    public static Label createLabel(String labelName) {
        return new LabelImpl(labelName);
    }

    public static SInstruction createInstruction(SInstructionRegistry instructionCode, String variableName) {
        return createInstruction(instructionCode, variableName, Label.EMPTY);
    }

    public static SInstruction createInstruction(SInstructionRegistry instructionCode, String variableName, Label jumpLabel) {
        return switch (instructionCode) {
            case NEUTRAL -> new Neutral(variableName);
            case INCREASE -> new Increase(variableName);
            case DECREASE -> new Decrease(variableName);
            case JUMP_NOT_ZERO -> new JumpNoZero(variableName, jumpLabel);
        };
    }

    public static SInstruction createInstructionWithLabel(Label instructionLabel, SInstructionRegistry instructionCode, String variableName) {
        return createInstructionWithLabel(instructionLabel, instructionCode, variableName, Label.EMPTY);
    }

    public static SInstruction createInstructionWithLabel(Label instructionLabel, SInstructionRegistry instructionCode, String variableName, Label jumpLabel) {
        return switch (instructionCode) {
            case NEUTRAL -> new Neutral(instructionLabel, variableName);
            case INCREASE -> new Increase(instructionLabel, variableName);
            case DECREASE -> new Decrease(instructionLabel, variableName);
            case JUMP_NOT_ZERO -> new JumpNoZero(instructionLabel, variableName, jumpLabel);
        };
    }

    public static SProgram createEmptyProgram() {
        return new SProgramImpl();
    }

    public static SProgramRunner createProgramRunner(SProgram program) {
        return new SProgramRunnerImpl(program);
    }
}
