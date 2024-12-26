package com.ehei;

import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private List<String> users;

    public UserManager() {
        this.users = new ArrayList<>();
    }

    public boolean addUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return users.add(username);
    }

    public boolean removeUser(String username) {
        return users.remove(username);
    }

    public List<String> getUsers() {
        return new ArrayList<>(users);
    }
}
