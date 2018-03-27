package se.juneday.substitutescheduler.domain;

public class Substitute {
    private String name;
    private int id;

    public Substitute(String name, int id) {
        this.name = name;
        this.id=id;
    }

    public String name() {
        return name;
    }

    public String toString() {
        return name;
    }

    public int id() {
        return id;
    }

}
