package ticket.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@PropertySource({ "classpath:/META-INF/ticket.properties" })
public class RedisConfig {
	
	@Autowired
	private Environment env;
	
	@Bean
    public JedisPool redisPoolFactory()  throws Exception{
		int maxIdle = Integer.parseInt(env.getRequiredProperty("redis.jedis.pool.max-idle"));
		long maxWaitMillis = Long.parseLong(env.getRequiredProperty("redis.jedis.pool.max-wait"));
		boolean blockWhenExhausted = Boolean.parseBoolean(env.getRequiredProperty("redis.block-when-exhausted"));
		String host = env.getRequiredProperty("redis.host");
		int port = Integer.parseInt(env.getRequiredProperty("redis.port"));
		int timeout = Integer.parseInt(env.getRequiredProperty("redis.timeout"));
		String password = env.getRequiredProperty("redis.password");
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        // 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
        jedisPoolConfig.setBlockWhenExhausted(blockWhenExhausted);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
        return jedisPool;
    }

}
