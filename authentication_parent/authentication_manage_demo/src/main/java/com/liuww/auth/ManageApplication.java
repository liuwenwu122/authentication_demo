package com.liuww.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @Date: 2019/9/2
 * @Author: WenWu.Liu
 * @Desc:
 */

@SpringBootApplication
@EnableEurekaClient
public class ManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManageApplication.class,args);
    }
}
