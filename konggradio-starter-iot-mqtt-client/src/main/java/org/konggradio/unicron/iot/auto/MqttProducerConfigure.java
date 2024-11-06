package org.konggradio.unicron.iot.auto;

import java.util.Map;
import java.util.Optional;

import org.konggradio.unicron.iot.bootstrap.Bean.SubMessage;
import org.konggradio.unicron.iot.bootstrap.MqttProducer;
import org.konggradio.unicron.iot.bootstrap.Producer;
import org.konggradio.unicron.iot.properties.ConnectOptions;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


/**
 * 自动配置类
 *
 * @author StormRiver
 * @create 2018-01-04 20:50
 **/

@Configuration
@ConditionalOnClass({MqttProducer.class})
@EnableConfigurationProperties({ConnectOptions.class})
public class MqttProducerConfigure   implements ApplicationContextAware,DisposableBean {

    private static  final  int _BLACKLOG =   1024;

    private static final  int  CPU =Runtime.getRuntime().availableProcessors();

    private static final  int  SEDU_DAY =10;

    private static final  int TIMEOUT =120;

    private static final  int BUF_SIZE=10*1024*1024;

    private ConfigurableApplicationContext applicationContext;


    @Bean
    @ConditionalOnMissingBean()
    public Producer initServer(ConnectOptions connectOptions, Environment env){
        MqttProducer mqttProducer = new MqttProducer();
        Map<String, Object> beansWithAnnotation = this.applicationContext.getBeansWithAnnotation(MqttMessageListener.class);
        checkArgs(connectOptions);
        final SubMessage[] build = new SubMessage[1];
        Optional.of(beansWithAnnotation).ifPresent((Map<String, Object> mqttListener) -> {
            beansWithAnnotation.forEach((name, bean) -> {
                Class<?> clazz = AopUtils.getTargetClass(bean);
                if (!MqttListener.class.isAssignableFrom(bean.getClass())) {
                    throw new IllegalStateException(clazz + " is not instance of " + MqttListener.class.getName());
                }
                MqttMessageListener annotation = clazz.getAnnotation(MqttMessageListener.class);
                MqttListener listener = (MqttListener) bean;
                mqttProducer.setMqttListener(listener);
                build[0] = SubMessage.builder()
                        .qos(annotation.qos())
                        .topic(annotation.topic())
                        .build();
            });
        });
        mqttProducer.connect(connectOptions);
        mqttProducer.sub(build[0]);
        return mqttProducer;
    }
    private void checkArgs(ConnectOptions connectOptions) {
        if(connectOptions.getServerIp()==null)
            throw  new RuntimeException("ip地址为空");
        if(connectOptions.getPort()<1)
            throw new RuntimeException("端口号为空");
        if(connectOptions.getBacklog()<1)
            connectOptions.setBacklog(_BLACKLOG);
        if(connectOptions.getBossThread()<1)
            connectOptions.setBossThread(CPU);
        if (connectOptions.getConnectTime()<1)
            connectOptions.setConnectTime(10);
        if (connectOptions.getHeart()<1)
            connectOptions.setConnectTime(120);
        if(connectOptions.getMinPeriod()<1)
            connectOptions.setMinPeriod(10);
        if(connectOptions.getRevbuf()<1)
            connectOptions.setRevbuf(BUF_SIZE);
        if(connectOptions.getSndbuf()<1)
            connectOptions.setSndbuf(BUF_SIZE);
        ConnectOptions.MqttOpntions mqtt=connectOptions.getMqtt();
        if(mqtt!=null){
            if(mqtt.getClientIdentifier()==null)
                throw  new RuntimeException("设备号为空");
            if(mqtt.getKeepAliveTime()<1)
                mqtt.setKeepAliveTime(100);
            if (mqtt.isHasUserName()&&mqtt.getUserName()==null)
                throw new RuntimeException("未设置用户");
            if (mqtt.isHasPassword()&&mqtt.getPassword()==null)
                throw new RuntimeException("未设置密码");
            if(!mqtt.isWillFlag()){
                mqtt.setWillRetain(false);
                mqtt.setWillQos(0);
                mqtt.setWillMessage(null);
                mqtt.setWillTopic(null);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void destroy() throws Exception {
        Producer bean = applicationContext.getBean(Producer.class);
        if(bean!=null){
            bean.close();
        }
    }
}
