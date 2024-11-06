package org.konggradio.unicron.iot.bean;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ClientConnectionInfo implements Serializable {

	private static final long serialVersionUID = -2751394300700288904L;
	String connectDateTime;
	//IP和端口，ip:port	格式,客户端的IP
	String ipPort;
	String ip;//
	//服务器IP
	String localServerIp;
	Channel channel;
	String deviceID;
	//通信协议版本
	String protocolVer;
	//RSA密钥
	String clientPublicKey;
	String serverPublicKey;
	String serverPrivateKey;
	boolean authCheckResult;
	boolean repeatConnection;
	boolean isOnline;
	//连接的用户名
	String userName;
	//最后一次联系时间
	Date lastLiveTime;
	String stationSoftwareVersion;//
	//边缘设备本地时间，毫秒时间格式
	long stationLocalTime;
	//服务器时间-桩时间偏移量
	long msecondOffset;

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);

	}

}
