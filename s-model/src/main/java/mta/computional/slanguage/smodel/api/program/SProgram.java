package mta.computional.slanguage.smodel.api.program;

import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;

import java.util.List;

public interface SProgram {

    long decode();
    String toString();
    int length();
    List<SInstruction> expand();
    SInstruction getInstructionAt(int index);
    SInstruction getInstructionByLabel(Label label);
}
