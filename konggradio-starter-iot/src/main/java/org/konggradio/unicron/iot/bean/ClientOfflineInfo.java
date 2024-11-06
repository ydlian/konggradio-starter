package org.konggradio.unicron.iot.bean;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


import java.io.Serializable;
import java.util.Date;

@Data
@Slf4j
public class ClientOfflineInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6840595808048689417L;
    private String connectionStatus;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date connectDateTime;
    private String ipPort;
    private String ip;
    private String deviceID;
    private String localServerIp;
    private String userName;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date lastLiveTime;
    private String sendStatus;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date offlineTime;
    private String offlineLongSendStatus;
    private String station;

    @JSONField(serialize = false)
    public String getWarnMessage() {
        String meString = null;
        try {
            meString = String.format("设备离线:{连接时间:%s,最后活跃时间%s,IP地址:%s,业务实例地址:%s,厂家编码:%s}",
                    DateUtil.format(this.getConnectDateTime(), "yyyy-MM-dd HH:mm:ss"),
					DateUtil.format(this.getLastLiveTime(), "yyyy-MM-dd HH:mm:ss"), this.getIpPort(),
                    this.getLocalServerIp(), this.getUserName());

        } catch (Exception e) {
            // TODO: handle exception
            log.info(e.getMessage());
        }
        return meString;
    }

    @JSONField(serialize = false)
    public String getRecoverMessage() {
        return "";
    }
}
