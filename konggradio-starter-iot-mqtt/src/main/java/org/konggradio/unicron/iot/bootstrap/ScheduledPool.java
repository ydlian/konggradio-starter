package org.konggradio.unicron.iot.bootstrap;

import org.konggradio.unicron.iot.pool.Scheduled;
import org.konggradio.unicron.iot.properties.InitBean;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务
 *
 * @author lianyadong
 * @create 2023-12-14 10:39
 **/
@Service
public class ScheduledPool implements Scheduled {

    private final InitBean serverBean;
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(20);

    public ScheduledPool(InitBean serverBean) {
        this.serverBean = serverBean;
    }

    public ScheduledFuture<?> submit(Runnable runnable) {
        int initalDelay = serverBean.getInitalDelay();
        int period = serverBean.getPeriod();

        return scheduledExecutorService.scheduleAtFixedRate(runnable, initalDelay, period, TimeUnit.SECONDS);
    }

//    public static void main(String[] a) throws InterruptedException {
//        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(100);
//        ScheduledFuture<?> schedule = scheduledExecutorService.schedule(new Runnable() {
//            @Override
//            public void run() {
//                System.out.print("123");
//            }
//        }, 2, TimeUnit.SECONDS);
//    }

}
