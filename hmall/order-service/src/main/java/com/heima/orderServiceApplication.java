package com.heima;

import com.sun.jdi.PathSearchingVirtualMachine;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan
@EnableDiscoveryClient
@EnableFeignClients()
@SpringBootApplication
public class orderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(orderServiceApplication.class, args);
    }
}
