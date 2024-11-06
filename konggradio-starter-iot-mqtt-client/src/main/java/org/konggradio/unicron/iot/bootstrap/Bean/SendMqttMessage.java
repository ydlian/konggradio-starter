package org.konggradio.unicron.iot.bootstrap.Bean;


import lombok.Builder;
import lombok.Data;
import org.konggradio.unicron.iot.enums.ConfirmStatus;

/**
 * 消息
 *
 * @author StormRiver
 * @create 2018-01-04 19:36
 **/
@Data
@Builder
public class SendMqttMessage {

    private String Topic;

    private byte[] payload;

    private int qos;

    private boolean retained;

    private boolean dup;

    private int messageId;


    private long timestamp;

    private volatile ConfirmStatus confirmStatus;


}
