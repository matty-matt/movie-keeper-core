package com.kociszewski.moviekeepercore.proxy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
public class ProxyTest {

    @Value("${api.key}")
    private String apiKey;

    @Test
    public void shouldHaveApiKeyAsAnEnvironmentVariable() {
        assertThat(apiKey).isNotBlank();
        assertThat(apiKey).doesNotContain(Arrays.asList("$","{","}"));
    }
}
