package org.konggradio.unicron.iot.serverQos;

import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.unicron.iot.ip.IpUtils;

@Slf4j
public class CurrentQos {
    public static MqttQoS QoS = MqttQoS.AT_MOST_ONCE;

    public static MqttQoS QoS0 = MqttQoS.AT_MOST_ONCE;
    public static MqttQoS QoS1 = MqttQoS.AT_LEAST_ONCE;
    public static MqttQoS QoS2 = MqttQoS.EXACTLY_ONCE;

    public static MqttQoS getQos() {
        String ip = IpUtils.getHost();
        boolean result = false;
        if (result) {
            return QoS2;
        }
        return QoS2;

    }
}
