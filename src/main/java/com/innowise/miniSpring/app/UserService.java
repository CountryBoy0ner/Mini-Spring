package com.innowise.miniSpring.app;


import com.innowise.miniSpring.minispring.*;


@Component
public class UserService implements InitializingBean {
    @Autowired
    private UserRepository repo;


    public UserService() {
        System.out.println("UserService Constructor called");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("UserService Ready = " + repo);
    }

    public void findUser(){
        repo.findUser();
        System.out.println("UserService - findUser() Called");
    }


}