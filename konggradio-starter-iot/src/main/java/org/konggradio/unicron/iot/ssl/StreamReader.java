package org.konggradio.unicron.iot.ssl;

import java.io.InputStream;

public class StreamReader {


    public String toByteArray(InputStream fin) {
        int i = -1;
        StringBuilder buf = new StringBuilder();
        try {
            while ((i = fin.read()) != -1) {
                if (buf.length() > 0) buf.append(",");
                buf.append("(byte)");
                buf.append(i);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }//add sofalala
            }

        } catch (Throwable e) {
            ;
        }

        return buf.toString();
    }

}
