package com.heima;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.heima.user.mapper")
public class HeimaLeadnewsUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeimaLeadnewsUserApplication.class, args);
    }

}
