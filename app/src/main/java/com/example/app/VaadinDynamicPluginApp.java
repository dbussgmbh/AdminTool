package com.example.app;

import com.example.core.plugin.PluginJarLoader;
import com.vaadin.flow.spring.annotation.EnableVaadin;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableVaadin("com.example")
@SpringBootApplication(scanBasePackages = "com.example")
public class VaadinDynamicPluginApp {

    public static void main(String[] args) {
        SpringApplication.run(VaadinDynamicPluginApp.class, args);
    }

    @Bean
    ApplicationRunner loadPluginsAtStartup(PluginJarLoader loader) {
        return args -> {
            try {
                loader.reload();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
