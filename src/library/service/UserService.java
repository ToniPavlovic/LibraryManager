package library.service;

import library.model.User;
import library.repository.UserRepository;

import java.util.List;

public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo){
        this.repo = repo;
    }

    public void registerUser(String name, String password, boolean isAdmin, User loggedInUser){
        if(!repo.getAll().isEmpty()){
            if (loggedInUser == null || !loggedInUser.isAdmin){
                throw new RuntimeException("Only admins can register users!");
            }
        }

        int nextId = repo.getAll().stream().mapToInt(u -> u.id).max().orElse(0) +1;

        if (repo.getAll().isEmpty()){
            isAdmin = true;
        }

        User newUser = new User(nextId, name, password, isAdmin);
        repo.add(newUser);
    }

    public User login(String name, String password){
        return repo.getAll().stream()
                .filter(u -> u.name.equals(name) && u.password.equals(password))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid credentials!"));
    }

    public List<User> listUsers(){
        return repo.getAll();
    }

    public void removeUser(int userId, User loggedInUser){
        if (loggedInUser == null || !loggedInUser.isAdmin){
            throw new RuntimeException("Only admins can register users!");
        }

        boolean exists = repo.findById(userId).isPresent();
        if (!exists) throw new RuntimeException("User not found!");

        repo.remove(userId);
    }
}