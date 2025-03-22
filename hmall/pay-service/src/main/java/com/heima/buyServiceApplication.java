package com.heima;

import lombok.Setter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.jmx.export.annotation.ManagedAttribute;

@SpringBootApplication
@MapperScan
@EnableDiscoveryClient
@EnableFeignClients
public class buyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(buyServiceApplication.class, args);
    }
}
