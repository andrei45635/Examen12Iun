package org.example;

import java.io.Serializable;

public interface Entity<ID> {
    void setId(ID id);
    ID getId();
}