package mta.computional.slanguage.smodel.api.label;

public interface Label {
    String label();
    String toVerboseString();

    Label EXIT = new Label() {
        @Override
        public String label() {
            return "EXIT";
        }

        @Override
        public String toVerboseString() {
            return "E";
        }
    };

    Label EMPTY = new Label() {
        @Override
        public String label() {
            return "EMPTY";
        }

        @Override
        public String toVerboseString() {
            return "";
        }
    };
}
