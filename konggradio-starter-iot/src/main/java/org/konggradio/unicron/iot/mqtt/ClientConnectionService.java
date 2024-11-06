package org.konggradio.unicron.iot.mqtt;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.konggradio.unicron.iot.bean.ClientConnectionInfo;
import org.konggradio.unicron.iot.bean.ClientOfflineInfo;
import org.konggradio.unicron.iot.constant.RedisConstant;
import org.konggradio.unicron.iot.ip.IpUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ClientConnectionService {

    private final static int MIN_SPAN_MSECONDS = 200;
	private final static String GLB_OFFLINE_KEY = RedisConstant.UNICRON_GLOBAL_OFFLINE_LIST;
    private final static String GLOBLE_CONNECT_LIST_CACHE_KEY = RedisConstant.UNICRON_GLOBAL_CONNECT_LIST;
    private static final String IP_CUT_REG = "(\\/|\\\\){1,}";
    private static final String SEPARATOR = ":";
    protected static ConcurrentHashMap<String, ClientConnectionInfo> onlineChannels = new ConcurrentHashMap<>(); // deviceId
    // ->
    // Channel

    protected static ConcurrentHashMap<String, Long> deviceCacheMap = new ConcurrentHashMap<String, Long>();
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public static ConcurrentHashMap<String, ClientConnectionInfo> getOnlineChannels() {
        return onlineChannels;
    }

    // 建立连接时
    public void printConnectionInfo() {
        String cacheKey = GLOBLE_CONNECT_LIST_CACHE_KEY;
        Map<Object, Object> globalResult = stringRedisTemplate.opsForHash().entries(cacheKey);

        log.info("全局客户端连接数={}，Global={},本地客户端连接数={},本地连接={}", globalResult.size(), JSONObject.toJSON(globalResult),
                onlineChannels.size(), JSONObject.toJSON(onlineChannels));

    }

    public boolean isClientRealOnline(String theDeviceId) {
        if (onlineChannels.get(theDeviceId) != null) {
            return true;
        }
        String cacheKey = GLOBLE_CONNECT_LIST_CACHE_KEY;
        HashOperations<String, String, String> vo = stringRedisTemplate.opsForHash();
        if (vo.get(cacheKey, theDeviceId) != null) {
            return true;
        }
        return false;
    }

    public boolean isClientThisNodeOnline(String theDeviceId) {
        if (onlineChannels.get(theDeviceId) != null) {
            return true;
        }

        return false;
    }


    public boolean checkDeviceIfLocalNodeExist(Channel channel, String checkDeviceId) {

        boolean result = false;
        ClientConnectionInfo client = onlineChannels.get(checkDeviceId);
        if (client != null) {
            return true;
        }
        log.info("[通道重复检查]重复状态：{},{},{}", result, checkDeviceId, JSON.toJSONString(onlineChannels));
        return result;
    }

    private String getRemoteAddr(ChannelHandlerContext ctx) {
        String remoteAddr = ctx.channel().remoteAddress().toString();
        remoteAddr = remoteAddr.replaceAll(IP_CUT_REG, "");
        return remoteAddr;
    }

    public ClientConnectionInfo getThisNodeConnectInfo(String theDeviceId) {
        ClientConnectionInfo con = onlineChannels.get(theDeviceId);
        return con;
    }

    public ClientConnectionInfo getGlobleConnectInfo(String theDeviceId) {
        String cacheKey = GLOBLE_CONNECT_LIST_CACHE_KEY;
        HashOperations<String, String, String> vo = stringRedisTemplate.opsForHash();
        String data = vo.get(cacheKey, theDeviceId);
        ClientConnectionInfo client = null;
        try {
            client = JSON.parseObject(data, ClientConnectionInfo.class);
        } catch (Exception e) {
            client = null;
            e.printStackTrace();
        }

        return client;
    }

    public ClientConnectionInfo getClientConnectionInfoByRemoteIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return null;
        }
        log.info("RemoteIp:" + ip);
        try {
            Set<String> keys = onlineChannels.keySet();
            for (String key : keys) {
                ClientConnectionInfo clientConnectionInfo = onlineChannels.get(key);
                if (clientConnectionInfo != null && ip.equals(clientConnectionInfo.getIp())) {
                    return clientConnectionInfo;
                }
            }
            String cacheKey = GLOBLE_CONNECT_LIST_CACHE_KEY;
            HashOperations<String, String, String> vo = stringRedisTemplate.opsForHash();
            Map<String, String> map = vo.entries(cacheKey);
            for (String string : map.keySet()) {
                String data = map.get(string);
                ClientConnectionInfo clientConnectionInfo = JSON.parseObject(data, ClientConnectionInfo.class);
                if (clientConnectionInfo != null && ip.equals(clientConnectionInfo.getIp())) {
                    return clientConnectionInfo;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            log.info(e.getMessage());
        }

        return null;
    }

    private void forceWriteRedis(HashOperations<String, String, String> vo, String key, String data) {

    }

    // 建立连接时
    public ClientConnectionInfo creatConnection(ClientConnectionInfo inputInfo, Channel channel, String theDeviceId) {
        String remoteAddr = channel.remoteAddress().toString();
        remoteAddr = remoteAddr.replaceAll(IP_CUT_REG, "");
        // String channelKey=remoteAddr;

        ClientConnectionInfo client = new ClientConnectionInfo();
        client.setChannel(channel);
        String connectDateTime = DateUtil.now();
        client.setConnectDateTime(connectDateTime);
        client.setDeviceID(theDeviceId);
        client.setIpPort(remoteAddr);
        if (remoteAddr.indexOf(":") > 0) {
            client.setIp(remoteAddr.split(":")[0]);
        } else {
            client.setIp(remoteAddr);
        }
        String localIp = IpUtils.getHost();
        client.setLocalServerIp(localIp);

        client.setAuthCheckResult(inputInfo.isAuthCheckResult());
        client.setClientPublicKey(inputInfo.getClientPublicKey());
        client.setServerPublicKey(inputInfo.getServerPublicKey());
        client.setServerPrivateKey(inputInfo.getServerPrivateKey());
        client.setUserName(inputInfo.getUserName());
        client.setLastLiveTime(DateUtil.date());
        if (inputInfo.isOnline()) {
            client.setOnline(true);
        }

        String ip = IpUtils.getHost();
        return client;
    }

    public void creatConnection4Offline(ClientConnectionInfo inputInfo, Channel channel, String theDeviceId) {

        if (inputInfo == null || StringUtils.isBlank(theDeviceId)) {
            return;
        }

        ClientOfflineInfo clientOfflineInfo = null;
        String deviceMessage = (String) stringRedisTemplate.opsForHash().get(GLB_OFFLINE_KEY, theDeviceId);
        Date dateNow = DateUtil.date();
        if (StringUtils.isNotBlank(deviceMessage)) {
            log.info("get create Redis Message success: {}", deviceMessage);
            clientOfflineInfo = JSON.parseObject(deviceMessage, ClientOfflineInfo.class);
            if (clientOfflineInfo == null) {
                return;
            }
            clientOfflineInfo.setConnectDateTime(dateNow);
            clientOfflineInfo.setLastLiveTime(dateNow);

            if ("离线".equals(clientOfflineInfo.getConnectionStatus())) {

                if ("未发送".equals(clientOfflineInfo.getSendStatus())) {
                    // 未发送的告警信息，恢复在线后不在发送
                    clientOfflineInfo.setSendStatus("已发送");
                } else {
                    clientOfflineInfo.setSendStatus("未发送");
                }
            } else {
                clientOfflineInfo.setSendStatus("已发送");
            }
            clientOfflineInfo.setConnectionStatus("在线");
        } else {
            log.info("get create Redis Message fail,new client: {}", deviceMessage);
            clientOfflineInfo = new ClientOfflineInfo();
            clientOfflineInfo.setConnectDateTime(dateNow);
            clientOfflineInfo.setIp(inputInfo.getIp());
            clientOfflineInfo.setIpPort(inputInfo.getIpPort());
            clientOfflineInfo.setDeviceID(inputInfo.getDeviceID());
            clientOfflineInfo.setLastLiveTime(dateNow);
            clientOfflineInfo.setLocalServerIp(inputInfo.getLocalServerIp());
            clientOfflineInfo.setConnectionStatus("在线");
            clientOfflineInfo.setUserName(inputInfo.getUserName());
            clientOfflineInfo.setSendStatus("已发送");
        }
        clientOfflineInfo.setOfflineLongSendStatus("已发送");
        String jsonString = JSON.toJSONString(clientOfflineInfo, SerializerFeature.IgnoreErrorGetter);
        stringRedisTemplate.opsForHash().put(GLB_OFFLINE_KEY, theDeviceId, jsonString);
        log.info("create Redis {} ,info:{}", GLB_OFFLINE_KEY, jsonString);

        if (StringUtils.isBlank((String) stringRedisTemplate.opsForHash().get(GLB_OFFLINE_KEY, theDeviceId))) {
            log.info("get create Redis message {} error ,wirte again", jsonString);
            stringRedisTemplate.opsForHash().put(GLB_OFFLINE_KEY, theDeviceId, jsonString);
        }

    }

    public void updateOfflineConnection(String theDeviceId) {

        ClientConnectionInfo con = onlineChannels.get(theDeviceId);

        ClientOfflineInfo clientOfflineInfo = null;
        String deviceMessage = (String) stringRedisTemplate.opsForHash().get(GLB_OFFLINE_KEY, theDeviceId);
        Date dateNow = DateUtil.date();
        if (StringUtils.isNotBlank(deviceMessage)) {
            log.info("get update Redis Message success: {}", deviceMessage);
            clientOfflineInfo = JSON.parseObject(deviceMessage, ClientOfflineInfo.class);
            if (clientOfflineInfo == null) {
                return;
            }

        } else {
            log.info("get update Redis Message fail,client create redis fail: {}", theDeviceId);
            clientOfflineInfo = new ClientOfflineInfo();
            clientOfflineInfo.setConnectDateTime(dateNow);
            clientOfflineInfo.setIp(con.getIp());
            clientOfflineInfo.setIpPort(con.getIpPort());
            clientOfflineInfo.setDeviceID(con.getDeviceID());
            clientOfflineInfo.setLastLiveTime(dateNow);
            clientOfflineInfo.setLocalServerIp(con.getLocalServerIp());
            clientOfflineInfo.setUserName(con.getUserName());
            clientOfflineInfo.setSendStatus("已发送");
        }

        clientOfflineInfo.setOfflineLongSendStatus("已发送");
        clientOfflineInfo.setConnectionStatus("在线");
        clientOfflineInfo.setLastLiveTime(dateNow);

        String offlineInfo = JSON.toJSONString(clientOfflineInfo, SerializerFeature.IgnoreErrorGetter);
        stringRedisTemplate.opsForHash().put(GLB_OFFLINE_KEY, theDeviceId, offlineInfo);
        log.info("update Redis {} ,info:{}", GLB_OFFLINE_KEY, offlineInfo);
    }

    // 更新连接时
    public void updateConnection(Channel channel, String theDeviceId) {
        ClientConnectionInfo hisConnect = onlineChannels.get(theDeviceId);
        log.info("updateConnection:{}", theDeviceId);
        String ip = IpUtils.getHost();

        ClientConnectionInfo newConnection = new ClientConnectionInfo();
        if (hisConnect != null && hisConnect.getChannel() != null) {
            newConnection = hisConnect;
            newConnection.setLocalServerIp(IpUtils.getHost());
            newConnection.setLastLiveTime(DateUtil.date());
            newConnection.setOnline(true);
            newConnection.setAuthCheckResult(true);
        } else {

            newConnection.setChannel(channel);
            newConnection.setDeviceID(theDeviceId);
            newConnection.setLocalServerIp(IpUtils.getHost());
            newConnection.setLastLiveTime(DateUtil.date());
            newConnection.setOnline(true);
            newConnection.setAuthCheckResult(true);
            onlineChannels.put(theDeviceId, newConnection);
        }
        HashOperations<String, String, String> vo = stringRedisTemplate.opsForHash();
        String newVal = newConnection.toString();
        String oldVal = vo.get(GLOBLE_CONNECT_LIST_CACHE_KEY, theDeviceId);
        // newConnection.setConnectDateTime(DateUtils.getCurLongDateStr());

        updateOfflineConnection(theDeviceId);
    }

    public boolean distroyConnection(String deviceId) {
        if (deviceId == null) {
            return true;
        }
        String ip = IpUtils.getHost();
        Long lasttime = deviceCacheMap.get(ip + SEPARATOR + deviceId);
        return removeBadConnection(deviceId);
    }

    public boolean removeBadConnection(String deviceId) {

        HashOperations<String, String, String> vo = stringRedisTemplate.opsForHash();
        // HashOperations<String, String, String> vo = stringRedisTemplate.opsForHash();
        String val = vo.get(GLOBLE_CONNECT_LIST_CACHE_KEY, deviceId);
        if (StringUtil.isNullOrEmpty(deviceId)) {
            return false;
        }
               return true;
    }

}
