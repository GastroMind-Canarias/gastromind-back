package com.gastromind.api;

import com.gastromind.api.infrastructure.adapters.out.ai.GeminiProperties;
import com.gastromind.api.infrastructure.adapters.out.ai.TicketImageProperties;
import com.gastromind.api.infrastructure.adapters.out.cache.RecipeSuggestionCacheProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ GeminiProperties.class, RecipeSuggestionCacheProperties.class, TicketImageProperties.class })
public class GastroMindApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GastroMindApiApplication.class, args);
	}

}
