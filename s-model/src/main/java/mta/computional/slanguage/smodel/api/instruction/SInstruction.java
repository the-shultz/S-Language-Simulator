package mta.computional.slanguage.smodel.api.instruction;

import mta.computional.slanguage.smodel.api.program.SProgramRunner;
import mta.computional.slanguage.smodel.api.label.Label;

import java.util.List;

public interface SInstruction {

    String getName();
    long decode();

    boolean hasLabel();
    Label getLabel();

    boolean isSynthetic();

    List<SInstruction> expand();
    String toString();

    Label execute(SProgramRunner programRunner);

    SInstruction STOP = new SInstruction() {
        @Override
        public String getName() {
            return "Stop";
        }

        @Override
        public long decode() {
            return 0;
        }

        @Override
        public boolean hasLabel() {
            return true;
        }

        @Override
        public Label getLabel() {
            return Label.EXIT;
        }

        @Override
        public boolean isSynthetic() {
            return false;
        }

        @Override
        public List<SInstruction> expand() {
            return List.of(this);
        }

        @Override
        public String toString() {
            return getName();
        }

        @Override
        public Label execute(SProgramRunner programRunner) {
            return Label.EXIT;
        }
    };
}
