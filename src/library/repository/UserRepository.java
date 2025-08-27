package library.repository;

import library.model.User;
import library.util.JsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private static final String FILE_NAME = "users.json";
    private List<User> users = new ArrayList<>();

    public List<User> getAll() {
        return users;
    }

    public void add(User user){
        users.add(user);
        save();
    }

    public void remove(int userId){
        users.removeIf(u -> u.id == userId);
        save();
    }

    public Optional<User> findById(int userId){
        return users.stream().filter(u -> u.id == userId).findFirst();
    }

    public void save(){
        JsonUtil.saveToFile(FILE_NAME, users);
    }

    public void load(){
        File file = new File(FILE_NAME);
        if (file.exists()){
            User[] loaded = JsonUtil.loadFromFile(FILE_NAME, User[].class);
            if (loaded != null){
                users = new ArrayList<>(Arrays.asList(loaded));
            }
        }
    }
}
