package com.chat.crazy.base.config;

import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

@Configuration
//@EnableConfigurationProperties(CustomizedRedisProperties.class)
public class RedisConfig {
    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    @Bean("chatRedisTemplate")
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
//        LettuceConnectionFactory lettuceConnectionFactory = createLettuceConnectionFactory(redisProperties.getChatCrazy());
//        lettuceConnectionFactory.afterPropertiesSet();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
//    @Bean("chatRedisTemplate")
//    public RedisTemplate<String, String> redisTemplate(CustomizedRedisProperties redisProperties) {
//        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
//        LettuceConnectionFactory lettuceConnectionFactory = createLettuceConnectionFactory(redisProperties.getChatCrazy());
//        lettuceConnectionFactory.afterPropertiesSet();
//        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
//        redisTemplate.afterPropertiesSet();
//        return redisTemplate;
//    }

    private LettuceConnectionFactory createLettuceConnectionFactory(RedisProperties properties) {
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        //连接池信息配置
        RedisProperties.Pool pool = properties.getLettuce().getPool();
        GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(pool.getMaxActive());
        config.setMaxIdle(pool.getMaxIdle());
        config.setMinIdle(pool.getMinIdle());
        if (pool.getMaxWait() != null) {
            config.setMaxWaitMillis(pool.getMaxWait().toMillis());
        }
        builder.poolConfig(config);

        // 配置拓扑信息
        ClusterClientOptions.Builder clientOptionsBuilder = ClusterClientOptions.builder();
        RedisProperties.Lettuce.Cluster.Refresh refreshProperties = properties.getLettuce().getCluster().getRefresh();
        ClusterTopologyRefreshOptions.Builder refreshBuilder = ClusterTopologyRefreshOptions.builder();
        if (refreshProperties.getPeriod() != null) {
            refreshBuilder.enablePeriodicRefresh(refreshProperties.getPeriod());
        }
        if (refreshProperties.isAdaptive()) {
            refreshBuilder.enableAllAdaptiveRefreshTriggers();
        }
        clientOptionsBuilder.topologyRefreshOptions(refreshBuilder.build());
        clientOptionsBuilder.timeoutOptions(TimeoutOptions.enabled());
        builder.clientOptions(clientOptionsBuilder.build());

        // 客户端信息配置
        if (properties.isSsl()) {
            builder.useSsl();
        }
        if (properties.getTimeout() != null) {
            builder.commandTimeout(properties.getTimeout());
        }
        if (properties.getLettuce() != null) {
            RedisProperties.Lettuce lettuce = properties.getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(properties.getLettuce().getShutdownTimeout());
            }
        }
        if (StringUtils.hasText(properties.getClientName())) {
            builder.clientName(properties.getClientName());
        }

        // 集群配置
        RedisProperties.Cluster clusterProperties = properties.getCluster();
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration(clusterProperties.getNodes());
        if (clusterProperties.getMaxRedirects() != null) {
            clusterConfig.setMaxRedirects(clusterProperties.getMaxRedirects());
        }

        if (properties.getPassword() != null) {
            clusterConfig.setPassword(RedisPassword.of(properties.getPassword()));
        }
        return new LettuceConnectionFactory(clusterConfig, builder.build());
    }


}