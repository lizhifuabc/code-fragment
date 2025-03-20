package io.github.lizhifuabc.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI配置
 * 用于生成API文档
 *
 * @author lizhifu
 * @since 2025/3/19
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("安全API接口文档")
                        .description("实现了签名验证、加密、IP白名单、限流等安全功能的API接口")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("lizhifu")
                                .url("https://github.com/lizhifuabc")
                                .email("lizhifu@example.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}