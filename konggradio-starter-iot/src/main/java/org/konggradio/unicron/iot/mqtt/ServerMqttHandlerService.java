package org.konggradio.unicron.iot.mqtt;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 抽象出服务端事件
 *
 * @author lianyadong
 * @create 2023-01-03 16:11
 **/
public abstract class ServerMqttHandlerService implements MqttHandlerInterface {


    public abstract void doLogic(Channel channel, MqttMessage mqttMessage);

    public abstract boolean login(Channel channel, MqttConnectMessage mqttConnectMessage);

    public abstract void publish(Channel channel, MqttPublishMessage mqttPublishMessage);

    public abstract void subscribe(Channel channel, MqttSubscribeMessage mqttSubscribeMessage);


    public abstract void pong(Channel channel);

    public abstract void unsubscribe(Channel channel, MqttUnsubscribeMessage mqttMessage);


    public abstract void disconnect(Channel channel);

    public abstract void doTimeOut(Channel channel, IdleStateEvent evt);
}

