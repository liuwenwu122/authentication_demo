package com.liuww.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


/**
 * @Date: 2019/9/1
 * @Author: WenWu.Liu
 * @Desc:
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
@ComponentScan("com.liuww.auth")
@MapperScan("com.liuww.auth.dao")
public class UcenterAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(UcenterAuthApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }


}
