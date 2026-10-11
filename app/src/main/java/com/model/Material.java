package model;
import exceptions.InvalidDataException;
public class Material {
    private String name;
    private double pointsPerUnit;

    public Material(String name, double pointsPerUnit) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Material: name = NULL or empty");
        }
        if (pointsPerUnit <= 0) {
            throw new InvalidDataException("Material: pointsPerUnit <= 0");
        }
        this.name = name;
        this.pointsPerUnit = pointsPerUnit;
    }

    public String getName() {
        return name;
    }

    public double getPointsPerUnit() {
        return pointsPerUnit;
    }

    @Override
    public String toString() {
        return name + " (" + pointsPerUnit + " pts/un)";
    }
}

