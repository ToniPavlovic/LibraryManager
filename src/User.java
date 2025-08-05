public class User {
    int id;
    String name;
    String password;
    boolean isAdmin;

    public User (int id, String name, String password, boolean isAdmin){
        this.id = id;
        this.name = name;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + name;
    }
}
