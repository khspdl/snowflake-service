package msa.snowflake.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msa.snowflake.controller.response.SnowflakeResponse;
import msa.snowflake.service.SnowflakeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SnowflakeController {

    private final SnowflakeService snowflakeService;

    @GetMapping("/snowflake")
    public SnowflakeResponse getId(@RequestParam("host") String host) {
        String result = validateRequest(host);

        if (result != null) {
            return SnowflakeResponse.builder()
                    .error(result)
                    .build();
        }

        return SnowflakeResponse.builder()
                .host(host)
                .snowflakeId(snowflakeService.getId())
                .build();
    }

    private String validateRequest(String host) {
        if (host == null) {
            log.error("Invalid request: host is null");
            return "Invalid request: host is null";
        }
        return null;
    }
}
