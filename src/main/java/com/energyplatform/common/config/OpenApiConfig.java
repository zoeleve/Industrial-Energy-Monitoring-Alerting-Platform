package com.energyplatform.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  // Swagger UI's default tagsSorter preserves this declaration order, not alphabetical.
  @Bean
  public OpenAPI openApi() {
    return new OpenAPI()
        .tags(
            List.of(
                new Tag().name("Auth"),
                new Tag().name("Alerts"),
                new Tag().name("Devices"),
                new Tag().name("Measurements")));
  }
}
