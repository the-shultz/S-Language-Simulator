package mta.computional.slanguage.simpl.program;

import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.label.LabelImpl;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.SProgram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static mta.computional.slanguage.smodel.api.instruction.SInstruction.STOP;

public class SProgramImpl implements SProgram {

    public static final String LABEL_NAMING_FORMAT = "L%d";
    private final List<SInstruction> instructions;
    private final String name;
    private final Set<Label> labels;
    private int nextLabelId;

    public SProgramImpl(String name) {
        this(name, new ArrayList<>());
    }

    public SProgramImpl(String name, List<SInstruction> instructions) {
        this.name = name;
        this.instructions = instructions;
        labels = new HashSet<>();
        nextLabelId = 0;

        if (!instructions.isEmpty()) {

            for (int i = 1; i < instructions.size(); i++) {
                instructions.get(i - 1).setNextInstructionInOrder(instructions.get(i));
            }

            instructions.getLast().setNextInstructionInOrder(STOP);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addInstruction(SInstruction instruction) {
        SInstruction wasLastInstruction = instructions.isEmpty() ? null : instructions.getLast();

        if (wasLastInstruction != null) {
            wasLastInstruction.setNextInstructionInOrder(instruction);
        }

        instructions.add(instruction);
        instruction.setNextInstructionInOrder(STOP);
    }

    @Override
    public long decode() {
        return 0;
    }

    @Override
    public String toVerboseString() {

        // add [line number] before each instruction
        AtomicInteger lineNumber = new AtomicInteger(1);
        String programInstructions = instructions
                .stream()
                .map(SInstruction::toVerboseString)
                .map(instruction -> String.format("%02d %s", lineNumber.getAndIncrement(), instruction))
                .collect(Collectors.joining("\n"));
        return String.format("Program: %s\n%s", name, programInstructions);
    }

    @Override
    public Label createAvailableLabel() {
        Label label;
        do {
            nextLabelId++;
             label = new LabelImpl(String.format(LABEL_NAMING_FORMAT, nextLabelId));
        } while (labels.contains(label));
        labels.add(label);
        return label;
    }

    @Override
    public int length() {
        return instructions.size();
    }

    @Override
    public SProgram expand() {
        List<SInstruction> expandInstructions = instructions;
        boolean hasSynthetic;
        do {
            expandInstructions = expandInstructions
                    .stream()
                    .flatMap(instruction -> instruction.expand(this).stream())
                    .collect(Collectors.toList());

            hasSynthetic = expandInstructions
                    .stream()
                    .anyMatch(SInstruction::isSynthetic);

        } while (hasSynthetic);

        return SComponentFactory.createProgramWithInstructions("! EXPANDED ! " + name, expandInstructions);
    }

    @Override
    // 1 based index
    public SInstruction getInstructionAt(int index) {
        if (index <= instructions.size()) {
            return instructions.get(index - 1);
        }
        return STOP;
    }

    @Override
    public SInstruction getFirstInstructionByLabel(Label label) {
        for (SInstruction instruction : instructions) {
            if (instruction.hasLabel() && instruction.getLabel().equals(label)) {
                return instruction;
            }
        }
        return STOP;
    }
}
