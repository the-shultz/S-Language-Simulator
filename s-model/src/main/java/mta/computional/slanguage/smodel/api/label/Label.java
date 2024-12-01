package mta.computional.slanguage.smodel.api.label;

public interface Label {
    String label();

    Label EXIT = new Label() {
        @Override
        public String label() {
            return "EXIT";
        }
    };

    Label EMPTY = new Label() {
        @Override
        public String label() {
            return "EMPTY";
        }
    };
}
