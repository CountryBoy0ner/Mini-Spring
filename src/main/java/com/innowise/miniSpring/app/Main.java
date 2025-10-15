package com.innowise.miniSpring.app;


import com.innowise.miniSpring.minispring.MiniApplicationContext;

public class Main {
    public static void main(String[] args) {
        MiniApplicationContext context = new MiniApplicationContext
                (
                        UserRepository.class,
                        UserService.class,
                        OrderService.class
                );

        OrderService service = context.getBean(OrderService.class);
        service.getOrderByUser();
    }

}