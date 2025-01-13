package co.kr.mayfarm.seoulinstitutemanager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication()
@MapperScan(basePackages = "co.kr.mayfarm.seoulinstitutemanager.repository")
public class SeoulInstituteManagerApplication {

	@Value("${service.url}")
	String SERVICE_URL;

	public static void main(String[] args) {
		SpringApplication.run(SeoulInstituteManagerApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins(SERVICE_URL).allowedMethods("*");
			}
		};
	}
}
