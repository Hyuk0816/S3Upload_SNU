package com.example.s3upload_snu.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    @Bean
    public Info apiInfo(){
        return new Info()
                .title("서울대학교 캠퍼스타운 출근 로그 분석")
                .description("세콤 매니저 RAW 데이터 업로드 API")
                .version("2.0");
    }
    @Bean
    public GroupedOpenApi getAllApi() {
        return GroupedOpenApi
                .builder()
                .group("ALL")
                .packagesToScan("com.example.s3upload_snu")
                .build();
    }
    private GroupedOpenApi createGroupedOpenApi(String groupName, String path) {
        return GroupedOpenApi.builder()
                .group(groupName)
                .pathsToMatch(path)
                .build();
    }

    @Bean
    public GroupedOpenApi getUserApi(){
        return createGroupedOpenApi("유저 API", "/api/**");
    }

    @Bean
    public GroupedOpenApi getFileApi(){
        return createGroupedOpenApi("파일 업로드 및 다운로드 API", "/api/file/**");
    }

}
