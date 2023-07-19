package com.chrisxyq.idempotence;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * 除此之外，按照接口隔离原则，我们将生成幂等号的代码抽离出来，
 * 放到 IdempotenceIdGenerator 类中。
 * 这样，调用方只需要依赖这个类的代码就可以了。幂等号生成算法的修改，
 * 跟幂等号存储逻辑的修改，两者完全独立，一个修改不会影响另外一个。
 */
public class RedisClusterIdempotenceStorage implements IdempotenceStorage {
    private JedisCluster jedisCluster;

    /**
     * Constructor
     *
     * @param redisClusterAddress the format is 128.91.12.1:3455;128.91.12.2:3452
     * @param config              should not be null
     */
    public RedisClusterIdempotenceStorage(String redisClusterAddress, GenericObjectPoolConfig config) {
        Set<HostAndPort> redisNodes = parseHostAndPorts(redisClusterAddress);
        this.jedisCluster = new JedisCluster(redisNodes, config);
    }

    public RedisClusterIdempotenceStorage(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    /**
     * Save {@idempotenceId} into storage if it does not exist.
     *
     * @param idempotenceId the idempotence ID
     * @return true if the {@idempotenceId} is saved, otherwise return false
     */
    @Override
    public boolean saveIfAbsent(String idempotenceId) {
        Long success = jedisCluster.setnx(idempotenceId, "1");
        return success == 1;
    }

    @Override
    public void delete(String idempotenceId) {
        jedisCluster.del(idempotenceId);
    }

    /**
     * 我们把原本放在构造函数中的逻辑抽离出来，放到了 parseHostAndPorts() 函数中。
     * 这个函数本应该是 Private 访问权限的，
     * 但为了方便编写单元测试，我们把它设置为成了 Protected 访问权限，
     * 并且通过注解 @VisibleForTesting 做了标明。
     *
     * @param redisClusterAddress
     * @return
     */
    @VisibleForTesting
    protected Set<HostAndPort> parseHostAndPorts(String redisClusterAddress) {
        String[] addressArray = redisClusterAddress.split(";");
        Set<HostAndPort> redisNodes = new HashSet<>();
        for (String address : addressArray) {
            String[] hostAndPort = address.split(":");
            redisNodes.add(new HostAndPort(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
        }
        return redisNodes;
    }
}
