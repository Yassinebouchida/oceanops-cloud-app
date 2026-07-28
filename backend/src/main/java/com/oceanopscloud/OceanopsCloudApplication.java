package com.oceanopscloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.context.annotation.ComponentScan(basePackages = "com.oceanopscloud")
public class OceanopsCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(OceanopsCloudApplication.class, args);
    }

}
