package com.ffforever.cartservice;

import com.ffforever.hmapi.client.cartClient;
import feign.Client;
import org.apache.catalina.startup.WebAnnotationSet;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@MapperScan("com.ffforever.cartservice.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ffforever.hmapi.client")
public class CartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartServiceApplication.class, args);
    }

    //WebClient
//    @Bean(name = "itemsWebClient")
//    public WebClient webClient(WebClient.Builder builder) {
//        return builder
//                .baseUrl("http://localhost:8081/")
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .build();
//    }
}
