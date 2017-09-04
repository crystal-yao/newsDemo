package com.example.demo.model;

import org.springframework.stereotype.Component;

/**
 * Created by Crys at 2017/8/20
 * 当前用户
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }
}
