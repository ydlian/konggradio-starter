package org.konggradio.unicron.iot.auto;

import org.konggradio.unicron.iot.bootstrap.BootstrapServer;
import org.konggradio.unicron.iot.bootstrap.NettyBootstrapServer;

import lombok.extern.slf4j.Slf4j;
import org.konggradio.unicron.iot.properties.InitBean;

/**
 * 初始化服务
 *
 * @author lianyadong
 * @create 2023-11-29 20:12
 **/
@Slf4j
public class InitServer {

    BootstrapServer bootstrapServer;
    private InitBean serverBean;

    public InitServer(InitBean serverBean) {
        this.serverBean = serverBean;
    }

    public void open() {
        if (serverBean != null) {

            bootstrapServer = new NettyBootstrapServer();
            bootstrapServer.setServerBean(serverBean);
            bootstrapServer.start();
        }
    }


    public InitBean getServerBean() {
        return serverBean;
    }


    public void close() {
        if (bootstrapServer != null) {
            bootstrapServer.shutdown();
        }
    }

}
