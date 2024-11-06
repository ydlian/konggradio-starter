package org.konggradio.unicron.iot.bootstrap;

import org.konggradio.unicron.iot.auto.MqttListener;
import org.konggradio.unicron.iot.bootstrap.Bean.SubMessage;

import io.netty.channel.Channel;
import org.konggradio.unicron.iot.properties.ConnectOptions;

import java.util.List;

/**
 * 生产者
 *
 * @author StormRiver
 * @create 2018-01-04 17:17
 **/
public interface Producer {

    Channel getChannel();

    Producer connect(ConnectOptions connectOptions);

    void  close();

    void setMqttListener(MqttListener mqttListener);

    void pub(String topic,String message,boolean retained,int qos);

    void pub(String topic,String message);

    void pub(String topic,String message,int qos);

    void pub(String topic,String message,boolean retained);

    void sub(SubMessage... subMessages);

    void unsub(List<String> topics);

    void unsub();

    void disConnect();

}
