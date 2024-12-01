package mta.computional.slanguage.smodel.api;

import java.util.List;

public interface SProgram {

    long decode();
    String toString();
    int length();
    List<SInstruction> expand();
    SInstruction getInstructionAt(int index);
    SInstruction getInstructionByLabel(Label label);
}
