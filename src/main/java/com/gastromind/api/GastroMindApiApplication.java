package com.gastromind.api;

import com.gastromind.api.infrastructure.adapters.out.ai.GeminiProperties;
import com.gastromind.api.infrastructure.adapters.out.ai.TicketImageProperties;
import com.gastromind.api.infrastructure.adapters.out.cache.RecipeSuggestionCacheProperties;
import com.gastromind.api.infrastructure.config.UsualPurchaseProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ GeminiProperties.class, RecipeSuggestionCacheProperties.class, TicketImageProperties.class,
        UsualPurchaseProperties.class })
/**
 * Representa gastro mind api application dentro del dominio de la aplicacion.
 */
public class GastroMindApiApplication {
	/**
	 * Realiza main.
	 * @param args valor a utilizar.
	 */

	public static void main(String[] args) {
		SpringApplication.run(GastroMindApiApplication.class, args);
	}

}




