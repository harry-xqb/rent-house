package com.harry.renthouse.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Harry Xu
 * @date 2020/5/13 10:51
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api(){
       /* Docket docket = new Docket(DocumentationType.SWAGGER_2).
                groupName("接口API").
                genericModelSubstitutes(DeferredResult.class).
                useDefaultResponseMessages(false).
                forCodeGeneration(true).
                select().
                apis(RequestHandlerSelectors.basePackage("com.harry.renthouse.web.controller")).
        apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)).
                        build().
                        apiInfo(apiInfo());*/
        return new Docket(DocumentationType.SWAGGER_2)
                .pathMapping("/")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.harry.renthouse.web.controller"))
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .build().apiInfo(new ApiInfoBuilder()
                .title("租房网API接口文档")
                .description("租房网api接口文档")
                .version("0.0.1")
                .contact(new Contact("harry",
                        "https://github.com/harry-xqb/rent-house",
                        "923243595@qq.com"))
                .license("github")
                .licenseUrl("https://github.com/harry-xqb/rent-house")
                .build());
    }
}
