package mta.computional.slanguage.smodel.api;

import java.util.List;

public interface SInstruction {

    String getName();
    long decode();

    boolean hasLabel();
    String getLabel();

    boolean isSynthetic();

    List<SInstruction> expand();
    String toString();

    Label execute(SProgramRunner programRunner);

}
