package org.konggradio.unicron.iot.bootstrap.scan;

import org.konggradio.unicron.iot.bootstrap.Bean.SendMqttMessage;
import org.konggradio.unicron.iot.bootstrap.MqttApi;


import lombok.extern.slf4j.Slf4j;
import org.konggradio.unicron.iot.enums.ConfirmStatus;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 扫描未确认信息
 *
 * @author StormRiver
 * @create 2018-01-06 16:50
 **/

@Slf4j
public abstract class ScanRunnable  extends MqttApi implements Runnable {



    private ConcurrentLinkedQueue<SendMqttMessage> queue  = new ConcurrentLinkedQueue<>();

    public  boolean addQueue(SendMqttMessage t){
        return queue.add(t);
    }

    public  boolean addQueues(List<SendMqttMessage> ts){
        return queue.addAll(ts);
    }



    @Override
    public void run() {
        if(!queue.isEmpty()) {
            SendMqttMessage poll;
            List<SendMqttMessage> list = new LinkedList<>();
            for (; (poll = queue.poll()) != null; ) {
                if (poll.getConfirmStatus() != ConfirmStatus.COMPLETE) {
                    list.add(poll);
                    doInfo(poll);
                }
                break;
            }
            addQueues(list);
        }
    }
    public  abstract  void  doInfo( SendMqttMessage poll);


}
