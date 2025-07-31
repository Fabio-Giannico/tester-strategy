package com.godel.utils.database;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;


@ApplicationScoped
public class RedisService {

    @ConfigProperty(name = "redis.user")
    private String REDIS_USER;
    @ConfigProperty(name = "redis.host")
    private String REDIS_HOST;
    @ConfigProperty(name = "redis.port")
    private int REDIS_PORT;
    @ConfigProperty(name = "redis.password")
    private String REDIS_PSW;



    public void setValue(String key, String value) {
        setValue(key, value, null);
    }
    public void setValue(String key, String value, SetParams params) {
        UnifiedJedis jedis = null;

        try {
            JedisClientConfig config = DefaultJedisClientConfig.builder()
                .user(REDIS_USER)
                .password(REDIS_PSW)
                .build();

            jedis = new UnifiedJedis(
                new HostAndPort(REDIS_HOST, REDIS_PORT),
                config
            );

            String res;

            if (params != null) {
                res = jedis.set(key, value, params);
            } else {
                res = jedis.set(key, value);
            }

            if (!"OK".equals(res)) {
                throw new JedisException("Errore durante il SET: " + res);
            }
        } catch (JedisException e) {
            System.err.println("Errore Redis: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String getValue(String key) {
        UnifiedJedis jedis = null;

        try {
            JedisClientConfig config = DefaultJedisClientConfig.builder()
                .user(REDIS_USER)
                .password(REDIS_PSW)
                .build();

            jedis = new UnifiedJedis(
                new HostAndPort(REDIS_HOST, REDIS_PORT),
                config
            );

            String res = jedis.get(key);

            if (res == null) {
                throw new JedisException("La chiave " + key + " non esiste");
            }

            return res;
            
        } catch (JedisException e) {
            System.err.println("Errore Redis: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return null;
    }
}