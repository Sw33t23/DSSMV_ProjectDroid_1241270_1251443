package model;
import exceptions.InvalidDataException;
public class EcoGuide {
    private String name;
    private UserContainer userContainer;

    public EcoGuide(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("EcoGuide: name = NULL or empty");
        }
        this.name = name;
        this.userContainer = new UserContainer();
    }

    public String getName() {
        return name;
    }

    public UserContainer getUserContainer() {
        return userContainer;
    }

    @Override
    public String toString() {
        return "EcoGuide Application: " + name;
    }
}
