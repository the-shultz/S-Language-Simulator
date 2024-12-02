package mta.computional.slanguage.smodel.api.label;

public enum ConstantLabel implements Label {
    EXIT("EXIT", "E"),
    EMPTY("EMPTY", "");

    private final String label;
    private final String verboseLabel;

    ConstantLabel(String label, String verboseLabel) {
        this.label = label;
        this.verboseLabel = verboseLabel;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public String toVerboseString() {
        return verboseLabel;
    }
}