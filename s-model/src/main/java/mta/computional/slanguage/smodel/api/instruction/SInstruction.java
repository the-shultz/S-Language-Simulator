package mta.computional.slanguage.smodel.api.instruction;

import mta.computional.slanguage.smodel.api.program.ExecutionContext;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.ProgramActions;

import java.io.Serializable;
import java.util.List;

import static mta.computional.slanguage.smodel.api.label.ConstantLabel.EXIT;

public interface SInstruction extends Serializable {

    String getName();
    long decode();
    List<String> getVariables();

    boolean hasLabel();
    Label getLabel();

    boolean isSynthetic();

    List<SInstruction> expand(ProgramActions context);
    String toVerboseString();

    Label execute(ExecutionContext context);

    SInstruction next();
    void setNextInstructionInOrder(SInstruction nextInstructionInOrder);

    void replaceVariable(String oldVariable, String newVariable);

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
        public List<String> getVariables() {
            return List.of();
        }

        @Override
        public boolean hasLabel() {
            return true;
        }

        @Override
        public Label getLabel() {
            return EXIT;
        }

        @Override
        public boolean isSynthetic() {
            return false;
        }

        @Override
        public List<SInstruction> expand(ProgramActions context) {
            return List.of();
        }

        @Override
        public String toVerboseString() {
            return "Program Stopped";
        }

        @Override
        public String toString() {
            return getName();
        }

        @Override
        public Label execute(ExecutionContext context) {
            return EXIT;
        }

        @Override
        public SInstruction next() {
            return this;
        }

        @Override
        public void setNextInstructionInOrder(SInstruction nextInstructionInOrder) {

        }

        @Override
        public void replaceVariable(String oldVariable, String newVariable) {

        }
    };
}
