package mta.computional.slanguage.simpl.instruction.function.factory;

public enum SFunction {
    ID,
    SUCCESSOR {
        @Override
        public String toString() {
            return "S";
        }
    },
    PROJECTION {
        @Override
        public String toString() {
            return "U";
        }
    },
    CONST,
    ADD {
        @Override
        public String toString() {
            return "+";
        }
    },
    MULTIPLY {
        @Override
        public String toString() {
            return "*";
        }
    },
    MINUS {
        @Override
        public String toString() {
            return "-";
        }
    },
    NOT {
        @Override
        public String toString() {
            return "!";
        }
    },
    SMALLER_EQUAL_THAN {
        @Override
        public String toString() {
            return "<=";
        }
    },
    AND {
        @Override
        public String toString() {
            return "&&";
        }
    },

}
