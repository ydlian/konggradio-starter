package org.konggradio.unicron.iot.message.decoder;

public interface BaseDecoder {

    public static final String CHARSET = "ISO-8859-1";

    //public abstract byte[] getPayload(T t);
    public abstract int getCmdNo(byte[] data);

    //对客户端传入的数据进行合规检查
    public abstract boolean checkClientkMsg(byte[] data);

    public abstract boolean checkClientkMsg(String data);

    byte[] getBodyData(byte[] data);

}
