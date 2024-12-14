package mta.computional.slanguage.simpl.program;

import mta.computional.slanguage.simpl.factory.SComponentFactory;
import mta.computional.slanguage.simpl.label.LabelImpl;
import mta.computional.slanguage.smodel.api.instruction.SInstruction;
import mta.computional.slanguage.smodel.api.label.Label;
import mta.computional.slanguage.smodel.api.program.SProgram;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static mta.computional.slanguage.smodel.api.instruction.SInstruction.STOP;

public class SProgramImpl implements SProgram {

    public static final String LABEL_NAMING_FORMAT = "L%d";
    private final List<SInstruction> instructions;
    private final String name;
    private final Set<Label> labels;
    private final Set<String> workingVariables;
    private int nextLabelId;
    private int nextWorkingVariableId;

    public SProgramImpl(String name) {
        this(name, new ArrayList<>());
    }

    public SProgramImpl(String name, List<SInstruction> instructions) {
        this.name = name;
        this.instructions = instructions;

        if (!instructions.isEmpty()) {

            for (int i = 1; i < instructions.size(); i++) {
                instructions.get(i - 1).setNextInstructionInOrder(instructions.get(i));
            }

            instructions.getLast().setNextInstructionInOrder(STOP);
        }

        nextLabelId = 0;
        labels = instructions
            .stream()
            .filter(SInstruction::hasLabel)
            .map(SInstruction::getLabel)
            .collect(Collectors.toSet());

        nextWorkingVariableId = 0;
        workingVariables = instructions
                .stream()
                .flatMap(s -> s.getVariables().stream())
                .collect(Collectors.toSet());
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

        labels.add(instruction.getLabel());
        workingVariables.addAll(instruction.getVariables());
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
    public String createFreeWorkingVariable() {
        String freeWorkingVariable;
        do {
            nextWorkingVariableId++;
            freeWorkingVariable = "z" + nextWorkingVariableId;
        } while (workingVariables.contains(freeWorkingVariable));
        workingVariables.add(freeWorkingVariable);
        return freeWorkingVariable;
    }

    @Override
    public void replaceVariable(Map<String, String> workingVariablesReplacements) {
        workingVariablesReplacements
                .forEach((oldVariable, newVariable) -> {
                    instructions.forEach(instruction -> instruction.replaceVariable(oldVariable, newVariable));
                    workingVariables.remove(oldVariable);
                    workingVariables.add(newVariable);
        });
    }

    @Override
    public void replaceLabels(Map<Label, Label> labelsReplacements) {
        labelsReplacements
                .forEach((oldLabel, newLabel) -> {
                    instructions.forEach(instruction -> instruction.replaceLabel(oldLabel, newLabel));
                    labels.remove(oldLabel);
                    labels.add(newLabel);
                });
    }

    @Override
    public int length() {
        return instructions.size();
    }

    @Override
    public SProgram expand(int degree) {
        List<SInstruction> expandInstructions = deepCopyInstructions();
        int degreeCounter = 0;
        boolean hasSynthetic;
        do {
            degreeCounter++;

            expandInstructions = expandInstructions
                    .stream()
                    .flatMap(instruction -> instruction.expand(this).stream())
                    .collect(Collectors.toList());

            hasSynthetic = expandInstructions
                    .stream()
                    .anyMatch(SInstruction::isSynthetic);

        } while (degreeCounter < degree && hasSynthetic);

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

    @Override
    public SProgram duplicate() {
        return new SProgramImpl(name, instructions);
    }

    @Override
    public Set<String> getUsedVariables() {
        return workingVariables;
    }

    @Override
    public Set<Label> getUsedLabels() {
        return labels;
    }

    @Override
    public Collection<? extends SInstruction> getInstructions() {
        return instructions;
    }

    private List<SInstruction> deepCopyInstructions() {
        // create a duplicate of instructions using serialization
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(instructions);
            out.flush();
            bos.flush();
            byte[] data = bos.toByteArray();

            try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
                 ObjectInputStream in = new ObjectInputStream(bis)) {
                return (List<SInstruction>) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deep copy instructions", e);
        }

    }
}
