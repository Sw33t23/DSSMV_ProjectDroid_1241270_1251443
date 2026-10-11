package model;
import exceptions.DuplicatedDataException;
import exceptions.InvalidDataException;
import java.util.ArrayList;
import java.util.List;
public class UserContainer {
    private List<User> users = new ArrayList<>();

    private User search(int id) {
        for (User u : users) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    public List<User> getAll() {
        return new ArrayList<>(users);
    }

    public User get(int id) {
        return search(id);
    }

    public void add(User obj) {
        if (obj == null) {
            throw new InvalidDataException("UserContainer: user = NULL");
        }
        if (search(obj.getId()) != null) {
            throw new DuplicatedDataException("User ID já existente: " + obj.getId());
        }
        users.add(obj);
    }

    public void remove(int id) {
        User u = search(id);
        if (u == null) {
            throw new InvalidDataException("UserContainer: utilizador com ID " + id + " não encontrado");
        }
        users.remove(u);
    }
}
