package library.service;

import library.model.User;
import library.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

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

        if (repo.getAll().isEmpty()){
            isAdmin = true;
        }

        User newUser = new User(0, name, hashPassword(password), isAdmin);
        repo.add(newUser);

        System.out.println("User registered successfully!.");
    }

    public User login(String name, String password) {
        User user = repo.findByName(name)
                .orElseThrow(() -> new RuntimeException("Invalid credentials!"));

        if (!verifyPassword(password, user.password)) {
            throw new RuntimeException("Invalid credentials!");
        }
        return user;
    }

    public List<User> listUsers(){
        return repo.getAll();
    }

    public void removeUser(int userId, User loggedInUser){
        if (loggedInUser == null || !loggedInUser.isAdmin){
            throw new RuntimeException("Only admins can remove users!");
        }

        boolean exists = repo.findById(userId).isPresent();
        if (!exists) throw new RuntimeException("User not found!");

        repo.remove(userId);
        System.out.println("User removed successfully!.");
    }

    public static String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String password, String hashPassword){
        return BCrypt.checkpw(password,hashPassword);
    }
}