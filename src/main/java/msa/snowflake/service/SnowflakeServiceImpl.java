package msa.snowflake.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SnowflakeServiceImpl implements SnowflakeService {
    private final SnowflakeProcessor snowflakeProcessor;

    public Long getId() {
        return snowflakeProcessor.nextId();
    }
}
