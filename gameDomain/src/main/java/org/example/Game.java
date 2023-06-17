package org.example;

import java.io.Serializable;

public class Game implements Entity<Integer>, Serializable {
    private int id;

    @Override
    public void setId(Integer integer) {
        this.id = integer;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
