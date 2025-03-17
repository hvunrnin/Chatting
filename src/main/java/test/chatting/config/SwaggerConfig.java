package test.chatting.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/"))  // 기본 서버 설정
                .info(apiInfo()); // API 기본 정보 추가
    }

    private Info apiInfo() {
        return new Info()
                .title("Chatting API") // API 제목
                .description("채팅 서비스 API") // API 설명
                .version("1.0.0"); // API 버전
    }
}
