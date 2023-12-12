package com.denver7074.bot.configuration;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@EnableCaching
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RedisConfig extends CachingConfigurerSupport {

    @Value("${spring.data.redis.host}")
    String redisHost;
    @Value("${spring.data.redis.port}")
    Integer redisPort;
//    @Bean
//    public JedisConnectionFactory jedisConnectionFactory() {
//        return new JedisConnectionFactory(new RedisStandaloneConfiguration(redisHost, redisPort));
//    }
//    @Bean
//    public RedisCacheManager cacheManager() {
//        return RedisCacheManager.builder(jedisConnectionFactory()).build();
//    }
}
