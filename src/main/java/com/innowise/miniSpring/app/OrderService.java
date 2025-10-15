package com.innowise.miniSpring.app;

import com.innowise.miniSpring.minispring.Autowired;
import com.innowise.miniSpring.minispring.Component;

@Component
public class OrderService {
    private UserService userService;

    public OrderService(UserService userService) {
        this.userService = userService;
        System.out.println("OrderService Constructor called");
    }

    public void getOrderByUser(){
        userService.findUser();
        System.out.println("OrderService - getOrderByUser Called");
    }
}
