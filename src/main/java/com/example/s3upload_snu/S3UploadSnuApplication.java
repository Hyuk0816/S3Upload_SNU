package com.example.s3upload_snu;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@OpenAPIDefinition(servers = {@Server(url = "https://snu-commute-v3latest-yi6gkqhzma-du.a.run.app/")})
@SpringBootApplication
public class S3UploadSnuApplication {

    public static void main(String[] args) {
        SpringApplication.run(S3UploadSnuApplication.class, args);
    }

}
