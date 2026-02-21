package br.com.empowerlearn.empowerlearn_api.config; // Novo pacote 'config'

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapeia a URL /uploads/** para o diretório físico
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:/temp/empowerlearn/uploads/") // Sintaxe otimizada para Windows
                .setCachePeriod(0); // Garante que a foto nova apareça sem cache
    }
}