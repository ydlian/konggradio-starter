package org.konggradio.unicron.iot.bootstrap.coder.client;
import java.io.UnsupportedEncodingException;

import org.konggradio.unicron.iot.exception.PayloadDecodeException;
import org.konggradio.unicron.iot.message.decoder.BaseDecoder;
import org.konggradio.unicron.iot.util.EncodeUtil;
import org.konggradio.unicron.iot.util.ServerDecoderUtil;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ClientDecoder implements BaseDecoder {

	public static int CMD_114=114;


	@Override
	public int getCmdNo(byte[] data) {
		// TODO Auto-generated method stub
		byte code[]=new byte[2];
		System.arraycopy(data, 6, code, 0, 2);
		int cmdNo= EncodeUtil.byteToShort(code);
		return cmdNo;
	}


	public byte[] getBodyData(byte[] data) {
		// TODO Auto-generated method stub
		//return 2+2+1+1+2+dataLen+1;
		int bodyLen=data.length-(2+2+1+1+2+1);
		byte bodyData[]=new byte[bodyLen];
		System.arraycopy(data, 8, bodyData, 0, bodyLen);
		return bodyData;
	}

	@Override
	public boolean checkClientkMsg(byte[] data) {
		// TODO Auto-generated method stub
		//log.info("收到不合法的PUBLISH数据:"+data);
		boolean result=false;
		try{
			result= ServerDecoderUtil.checkClientkMsg(data);
		}catch(PayloadDecodeException e){
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public boolean checkClientkMsg(String data) {
		// TODO Auto-generated method stub
		byte[] decode=null;
		try {
			decode = data.getBytes(CHARSET);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return checkClientkMsg(decode);
	}



}
