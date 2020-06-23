package com.jianke.mall;


import com.jianke.mall.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 促销管理接口
 *
 * @author 郑喜荣
 * @since 16/9/27
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@Configuration
@EnableAspectJAutoProxy
public class RedisServiceApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(RedisServiceApplication.class, args);
    }

    @Autowired
    private RedisService redisService;

    @Override
    public void run(String... args) throws Exception {

    }
}

