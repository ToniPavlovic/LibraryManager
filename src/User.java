public class User {
    int id;
    String name;
    String password;

    public User (int id, String name, String password){
        this.id = id;
        this.name = name;
        this.password = password;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + name;
    }
}
