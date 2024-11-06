package org.konggradio.unicron.iot.bootstrap.channel;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import com.alibaba.fastjson.JSONObject;
import org.konggradio.unicron.iot.bootstrap.cache.Cache;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.unicron.iot.enums.ConfirmStatus;
import org.konggradio.unicron.iot.mqtt.ClientConnectionService;
import org.konggradio.unicron.iot.mqtt.ClientMqttHandlerService;

/**
 * 客户端channelService 处理收到消息信息
 *
 * @author StormRiver
 * @create 2018-01-02 20:48
 **/
@Slf4j
public class MqttHandlerServiceService extends ClientMqttHandlerService {

	ClientConnectionService clientConnectionService=new ClientConnectionService();
    @Override
    public void close(Channel channel) {}
    //modify by lianaydong,增加对Qos 1/2支持
    //模块: unicron-mqtt-client
    //package: com.xiaojukeji.unicron.iot.bootstrap.channel;
    @Override
    public void puback(Channel channel, MqttMessage mqttMessage) {
    	Object header=mqttMessage.variableHeader();
    	int messageId = 0;
    	if(header instanceof MqttMessageIdVariableHeader){
    		MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
            messageId = messageIdVariableHeader.messageId();
    	}

    	if(header instanceof MqttPublishVariableHeader){
    		MqttPublishVariableHeader messageIdVariableHeader = (MqttPublishVariableHeader) mqttMessage.variableHeader();
            messageId = messageIdVariableHeader.messageId();
    	}
        Optional.ofNullable(Cache.del(messageId)).ifPresent(sendMqttMessage -> {
            sendMqttMessage.setConfirmStatus(ConfirmStatus.COMPLETE);
        });
    }

    @Override
    public void pubrec(Channel channel, MqttMessage mqttMessage ) {
        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        int messageId = messageIdVariableHeader.messageId();
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREL,false, MqttQoS.AT_LEAST_ONCE,false,0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        MqttMessage mqttPubAckMessage = new MqttMessage(mqttFixedHeader,from);
        Optional.ofNullable(Cache.get(messageId)).ifPresent(sendMqttMessage -> {
            sendMqttMessage.setTimestamp(System.currentTimeMillis());
            sendMqttMessage.setConfirmStatus(ConfirmStatus.PUBREL);
        });
        channel.writeAndFlush(mqttPubAckMessage);
    }

    @Override
    public void pubrel(Channel channel, MqttMessage mqttMessage ) {
        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        int messageId = messageIdVariableHeader.messageId();
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBCOMP,false, MqttQoS.AT_MOST_ONCE,false,0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        MqttMessage mqttPubAckMessage = new MqttMessage(mqttFixedHeader,from);
        Optional.ofNullable(Cache.del(messageId)).ifPresent(sendMqttMessage -> {
            sendMqttMessage.setConfirmStatus(ConfirmStatus.COMPLETE);
        });
        channel.writeAndFlush(mqttPubAckMessage);
    }

    @Override
    public void pubcomp(Channel channel,MqttMessage mqttMessage ) {
        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        int messageId = messageIdVariableHeader.messageId();
        Optional.ofNullable(Cache.del(messageId)).ifPresent(sendMqttMessage -> {
            sendMqttMessage.setConfirmStatus(ConfirmStatus.COMPLETE);
        });
        log.info("客户端发布的消息已完成:{}",JSONObject.toJSON(channel));
    }

    @Override
    public void heart(Channel channel, IdleStateEvent evt) {

        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttMessage mqttMessage  = new MqttMessage(fixedHeader);
        log.info("发送心跳:{}",JSONObject.toJSON(channel));
        channel.writeAndFlush(mqttMessage);
    }


    public void heart_bak(Channel channel, IdleStateEvent evt) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0);
        /*CMD_102 cmd=new CMD_102();
        String msg=cmd.pack();
		ByteBuf heapBuffer = Unpooled.buffer(msg.length());
		// 2、写入缓冲区内容
		try {
			byte[] toSend=msg.getBytes(CharsetDef.CHARSET);
			log.info("心跳包发送装包:");
			EncodeUtil.print(toSend);
			heapBuffer.writeBytes(toSend);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		//Object mqttVariableHeader;
		//MqttMessage mqttMessage  = new MqttMessage(fixedHeader,mqttVariableHeader,heapBuffer);
		MqttMessage mqttMessage  = new MqttMessage(fixedHeader);
        log.info("发送心跳");
        channel.writeAndFlush(mqttMessage);
    }

    public void suback(Channel channel,MqttSubAckMessage mqttMessage) {
    	log.info("【客户端】suback:"+mqttMessage.payload().toString());
        ScheduledFuture<?> scheduledFuture = channel.attr(getKey(Integer.toString(mqttMessage.variableHeader().messageId()))).get();
        if(scheduledFuture!=null){
            scheduledFuture.cancel(true);
        }
    }
    //qos1 send
    @Override
    public void pubBackMessage(Channel channel, int messageId) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK,false, MqttQoS.AT_LEAST_ONCE,false,0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        MqttPubAckMessage mqttPubAckMessage = new MqttPubAckMessage(mqttFixedHeader,from);
        channel.writeAndFlush(mqttPubAckMessage);
    }

    @Override
    public void unsubBack(Channel channel, MqttMessage mqttMessage) {
        int messageId;
        if(mqttMessage instanceof  MqttUnsubAckMessage){
            MqttUnsubAckMessage mqttUnsubAckMessage = (MqttUnsubAckMessage)mqttMessage;
            messageId= mqttUnsubAckMessage.variableHeader().messageId();
        }
        else {
            MqttMessageIdVariableHeader o =(MqttMessageIdVariableHeader) mqttMessage.variableHeader();
            messageId= o.messageId();
        }
        if(messageId>0){
            ScheduledFuture<?> scheduledFuture = channel.attr(getKey(Integer.toString(messageId))).get();
            if(!scheduledFuture.isCancelled()){
                scheduledFuture.cancel(true);
            }
        }
    }

    private AttributeKey<ScheduledFuture<?>> getKey(String id){
        return   AttributeKey.valueOf(id);
    }

}
