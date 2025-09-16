package msa.snowflake.service;

import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class AppConfigService {
    private final WebClient webClient;
    private final LoggingSystem loggingSystem;

    public AppConfigService(LoggingSystem loggingSystem) {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:2772")
                .build();
        this.loggingSystem = loggingSystem;
    }

    @Scheduled(fixedDelay = 30000)
    public void refreshConfig() {
        try {
            Map<String, Object> config = webClient.get()
                    .uri("/applications/snowflake/environments/dev/configurations/logging")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (config != null && config.containsKey("logging")) {
                Map<String, Object> logging = (Map<String, Object>) config.get("logging");
                if (logging.containsKey("level")) {
                    Map<String, String> levels = (Map<String, String>) logging.get("level");
                    levels.forEach((logger, levelStr) -> {
                        try {
                            LogLevel level = LogLevel.valueOf(levelStr.toUpperCase());
                            loggingSystem.setLogLevel(logger, level);
                            System.out.println("Updated log level: " + logger + " -> " + level);
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid log level for " + logger + ": " + levelStr);
                        }
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to refresh config: " + e.getMessage());
        }
    }
}
