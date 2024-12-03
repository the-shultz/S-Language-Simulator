package mta.computional.slanguage.smodel.api.label;

import java.io.Serializable;

public interface Label extends Serializable {
    String label();
    String toVerboseString();
}
