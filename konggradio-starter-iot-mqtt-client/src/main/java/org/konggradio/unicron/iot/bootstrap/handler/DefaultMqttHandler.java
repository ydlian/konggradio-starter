package org.konggradio.unicron.iot.bootstrap.handler;

import org.konggradio.unicron.iot.auto.MqttListener;
import org.konggradio.unicron.iot.bootstrap.MqttProducer;
import org.konggradio.unicron.iot.bootstrap.Producer;
import org.konggradio.unicron.iot.mqtt.ClientConnectionService;
import org.konggradio.unicron.iot.mqtt.ClientMqttHandlerService;
import org.konggradio.unicron.iot.mqtt.MqttHander;
import org.konggradio.unicron.iot.properties.ConnectOptions;
import org.konggradio.unicron.iot.util.ByteBufUtil;
import org.springframework.beans.factory.annotation.Autowired;



import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认 mqtthandler处理
 *
 * @author StormRiver
 * @create 2017-11-20 13:58
 **/

@ChannelHandler.Sharable
@Slf4j

public class DefaultMqttHandler extends MqttHander {

	@Autowired
	ClientConnectionService clientConnectionService;

    private ClientMqttHandlerService mqttHandlerApi;

    private MqttProducer mqttProducer;

    private MqttListener mqttListener;

    private ConnectOptions connectOptions;

    public DefaultMqttHandler(ConnectOptions connectOptions, ClientMqttHandlerService mqttHandlerApi, Producer producer, MqttListener mqttListener) {
        super(mqttHandlerApi);
        this.mqttProducer =(MqttProducer) producer;
        this.mqttListener = mqttListener;
        this.mqttHandlerApi=mqttHandlerApi;
        this.connectOptions=connectOptions;
        if(clientConnectionService ==null){
        	clientConnectionService=new ClientConnectionService();
        }
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        ConnectOptions.MqttOpntions mqtt = connectOptions.getMqtt();
        //clientConnectionService.creatConnection(channel, mqtt.getClientIdentifier());
        log.info("【DefaultMqttHandler：channelActive】"+ctx.channel().localAddress().toString()+"启动成功");
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT,false, MqttQoS.AT_LEAST_ONCE,false,10);
        MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(MqttVersion.MQTT_3_1_1.protocolName(),MqttVersion.MQTT_3_1_1.protocolLevel(),mqtt.isHasUserName(),mqtt.isHasPassword(),mqtt.isWillRetain(),mqtt.getWillQos(),mqtt.isWillFlag(),mqtt.isCleanSession(),mqtt.getKeepAliveTime());
        MqttConnectPayload mqttConnectPayload = new MqttConnectPayload(mqtt.getClientIdentifier(),mqtt.getWillTopic(),mqtt.getWillMessage(),mqtt.getUserName(),mqtt.getPassword());
        MqttConnectMessage mqttSubscribeMessage = new MqttConnectMessage(mqttFixedHeader,mqttConnectVariableHeader,mqttConnectPayload);
        channel.writeAndFlush(mqttSubscribeMessage);
        //super.channelActive(ctx);
    }

    @Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	ConnectOptions.MqttOpntions mqtt = connectOptions.getMqtt();
    	try{
    		if(mqtt.getClientIdentifier()!=null){
        		clientConnectionService.distroyConnection(mqtt.getClientIdentifier().trim());
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{

    		log.info("【channelInactive】"+ctx.channel().remoteAddress().toString()+"关闭成功");
        	super.channelInactive(ctx);
        	try{
        		ctx.close();
            	//add by 2018-04-10 关闭
            	mqttProducer.close();
            	mqttProducer.disConnect();
        	}catch(Exception e){
        		e.printStackTrace();
        	}

    	}



    }

    @Override
    public void doMessage(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) {
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        switch (mqttFixedHeader.messageType()){
            case UNSUBACK:
                mqttHandlerApi.unsubBack(channelHandlerContext.channel(),mqttMessage);
                break;
            case CONNACK:
                mqttProducer.connectBack((MqttConnAckMessage) mqttMessage);
                break;
            case PUBLISH:

                publish(channelHandlerContext.channel(),(MqttPublishMessage)mqttMessage);
                //break;
            case PUBACK: // qos 1回复确认
                mqttHandlerApi.puback(channelHandlerContext.channel(),mqttMessage);
                break;
            case PUBREC: //
                mqttHandlerApi.pubrec(channelHandlerContext.channel(),mqttMessage);
                break;
            case PUBREL: //
                mqttHandlerApi.pubrel(channelHandlerContext.channel(),mqttMessage);
                break;
            case PUBCOMP: //
                mqttHandlerApi.pubcomp(channelHandlerContext.channel(),mqttMessage);
                break;
            case SUBACK:
                mqttHandlerApi.suback(channelHandlerContext.channel(),(MqttSubAckMessage)mqttMessage);
                //break;
            default:
                break;
        }
    }

    private void publish(Channel channel,MqttPublishMessage mqttMessage) {
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        MqttPublishVariableHeader mqttPublishVariableHeader = mqttMessage.variableHeader();
        ByteBuf payload = mqttMessage.payload();
        byte[] bytes = ByteBufUtil.copyByteBuf(payload); //
        if(mqttListener!=null){
            mqttListener.callBack(mqttPublishVariableHeader.topicName(),new String(bytes));
        }
        switch (mqttFixedHeader.qosLevel()){
            case AT_MOST_ONCE:
                break;
            case AT_LEAST_ONCE:
                mqttHandlerApi.pubBackMessage(channel,mqttPublishVariableHeader.messageId());
                break;
            case EXACTLY_ONCE:
                mqttProducer.pubRecMessage(channel,mqttPublishVariableHeader.messageId());
                break;
		default:
			break;
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        mqttProducer.getNettyBootstrapClient().doubleConnect();
        log.error("exception",cause);
        if(mqttListener!=null){
            mqttListener.callThrowable(cause);
        }
    }


}
