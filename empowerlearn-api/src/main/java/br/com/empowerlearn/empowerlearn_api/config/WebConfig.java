package br.com.empowerlearn.empowerlearn_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapeia a URL /uploads/** para o diretório físico (caminho relativo, funciona em qualquer SO)
        String uploadDir = System.getProperty("user.dir") + java.io.File.separator + "uploads" + java.io.File.separator;
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir)
                .setCachePeriod(0); // Garante que a foto nova apareça sem cache
    }
}
