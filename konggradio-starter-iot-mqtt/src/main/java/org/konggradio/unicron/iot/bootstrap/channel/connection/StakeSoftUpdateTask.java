package org.konggradio.unicron.iot.bootstrap.channel.connection;

import lombok.extern.slf4j.Slf4j;
import org.konggradio.unicron.iot.constant.RedisConstant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StakeSoftUpdateTask {

    private final static String ONLINE_CACHE_KEY = RedisConstant.UNICRON_GLOBAL_CONNECT_LIST;
    private final static int softupdateRetryOutTimes = 3;
    private final String UPDATE_KEY = RedisConstant.UNICRON_STAKE_UPDATE_KEY;
    private final String Md5String = "8b25841cdb7b4a2d";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void updateStakeSoftStatus() {

    }
}
