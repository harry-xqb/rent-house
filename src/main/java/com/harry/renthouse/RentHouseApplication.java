package com.harry.renthouse;

import com.harry.renthouse.entity.User;
import com.harry.renthouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@RestController
public class RentHouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentHouseApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello world ";
    }

}
