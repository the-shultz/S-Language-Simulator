package mta.computional.slanguage.smodel.api;

public interface Label {
    String getLabel();

    Label EXIT = new Label() {
        @Override
        public String getLabel() {
            return "EXIT";
        }
    };

    Label EMPTY = new Label() {
        @Override
        public String getLabel() {
            return "EMPTY";
        }
    };
}
