package model;

import java.io.Serializable;

public class User implements Serializable {
    public final static byte ADMIN_ROLE = 1;
    public final static byte USER_ROLE = 0;
    private int id;
    private String username;
    private String password;
    private byte role;

    public User(int id) {
        this.id = id;
        this.username = "";
        this.password = "";
    }

    public User(String username, String password) {
        this.id = -1;
        this.username = username;
        this.password = password;
    }

    public User(int id, String username) {
        this.id = id;
        this.username = username;
        this.password = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(byte role) {
        this.role = role;
    }

    public byte getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "User: " +
                "\n id: " + id +
                "\nname: " + username +
                "\npassword" + password;
    }
}
