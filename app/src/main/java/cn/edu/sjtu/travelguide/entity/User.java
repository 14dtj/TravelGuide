package cn.edu.sjtu.travelguide.entity;

import java.util.List;

/**
 * 用户
 */
public class User {
    private long id;
    private String username;
    private String password;
    private String email;
    private int role;
    private List<SearchRecord> hassearched;

    public User(long id, String username, String password, String email, int role, List<SearchRecord> hassearched) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.hassearched = hassearched;
    }

    public User(String username, String email, String password, int role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public List<SearchRecord> getHassearched() {
        return hassearched;
    }

    public void setHassearched(List<SearchRecord> hassearched) {
        this.hassearched = hassearched;
    }
}
