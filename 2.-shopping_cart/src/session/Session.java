package session;

import model.User;

import java.util.Optional;

public class Session {
    private User user = null;
    private static Session instance = null;

    private Session() {
    }

    public static Session getInstance() {
        if(instance == null) {
            synchronized (Session.class) {
                if(instance == null)
                    instance = new Session();
            }
        }

        return instance;
    }

    public Optional<User> getUser() {
        if(user == null) return Optional.empty();

        return Optional.of(new User(user.getId(), user.getUsername()));
    }

    public void setUser(User u) {
        user = u;
    }

    public void clearSession() {
        setUser(null);
    }

    public void createSession(User u) {
        setUser(u);
    }
}
