package org.konggradio.unicron.iot.bootstrap;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.konggradio.unicron.iot.bootstrap.Bean.SendMqttMessage;
import org.konggradio.unicron.iot.bootstrap.Bean.SubMessage;


import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.unicron.iot.constant.CharsetDef;
import org.konggradio.unicron.iot.enums.ConfirmStatus;
import org.konggradio.unicron.iot.properties.ConnectOptions;
import org.konggradio.unicron.iot.util.MessageId;


/**
 * mqtt api操作类
 *
 * @author StormRiver
 * @create 2018-01-04 15:10
 **/
@Slf4j
public class MqttProducer  extends  AbsMqttProducer{

	private int RETRY_TIMES=5;
    public  Producer connect(ConnectOptions connectOptions){
    	int cnt=0;
    	while(this.channel == null && cnt<=RETRY_TIMES){
    		connectTo(connectOptions);
    		cnt++;
    		log.info("尝试连接次数："+cnt+" / "+RETRY_TIMES);
    		if(this.channel != null){
    			break;
    		}
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        return this;
    }

    @Override
    public void pub(String topic,String message,int qos){
        pub(topic,message,false,qos);
    }

    @Override
    public void pub(String topic, String message, boolean retained) {
        pub(topic,message,retained,0);
    }
    @Override
    public void pub(String topic,String message){
        pub(topic,message,false,0);
    }


    @Override
    public void pub(String topic, String message, boolean retained, int qos) {
        Optional.ofNullable(buildMqttMessage(topic, message, retained, qos, false, true))
        .ifPresent(sendMqttMessage -> {
            pubMessage(channel, sendMqttMessage);
        });
    }

    /*
     @注意编码！！！
     */
    private SendMqttMessage buildMqttMessage(String topic, String message, boolean retained, int qos, boolean dup, boolean time) {
        int messageId=0;
        if(qos!=0){
            messageId= MessageId.messageId();
        }
        try {
            return SendMqttMessage.builder().messageId(messageId)
                    .Topic(topic)
                    .dup(dup)
                    .retained(retained)
                    .qos(qos)
                    .confirmStatus(ConfirmStatus.PUB)
                    .timestamp(System.currentTimeMillis())
                    .payload(message.getBytes(CharsetDef.CHARSET)).build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void sub(SubMessage... subMessages){
        Optional.ofNullable(getSubTopics(subMessages)).ifPresent(mqttTopicSubscriptions -> {
            int messageId = MessageId.messageId();
            subMessage(channel, mqttTopicSubscriptions,messageId);
            topics.addAll(mqttTopicSubscriptions);
        });
    }

    @Override
    public void unsub(List<String> topics) {
        Optional.ofNullable(topics).ifPresent(strings -> {
            int messageId = MessageId.messageId();
            super.unsub(strings,messageId);
        });
    }

    @Override
    public void unsub(){
        unsub(toList());
    }



    private List<MqttTopicSubscription> getSubTopics(SubMessage[]subMessages ) {
        return  Optional.ofNullable(subMessages)
                .map(subMessages1 -> {
                    List<MqttTopicSubscription> mqttTopicSubscriptions = new LinkedList<>();
                    for(SubMessage sb :subMessages1){
                        MqttTopicSubscription mqttTopicSubscription  = new MqttTopicSubscription(sb.getTopic(),sb.getQos());
                        mqttTopicSubscriptions.add(mqttTopicSubscription);
                    }
                    return mqttTopicSubscriptions;
                }).orElse(null);
    }

    private List<String> toList(){
        return Optional.ofNullable(topics).
                map(mqttTopicSubscriptions ->
                        mqttTopicSubscriptions.stream().
                                map(mqttTopicSubscription -> mqttTopicSubscription.topicName()).collect(Collectors.toList()))
                .orElse(null);
    }


    private List<String> getTopics(SubMessage[] subMessages) {
        return  Optional.ofNullable(subMessages)
                .map(subMessages1 -> {
                    List<String> mqttTopicSubscriptions = new LinkedList<>();
                    for(SubMessage sb :subMessages1){
                        mqttTopicSubscriptions.add(sb.getTopic());
                    }
                    return mqttTopicSubscriptions;
                }).orElse(null);
    }




}
