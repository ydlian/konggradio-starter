package org.konggradio.unicron.iot.bootstrap.channel;

import org.konggradio.unicron.iot.bootstrap.BaseApi;
import org.konggradio.unicron.iot.bootstrap.ChannelService;
import org.konggradio.unicron.iot.bootstrap.bean.WillMeaasge;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 遗嘱消息处理
 *
 * @author lianyadong
 * @create 2023-11-23 11:20
 **/
@Slf4j
@Component
@Setter
@Getter
@NoArgsConstructor
public class WillService implements BaseApi {


    private static ConcurrentHashMap<String, WillMeaasge> willMeaasges = new ConcurrentHashMap<>(); // deviceid -WillMeaasge
    @Autowired
    ChannelService channelService;

    /**
     * 保存遗嘱消息
     */
    public void save(String deviceid, WillMeaasge build) {
        willMeaasges.put(deviceid, build); // 替换旧的
    }


    public void doSend(String deviceId) {  // 客户端断开连接后 开启遗嘱消息发送
        if (StringUtils.isNotBlank(deviceId) && (willMeaasges.get(deviceId)) != null) {
            WillMeaasge willMeaasge = willMeaasges.get(deviceId);
            channelService.sendWillMsg(willMeaasge); // 发送遗嘱消息
            if (!willMeaasge.isRetain()) { // 移除
                willMeaasges.remove(deviceId);
                log.debug("deviceId will message[" + willMeaasge.getWillMessage() + "] is removed");
            }
        }
    }

    /**
     * 删除遗嘱消息
     */
    public void del(String deviceid) {
        willMeaasges.remove(deviceid);
    }
}
