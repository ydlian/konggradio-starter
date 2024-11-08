package org.konggradio.unicron.iot.bootstrap.conf;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CommonApiConfig {
    @Value("${gift.namespace}")
    private String namespace;

    @Value("${gift.uploadHost}")
    private String uploadHost;

    @Value("${gift.selectHost}")
    private String selectHost;


}
