package org.example;

import java.io.Serializable;
import java.util.Objects;

public class Configuration implements Entity<Integer>, Serializable {
    private int id;
    private int posX;
    private int posY;
    private String message;

    public Configuration() {}

    public Configuration(int posX, int posY, String message) {
        this.posX = posX;
        this.posY = posY;
        this.message = message;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void setId(Integer integer) {
        this.id = integer;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return id == that.id && posX == that.posX && posY == that.posY && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, posX, posY, message);
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "id=" + id +
                ", posX=" + posX +
                ", posY=" + posY +
                ", message='" + message + '\'' +
                '}';
    }
}
