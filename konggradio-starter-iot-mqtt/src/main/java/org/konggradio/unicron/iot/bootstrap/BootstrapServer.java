package org.konggradio.unicron.iot.bootstrap;


import org.konggradio.unicron.iot.properties.InitBean;

/**
 * 启动类接口
 *
 * @author lianyadong
 * @create 2023-11-18 14:05
 **/
public interface BootstrapServer {

    void shutdown();

    void setServerBean(InitBean serverBean);

    void start();


}
