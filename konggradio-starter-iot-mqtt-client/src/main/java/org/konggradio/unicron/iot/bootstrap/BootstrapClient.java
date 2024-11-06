package org.konggradio.unicron.iot.bootstrap;


import io.netty.channel.Channel;

/**
 * 启动类接口
 *
 * @author StormRiver
 * @create 2017-11-18 14:05
 **/

public interface BootstrapClient {


    void shutdown();

    void initEventPool();

    Channel start();


}
