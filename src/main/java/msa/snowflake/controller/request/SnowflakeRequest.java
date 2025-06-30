package msa.snowflake.controller.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SnowflakeRequest {

    private String host;
    private String service;
}
