package org.konggradio.unicron.iot.bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.konggradio.unicron.iot.bootstrap.Bean.SendMqttMessage;
import org.konggradio.unicron.iot.bootstrap.cache.Cache;
import org.konggradio.unicron.iot.bootstrap.scan.SacnScheduled;
import org.konggradio.unicron.iot.enums.ConfirmStatus;
import org.konggradio.unicron.iot.ip.IpUtils;
import org.konggradio.unicron.iot.mqtt.ClientConnectionService;
import org.konggradio.unicron.iot.properties.ConnectOptions;
import org.konggradio.unicron.iot.util.MessageId;
import org.springframework.beans.factory.annotation.Autowired;

import org.konggradio.unicron.iot.auto.MqttListener;
import org.konggradio.unicron.iot.bootstrap.channel.MqttHandlerServiceService;
import org.konggradio.unicron.iot.bootstrap.handler.DefaultMqttHandler;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作类
 *
 * @author StormRiver
 * @create 2018-01-04 17:23
 **/
@Slf4j
public abstract class AbsMqttProducer extends MqttApi implements  Producer {

	@Autowired
	ClientConnectionService clientConnectionService;

    protected   Channel channel;

    protected  MqttListener mqttListener;

    private  NettyBootstrapClient nettyBootstrapClient ;

    protected SacnScheduled sacnScheduled;

    protected List<MqttTopicSubscription> topics = new ArrayList<>();


    private  static final CountDownLatch countDownLatch = new CountDownLatch(1);

    public AbsMqttProducer(){
    	if(clientConnectionService==null){
    		clientConnectionService=new ClientConnectionService();
    	}
    }

    protected   void  connectTo(ConnectOptions connectOptions){
    	log.info("connectTo ...");
        if(this.nettyBootstrapClient ==null){
            this.nettyBootstrapClient = new NettyBootstrapClient(connectOptions);
        }
        this.channel =nettyBootstrapClient.start();
        initPool(connectOptions.getMinPeriod());
        try {
            countDownLatch.await(connectOptions.getConnectTime(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        	e.printStackTrace();
        	//sofalala
        	log.info("重新连接");
            nettyBootstrapClient.doubleConnect(); // 重新连接
        }
    }

    @Override
    public void disConnect() {
    	log.info(" MQTT disConnect!");
        sendDisConnect(channel);
    }


    @Override
    public void pubRecMessage(Channel channel, int messageId) {
        SendMqttMessage sendMqttMessage= SendMqttMessage.builder().messageId(messageId)
                .confirmStatus(ConfirmStatus.PUBREC)
                .timestamp(System.currentTimeMillis())
                .build();
        Cache.put(messageId,sendMqttMessage);
        boolean flag;
        do {
            flag = sacnScheduled.addQueue(sendMqttMessage);
        } while (!flag);
        log.info("[Client]pubRecMessage"+channel);
        super.pubRecMessage(channel, messageId);
    }

    @Override
    protected void pubMessage(Channel channel, SendMqttMessage mqttMessage) {
        super.pubMessage(channel, mqttMessage);

        //log.info(" [客户端发送]pubMessage"+channel);
        if(mqttMessage.getQos()!=0){
            Cache.put(mqttMessage.getMessageId(),mqttMessage);
            boolean flag;
            do {
                flag = sacnScheduled.addQueue(mqttMessage);

            } while (!flag);
        }
    }

    protected void initPool( int seconds){
        this.sacnScheduled =new SacnScheduled(this,seconds);
        sacnScheduled.start();
    }

    //sofalala
    @Override
    protected void subMessage(Channel channel, List<MqttTopicSubscription> mqttTopicSubscriptions, int messageId) {

    	log.info("++subMessage++:"+messageId);
    	ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {

    		//SUBACK确认收到后要停止发送订阅消息
    		if(channel.isActive()){
                subMessage(channel, mqttTopicSubscriptions, messageId);
            }

        }, 10, 60, TimeUnit.SECONDS);
    	if(channel!=null){
    		channel.attr(getKey(Integer.toString(messageId))).setIfAbsent(scheduledFuture);

    	}

        super.subMessage(channel, mqttTopicSubscriptions, messageId);
    }


    public void unsub(List<String> topics,int messageId) {
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            if(channel.isActive()){
                unSubMessage(channel, topics, messageId);
            }
        }, 10, 10, TimeUnit.SECONDS);
        channel.attr(getKey(Integer.toString(messageId))).setIfAbsent(scheduledFuture);
        unSubMessage(channel, topics, messageId);
    }

    @Override
    public void close() {
        if(nettyBootstrapClient!=null){
            nettyBootstrapClient.shutdown();
        }
        if(sacnScheduled!=null){
            sacnScheduled.close();
        }
        //add 2018-04-10
        log.info(" MQTT close!");
    }

    public  void connectBack(MqttConnAckMessage mqttConnAckMessage){
        MqttConnAckVariableHeader mqttConnAckVariableHeader = mqttConnAckMessage.variableHeader();
        switch ( mqttConnAckVariableHeader.connectReturnCode()){
            case CONNECTION_ACCEPTED:
                countDownLatch.countDown();
                break;
            case CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD:
                throw new RuntimeException("用户名密码错误");
            case CONNECTION_REFUSED_IDENTIFIER_REJECTED:
                throw  new RuntimeException("clientId  不允许链接");
            case CONNECTION_REFUSED_SERVER_UNAVAILABLE:
                throw new RuntimeException("服务不可用");
            case CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION:
                throw new RuntimeException("mqtt 版本不可用");
            case CONNECTION_REFUSED_NOT_AUTHORIZED:
                throw new RuntimeException("未授权登录");
        }

    }

    public class NettyBootstrapClient extends AbstractBootstrapClient {

        private NioEventLoopGroup bossGroup;

        Bootstrap bootstrap=null ;// 启动辅助类

        private ConnectOptions connectOptions;


        public NettyBootstrapClient(ConnectOptions connectOptions) {
            this.connectOptions = connectOptions;
        }


        public void doubleConnect(){
            ChannelFuture connect = bootstrap.connect(connectOptions.getServerIp(), connectOptions.getPort());
            connect.addListener((ChannelFutureListener) future -> {

                if (future.isSuccess()){
                    AbsMqttProducer absMqttProducer = AbsMqttProducer.this;
                    absMqttProducer.channel =future.channel();
                    absMqttProducer.subMessage(future.channel(),topics, MessageId.messageId());
                }
                else
                    doubleConnect();
            });
        }
        @Override
        public Channel start() {
        	log.info("client start ...");
            initEventPool();
            bootstrap.group(bossGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, connectOptions.isTcpNodelay())
                    .option(ChannelOption.SO_KEEPALIVE, connectOptions.isKeepalive())
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_SNDBUF, connectOptions.getSndbuf())
                    .option(ChannelOption.SO_RCVBUF, connectOptions.getRevbuf())
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            initHandler(ch.pipeline(),connectOptions,new DefaultMqttHandler(connectOptions,new MqttHandlerServiceService(), AbsMqttProducer.this, mqttListener));
                        }
                    });
            try {
                return bootstrap.connect(connectOptions.getServerIp(), connectOptions.getPort()).sync().channel();
            } catch (Exception e) {
                log.info("connect to channel fail ",e);
            }
            return null;
        }
        @Override
        public void shutdown() {
            if( bossGroup!=null ){
                try {
                    bossGroup.shutdownGracefully().sync();// 优雅关闭
                } catch (InterruptedException e) {
                    log.info("客户端关闭资源失败【" + IpUtils.getHost() + ":" + connectOptions.getPort() + "】");
                }
            }
        }

        @Override
        public void initEventPool() {
            bootstrap= new Bootstrap();
            bossGroup = new NioEventLoopGroup(4, new ThreadFactory() {
                private AtomicInteger index = new AtomicInteger(0);
                public Thread newThread(Runnable r) {
                    return new Thread(r, "BOSS_" + index.incrementAndGet());
                }
            });
        }
    }

    public NettyBootstrapClient getNettyBootstrapClient() {
        return nettyBootstrapClient;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setMqttListener(MqttListener mqttListener) {
        this.mqttListener = mqttListener;
    }


}
