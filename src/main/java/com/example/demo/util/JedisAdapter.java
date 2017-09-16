package com.example.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

/**
 * Created by Crys at 2017/9/6
 */
@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class.toString());

    private JedisPool pool= null;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool();
    }

    //添加value
    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        }catch( Exception e) {
            logger.error("获取Jedis失败" + e.getMessage());
            return 0;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    //将某个值移除
    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        }catch( Exception e) {
            logger.error("获取Jedis失败" + e.getMessage());
            return 0;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    //某个值是否在键值中
    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        }catch( Exception e) {
            logger.error("获取Jedis失败" + e.getMessage());
            return false;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 返回key的value数
     * @param key
     * @return
     */
    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch( Exception e) {
            logger.error("获取Jedis失败" + e.getMessage());
            return 0;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    /*public static void print( int index, Object obj ) {
        System.out.println(String.format("%d,%s", index, obj.toString()));
    }

    public static void main( String[] argv ) {
        Jedis jedis= new Jedis();
        jedis.flushAll();

        //redis命令练习，set,rename,setex
        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "this");
        print(2, jedis.get("this"));
        jedis.setex("temprary", 15, "222");
        print(3, jedis.get("temprary"));

        //incr,incrBy
        jedis.set("pv", "10");
        jedis.incr("pv");
        print(4, jedis.get("pv"));
        jedis.incrBy("pv", 6);
        print(4, jedis.get("pv"));

        //list的操作，list底层是双向列表?
        String listName = "ListA";
        for (int i = 0; i < 5; i++) {
            jedis.lpush(listName, "a" + String.valueOf(i + 1));
        }
        print(5, jedis.lrange(listName, 0, 5));
        print(5, jedis.llen(listName));
        jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a3", "a3.3");
        jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a3", "a4.9");
        print(5, jedis.lrange(listName, 0, 7));

        //hash的操作
        String userKey= "user";
        jedis.hset(userKey, "name", "ella");
        jedis.hset(userKey, "phone", "13100008888");
        jedis.hset(userKey, "age", "23");
        print(6, jedis.hget(userKey, "phone"));
        print(7, jedis.hgetAll(userKey));
        print(8, jedis.hkeys(userKey));
        print(9, jedis.hvals(userKey));
        jedis.hdel(userKey, "age");
        print(10, jedis.hkeys(userKey));
        print(11, jedis.hexists(userKey, "email"));
        jedis.hsetnx(userKey, "name","anonymous");
        jedis.hsetnx(userKey, "age","24");
        print(12, jedis.hgetAll(userKey));

        //set操作，添加、打印、交、并、差、删除、获取元素个数
        String setKeys1= "set1";
        String setKeys2= "set2";
        for(int i=0; i< 8; i++ ) {
            jedis.sadd(setKeys1, String.valueOf(i));
            jedis.sadd(setKeys2, String.valueOf(i*2));
        }
        print(13, jedis.smembers(setKeys1));
        print(14, jedis.smembers(setKeys2));
        print(15, jedis.sinter(setKeys1, setKeys2));
        print(16, jedis.sunion(setKeys1, setKeys2));
        print(17, jedis.sdiff(setKeys1, setKeys2));
        print(18, jedis.sismember(setKeys1, "7"));
        jedis.srem(setKeys1, "7");
        print(19, jedis.smembers(setKeys1));
        print(20, jedis.scard(setKeys1));

        //sortedSet操作
        String rankKey = "sortedSet1";
        jedis.zadd(rankKey, 70, "Mike");
        jedis.zadd(rankKey, 90, "Jim");
        jedis.zadd(rankKey, 86, "Lucy");
        jedis.zadd(rankKey, 55, "Alice");
        jedis.zadd(rankKey, 43, "Bob");
        print(21, jedis.zcard(rankKey));
        print(22, jedis.zcount(rankKey, 50,100));
        print(23, jedis.zscore(rankKey, "Mike"));
        jedis.zincrby(rankKey, 5, "Bob");
        print(24, jedis.zscore(rankKey, "Bob"));
        jedis.zincrby(rankKey, 10, "Lily");
        print(25, jedis.zrange(rankKey, 0,6));
        print(26, jedis.zrevrange(rankKey, 0, 6));

        for(Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, "0", "100") ) {
            print( 27, tuple.getElement() + String.valueOf(tuple.getScore()));
        }

        print(27, jedis.zrank(rankKey, "Alice"));
        print(27, jedis.zrevrank(rankKey, "Alice"));

        //连接池
        JedisPool pool = new JedisPool();
        for(int i=0; i< 100 ; i++) {
            Jedis j= pool.getResource();
            j.get("a");
            System.out.println("POOL" + i);
            j.close();
        }
    }*/
}
