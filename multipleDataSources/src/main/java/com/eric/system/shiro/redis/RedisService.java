package com.eric.system.shiro.redis;

import org.apache.shiro.dao.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected RedisTemplate redisTemplate;

    public long delByPattern(String pattern) {
        if (!StringUtils.isEmpty(pattern)) {
            return del(this.keys(pattern));
        }
        return 0;
    }

    /**
     * @param keys
     */
    public long del(final Set<String> keys) {
        return (long) redisTemplate.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0;
                for (String key : keys) {
                    result = connection.del(key.getBytes());
                }
                return result;
            }
        });
    }

    /**
     * @param keys
     */
    public long del(final String... keys) {
        return (long) redisTemplate.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0;
                for (int i = 0; i < keys.length; i++) {
                    result = connection.del(keys[i].getBytes());
                }
                return result;
            }
        });
    }

    /**
     * @param keys
     */
    public long del(final byte[]... keys) {
        return (long) redisTemplate.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0;
                for (int i = 0; i < keys.length; i++) {
                    result = connection.del(keys[i]);
                }
                return result;
            }
        });
    }

    /**
     * @param key
     * @param value
     * @param liveTime
     */
    public void set(final byte[] key, final byte[] value, final long liveTime) {
        redisTemplate.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                connection.set(key, value);
                if (liveTime > 0) {
                    connection.expire(key, liveTime);
                }
                return 1L;
            }
        });
    }

    /**
     * @param key
     * @param value
     * @param liveTime
     */
    public void set(String key, String value, long liveTime) {
        this.set(key.getBytes(), value.getBytes(), liveTime);
    }

    /**
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        this.set(key, value, 0L);
    }

    /**
     * @param key
     * @param object
     */
    public void set(String key, Object object) {
        this.set(key.getBytes(), SerializeUtils.serialize(object), 0L);
    }

    /**
     * @param key
     * @param value
     */
    public void set(byte[] key, byte[] value) {
        this.set(key, value, 0L);
    }

    /**
     * @param key
     * @return
     */
    public Object getObject(final byte[] key) {
        return redisTemplate.execute(new RedisCallback() {
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return SerializeUtils.deserialize(connection.get(key));
            }
        });
    }

    /**
     * @param key
     * @return
     */
    public Object getObject(final String key) {
        return getObject(key.getBytes());
    }

    /**
     * get value from redis
     *
     * @param key
     * @return
     */
    public byte[] get(byte[] key) {
        return (byte[]) redisTemplate.execute(new RedisCallback<byte[]>() {
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(key);
            }
        });
    }

    /**
     * @param key
     * @return
     */
    public String get(final String key) {
        return (String) redisTemplate.execute(new RedisCallback() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                try {
                    return new String(connection.get(key.getBytes()), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return "";
            }
        });
    }

    /**
     * @param pattern
     * @return
     */
    public Set keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * @param key
     * @return
     */
    public boolean exists(final byte[] key) {
        return (boolean) redisTemplate.execute(new RedisCallback() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.exists(key);
            }
        });
    }

    /**
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return exists(key.getBytes());
    }

    /**
     * @return
     */
    public String flushDB() {
        return (String) redisTemplate.execute(new RedisCallback() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushDb();
                return "ok";
            }
        });
    }

    /**
     * @return
     */
    public long dbSize() {
        return (long) redisTemplate.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.dbSize();
            }
        });
    }

    /**
     * @return
     */
    public String ping() {
        return (String) redisTemplate.execute(new RedisCallback() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.ping();
            }
        });
    }

    /**
     * 缓存list
     *
     * @param k
     * @param v
     * @param time
     * @return
     */
    public boolean setList(String k, List v, long time) {
        String key = k;
        try {
            ListOperations<String, String> listOps = redisTemplate.opsForList();
            long l = listOps.rightPushAll(key, v);
            if (time > 0) redisTemplate.expire(key, time, TimeUnit.SECONDS);
            return true;
        } catch (Throwable t) {
            logger.error("缓存[" + key + "]失败, value[" + v + "]", t);
        }
        return false;
    }

    /**
     * 缓存list
     *
     * @param k
     * @param v
     * @return
     */
    public boolean setList(String k, List v) {
        return setList(k, v, -1);
    }

    /**
     * 获取list缓存
     *
     * @param k
     * @param start
     * @param end
     * @return
     */
    public List getList(String k, long start, long end) {
        try {
            ListOperations<String, String> listOps = redisTemplate.opsForList();
            return listOps.range(k, start, end);
        } catch (Throwable t) {
            logger.error("获取list缓存失败key[" + k + ", error[" + t + "]");
        }
        return null;
    }


    /**
     * 获取总条数, 可用于分页
     *
     * @param k
     * @return
     */
    public long getListSize(String k) {
        try {
            ListOperations<String, String> listOps = redisTemplate.opsForList();
            return listOps.size(k);
        } catch (Throwable t) {
            logger.error("获取list长度失败key[" + k + "], error[" + t + "]");
        }
        return 0;
    }

    public List getList(String k) {
        try {
            ListOperations<String, String> listOps = redisTemplate.opsForList();
            return listOps.range(k, 0, getListSize(k));
        } catch (Throwable t) {
            logger.error("获取list缓存失败key[" + k + ", error[" + t + "]");
        }
        return null;
    }
}
