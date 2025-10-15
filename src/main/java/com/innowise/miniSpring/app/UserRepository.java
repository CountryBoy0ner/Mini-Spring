package com.innowise.miniSpring.app;

import com.innowise.miniSpring.minispring.Component;

@Component
public class UserRepository {
    public UserRepository() {
        System.out.println("UserRepository Constructor Called");
    }
    public void findUser() {
        System.out.println("UserRepository- findUser()  Called");
    }
}