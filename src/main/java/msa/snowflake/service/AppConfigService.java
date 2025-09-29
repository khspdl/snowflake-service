package msa.snowflake.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
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
                            log.info("Updated log level: {} -> {}", logger, level);
                        } catch (IllegalArgumentException e) {
                            log.error("Invalid log level for {}: {}", logger, levelStr);
                        }
                    });
                }
            }
        } catch (Exception e) {
            log.error("Failed to refresh config: {}", e.getMessage());
        }
    }
}
