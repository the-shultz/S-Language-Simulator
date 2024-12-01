package mta.computional.slanguage.simpl.program;

import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.SProgram;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SProgramImpl implements SProgram {

    private final List<SInstruction> instructions;

    public SProgramImpl() {
        this.instructions = new ArrayList<>();
    }

    public SProgramImpl(List<SInstruction> instructions) {
        this.instructions = instructions;
    }

    @Override
    public void addInstruction(SInstruction instruction) {
        instructions.add(instruction);
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    public String toVerboseString() {

        // add [line number] before each instruction
        AtomicInteger lineNumber = new AtomicInteger(1);
        return instructions
                .stream()
                .map(SInstruction::toVerboseString)
                .map(instruction -> String.format("%02d %s", lineNumber.getAndIncrement(), instruction))
                .collect(Collectors.joining("\n"));
    }

    @Override
    public int length() {
        return instructions.size();
    }

    @Override
    public List<SInstruction> expand() {
        return instructions
                .stream()
                .flatMap(instruction -> instruction.expand().stream())
                .collect(Collectors.toList());
    }

    @Override
    // 1 based index
    public SInstruction getInstructionAt(int index) {
        if (index <= instructions.size()) {
            return instructions.get(index - 1);
        }
        return SInstruction.STOP;
    }

    @Override
    public SInstruction getInstructionByLabel(Label label) {
        for (SInstruction instruction : instructions) {
            if (instruction.hasLabel() && instruction.getLabel().equals(label)) {
                return instruction;
            }
        }
        return SInstruction.STOP;
    }
}
