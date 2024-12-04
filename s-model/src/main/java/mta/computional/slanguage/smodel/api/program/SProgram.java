package mta.computional.slanguage.smodel.api.program;

import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;

import java.io.Serializable;
import java.util.List;

public interface SProgram extends ProgramActions, Serializable {

    String getName();
    void addInstruction(SInstruction instruction);

    long decode();
    String toVerboseString();
    int length();
    SProgram expand();
    SInstruction getInstructionAt(int index);
    SInstruction getFirstInstructionByLabel(Label label);
}
