package org.konggradio.unicron.iot.util;

import java.util.concurrent.atomic.AtomicLong;


public class Count {
    private static AtomicLong counter = new AtomicLong(System.currentTimeMillis());

    public static short getShortCount() {
        short num = (short) (counter.getAndIncrement() % Short.MAX_VALUE);
        return num;
    }

    public static int getIntCount() {
        return (int) (counter.getAndIncrement() % Integer.MAX_VALUE);
    }

    public static long getCount() {
        return counter.getAndIncrement();
    }


}
