package model;
import exceptions.InvalidDataException;
import java.time.LocalDate;
public class RecyclingRecord {
    private User user;
    private Material material;
    private double quantity;
    private LocalDate date;

    public RecyclingRecord(User user, Material material, double quantity, LocalDate date) {
        if (user == null) throw new InvalidDataException("RecyclingRecord: user = NULL");
        if (material == null) throw new InvalidDataException("RecyclingRecord: material = NULL");
        if (quantity <= 0) throw new InvalidDataException("RecyclingRecord: quantity = " + quantity);
        if (date == null) throw new InvalidDataException("RecyclingRecord: date = NULL");

        this.user = user;
        this.material = material;
        this.quantity = quantity;
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public Material getMaterial() {
        return material;
    }

    public double getQuantity() {
        return quantity;
    }

    public LocalDate getDate() {
        return date;
    }

    public double calculatePoints() {
        return quantity * material.getPointsPerUnit();
    }

    @Override
    public String toString() {
        return "RecyclingRecord{" +
                "user=" + user +
                ", material=" + material +
                ", quantity=" + quantity +
                ", date=" + date +
                ", points=" + calculatePoints() +
                '}';
    }
}
