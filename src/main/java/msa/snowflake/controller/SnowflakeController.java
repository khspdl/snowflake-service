package msa.snowflake.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msa.snowflake.controller.request.SnowflakeRequest;
import msa.snowflake.controller.response.SnowflakeResponse;
import msa.snowflake.service.SnowflakeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SnowflakeController {

    private final SnowflakeService snowflakeService;

    @GetMapping("/snowflake")
    public SnowflakeResponse getId(@RequestBody SnowflakeRequest request) {
        String result = validateRequest(request);

        if (result != null) {
            return SnowflakeResponse.builder()
                    .error(result)
                    .build();
        }

        return SnowflakeResponse.builder()
                .host(request.getHost())
                .service(request.getService())
                .snowflakeId(snowflakeService.getId())
                .build();
    }

    private String validateRequest(SnowflakeRequest request) {
        if (request == null) {
            log.error("Invalid request: request body is null");
            return "Invalid request: request body is null";
        }
        if (request.getService() == null) {
            log.error("Invalid request: 'service' field is null");
            return "Invalid request: missing 'service' field";
        }
        if (request.getHost() == null) {
            log.error("Invalid request: 'host' field is null");
            return "Invalid request: missing 'host' field";
        }

        return null;
    }
}
