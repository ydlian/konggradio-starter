package org.konggradio.unicron.iot.bootstrap.handler;

import com.alibaba.fastjson.JSONObject;

import org.konggradio.unicron.iot.bean.ClientConnectionInfo;
import org.konggradio.unicron.iot.bootstrap.ChannelService;
import org.konggradio.unicron.iot.bootstrap.bean.MqttChannel;
import org.konggradio.unicron.iot.bootstrap.channel.StationManagementService;
import org.konggradio.unicron.iot.bootstrap.coder.server.ServerDecoder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.unicron.iot.ip.IpUtils;
import org.konggradio.unicron.iot.mqtt.ClientConnectionService;
import org.konggradio.unicron.iot.mqtt.MqttHander;
import org.konggradio.unicron.iot.mqtt.MqttHandlerInterface;
import org.konggradio.unicron.iot.mqtt.ServerMqttHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认 mqtthandler处理
 *
 * @author lianyadong
 * @create 2023-11-20 13:58
 **/

@ChannelHandler.Sharable
@Slf4j
@Component
public class DefaultMqttHandler extends MqttHander {

    private static long msgCnt = 0;
    private final MqttHandlerInterface mqttHandlerApi;
    @Autowired
    private ServerDecoder serverDecoder;
    @Autowired
    private StationManagementService stationManagementService;
    @Autowired
    private ClientConnectionService clientConnectionService;
    @Autowired
    private ChannelService channelService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private ConcurrentHashMap<String, String> _cache_datas = new ConcurrentHashMap<String, String>();

    public DefaultMqttHandler(MqttHandlerInterface mqttHandlerApi, MqttHandlerInterface mqttHandlerApi1) {
        super(mqttHandlerApi);
        this.mqttHandlerApi = mqttHandlerApi1;
    }

    private void serverRejectConnection(ServerMqttHandlerService serverMqttHandlerService, Channel channel) {

        serverMqttHandlerService.disconnect(channel);
        channel.close();
    }

    @Override
    public void doMessage(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) {
        Channel channel = channelHandlerContext.channel();
        msgCnt++;
        String stationEquipId = channelService.getDeviceId(channel);

        ServerMqttHandlerService serverMqttHandlerService = null;

        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();

        try {
            int msgType = mqttFixedHeader.messageType().value();
            int msgQos = mqttFixedHeader.qosLevel().value();
            int messageId = -1;
            try {
                MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) mqttMessage
                        .variableHeader();
                if (messageIdVariableHeader != null) {
                    messageId = messageIdVariableHeader.messageId();
                }
            } catch (Exception e) {
                messageId = -1;
                //e.printStackTrace();
            }
            log.info("[msg_rec_log],收到消息，类型={},msgType={},msgQos={},messageId={},消息体={}",
                    mqttFixedHeader.messageType(), msgType, msgQos, messageId, mqttMessage);

            if (msgType <= 0
                    || msgQos < 0
                    || msgQos > MqttQoS.EXACTLY_ONCE.value()) {
                return;
            }


            if (mqttHandlerApi instanceof ServerMqttHandlerService) {
                serverMqttHandlerService = (ServerMqttHandlerService) mqttHandlerApi;
            } else {
                log.error("ServerMqttHandlerService not match!");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("异常消息类型:{},mqttFixedHeader={}", e.getCause(), mqttFixedHeader);
            return;
        }
        String thisServer = IpUtils.getHost();
        //
        try {
            if (mqttFixedHeader.messageType().equals(MqttMessageType.CONNECT)) {

                MqttConnectMessage connMsg = (MqttConnectMessage) mqttMessage;
                String deviceId = connMsg.payload().clientIdentifier();
                //ClientConnectionInfo client=clientConnectionService.getConnectInfo(deviceId);

                if (clientConnectionService.isClientThisNodeOnline(deviceId)) {


                }


                log.info("[cliet]开始建立连接：Starting connect....{},{},{}", thisServer, channel.id(), deviceId);

                try {
                    onClientConnect(channelHandlerContext, connMsg);
                } catch (Exception e) {
                    log.error("connect error", e);
                }
                if (!serverMqttHandlerService.login(channel, (MqttConnectMessage) mqttMessage)) {
                    log.info("[#####[login_check_fail]######]客户端登陆检查失败,当前连接关闭，{},{}", JSONObject.toJSON(channel), deviceId);

                    MqttChannel conchannel = channelService.getMqttChannel(deviceId);

                    //拒绝连接
                    serverRejectConnection(serverMqttHandlerService, channel);
                } else {

                    log.info("[login_check_success],{}", deviceId);

                }
                log.info("[doMessage]connect 消息处理完毕,{}", deviceId);
                return;
            }//处理连接建立的消息


            String theDeviceId = channelService.getDeviceId(channel);
            if (theDeviceId == null) {
                log.info("[doMessage]theDeviceId==null,继续处理");
                //return;
            }

            if (mqttFixedHeader.messageType().value() == MqttMessageType.PUBLISH.value()) {

            }


            MqttChannel theMqttChannel = channelService.getMqttChannel(theDeviceId);
            if (theDeviceId == null || theMqttChannel == null) {
                //serverRejectConnection(serverMqttHandlerService,channel);
                log.info("[doMessage]theDeviceId == null || theMqttChannel==null,继续");
                //return;
            }

            if (!channelService.getMqttChannel(channelService.getDeviceId(channel)).isLogin()) {

                log.info("[doMessage]NOT Login");
                //return;
            }

            /*if (channelService.getMqttChannel(channelService.getDeviceId(channel)) != null
                    && channelService.getMqttChannel(channelService.getDeviceId(channel)).isLogin()) {*/

            switch (mqttFixedHeader.messageType()) {
                case PUBLISH:
                    break;
                case SUBSCRIBE:
                    log.info("[++Server处理++SUBSCRIBE]:stationEquipId={}", stationEquipId);
                    serverMqttHandlerService.subscribe(channel, (MqttSubscribeMessage) mqttMessage);
                    break;
                case PINGREQ:

                    log.info("_com_unicron_logic_server||traceid={}||客户端心跳消息PINGREQ={}", theDeviceId, JSONObject.toJSONString(channel));
                    serverMqttHandlerService.pong(channel);
                    //ClientConnectionInfo newClient=new ClientConnectionInfo();
                    clientConnectionService.updateConnection(channel, stationEquipId);

                    break;

                case DISCONNECT:
                    log.debug("客户端请求断开连接。");
                    MqttChannel conchannel = channelService.getMqttChannel(theDeviceId);
                    if (conchannel != null) {
                        channelService.deleteMqttChannel(conchannel);
                    }
                    stationManagementService.deleteStation(stationEquipId);
                    serverMqttHandlerService.disconnect(channel);
                    break;
                case UNSUBSCRIBE:
                    //log.debug("UNSUBSCRIBE");
                    serverMqttHandlerService.unsubscribe(channel, (MqttUnsubscribeMessage) mqttMessage);
                    break;
                case PUBACK:
                    //log.debug("PUBACK");
                    mqttHandlerApi.puback(channel, mqttMessage);
                    break;
                case PUBREC:
                    //log.debug("PUBREC");
                    mqttHandlerApi.pubrec(channel, mqttMessage);
                    break;
                case PUBREL:
                    mqttHandlerApi.pubrel(channel, mqttMessage);
                    break;
                case PUBCOMP:
                    mqttHandlerApi.pubcomp(channel, mqttMessage);
                    break;
                default:
                    break;
            }
            /*} else {
                //log.error("Something error!ID={} ", stationEquipId);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            log.info("[doMessage]捕获异常:{}", e.getCause());
        }

    }

    //建立连接时
    private void onClientConnect(ChannelHandlerContext ctx, MqttConnectMessage connMsg) {
        String theDeviceId = connMsg.payload().clientIdentifier();
        String remoteAddr = ctx.channel().remoteAddress().toString();
        log.info("【onClientConnect】{},{},正在建立链接", remoteAddr, theDeviceId);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String theDeviceId = channelService.getDeviceId(ctx.channel());

        log.info("[DefaultMqttHandler:channelInactive]连接关闭！theDeviceId={},addr={}", theDeviceId, ctx.channel().remoteAddress().toString());
        //clientConnectionService.printConnectionInfo();
        if (theDeviceId != null) {
            //stringRedisTemplate.opsForValue().set(RedisConstant.UNICRON_IS_DEVICE_ONLINE+theDeviceId, "0");
            log.info("【关闭连接：DefaultMqttHandler->channelInactive】ID={}", theDeviceId);
			//iStationService.updateStationConnectStatus(theDeviceId, "0");
            ClientConnectionInfo client = clientConnectionService.getThisNodeConnectInfo(theDeviceId);
            MqttChannel conchannel = channelService.getMqttChannel(theDeviceId);
            if (conchannel != null) {
                channelService.deleteMqttChannel(conchannel);
            }
        }
        stationManagementService.deleteStation(theDeviceId);
        super.channelInactive(ctx);
    }
    /*
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    }*/

}
