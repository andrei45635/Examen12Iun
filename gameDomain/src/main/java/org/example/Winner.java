package org.example;

import java.io.Serializable;

public class Winner implements Entity<Integer>, Serializable {
    private int id;
    private String Username;
    private int Tries;
    private String WinningHint;

    public Winner(String username, int tries, String message) {
        Username = username;
        Tries = tries;
        WinningHint = message;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public int getTries() {
        return Tries;
    }

    public void setTries(int tries) {
        Tries = tries;
    }

    public String getWinningHint() {
        return WinningHint;
    }

    public void setWinningHint(String message) {
        WinningHint = message;
    }

    @Override
    public void setId(Integer integer) {
        this.id = integer;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
