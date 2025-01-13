package co.kr.mayfarm.seoulinstitutemanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class ImageConfig  implements WebMvcConfigurer {

    @Value("${dir.attachment}")
    private String ATTACHMENT_DIR;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/manager/images/**")
                .addResourceLocations("file:///" + ATTACHMENT_DIR+ "/");
    }

}
