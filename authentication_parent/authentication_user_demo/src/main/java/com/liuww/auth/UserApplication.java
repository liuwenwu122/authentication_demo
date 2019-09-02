package com.liuww.auth;

import com.liuww.auth.interceptor.FeignClientInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @Date: 2019/9/2
 * @Author: WenWu.Liu
 * @Desc:
 */
@EnableFeignClients //开始feignClient
@EnableDiscoveryClient
@SpringBootApplication
@EnableEurekaClient
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
    }

    @Bean
    @LoadBalanced//开始客户端负载均衡
    public RestTemplate restTemplate(){
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }

    @Bean
    public FeignClientInterceptor getFeignClientInterceptor(){
        return new FeignClientInterceptor();
    }
}
