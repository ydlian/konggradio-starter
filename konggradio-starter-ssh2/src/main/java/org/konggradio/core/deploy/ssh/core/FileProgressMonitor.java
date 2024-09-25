/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: FileProgressMonitor.java
 *  <p>
 *  Licensed under the Apache License Version 2.0;
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package org.konggradio.core.deploy.ssh.core;

import com.jcraft.jsch.SftpProgressMonitor;

import org.konggradio.core.deploy.framework.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class FileProgressMonitor extends TimerTask implements SftpProgressMonitor {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(FileProgressMonitor.class);

	private long progressInterval = 5 * 1000; // 默认间隔时间为5秒

    private boolean isEnd = false; // 记录传输是否结束

    private long transfered; // 记录已传输的数据总大小

    private long fileSize; // 记录文件总大小

    private Timer timer; // 定时器对象

    private boolean isScheduled = false; // 记录是否已启动timer记时器

    private String src;
    private String dest;

    public FileProgressMonitor(long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public void run() {
        if (!isEnd()) { // 判断传输是否已结束
            long transferred = getTransfered();
            if (transferred != fileSize) { // 判断当前已传输数据大小是否等于文件总大小

                logger.info("uploading: " + IOUtil.bytesToMB(transferred) + " MB");
                sendProgressMessage(transferred);
            } else {
                setEnd(true); // 如果当前已传输数据大小等于文件总大小，说明已完成，设置end
            }
        } else {
        	logger.info("Transferring done. Cancel timer.");
            stop(); // 如果传输结束，停止timer记时器
            return;
        }
    }

    public void stop() {
    	logger.debug("Try to stop progress monitor.");
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
            isScheduled = false;
        }
        logger.debug("Progress monitor stoped.");
    }

    public void start() {
//        logger.info("准备传输...");
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(this, 1000, progressInterval);
        isScheduled = true;
        logger.info("start upload:{} ...",src);
    }

    /**
     * 打印progress信息
     * @param transfered
     */
    private void sendProgressMessage(long transfered) {
        if (fileSize != 0) {
            double d = ((double)transfered * 100)/(double)fileSize;
            DecimalFormat df = new DecimalFormat( "#.##");
            logger.info("Transferring: " + df.format(d) + "%");
        } else {
        	logger.info("文件大小为0，忽略传输进度！");
        }
    }

    /**
     * 实现了SftpProgressMonitor接口的count方法
     */
    public boolean count(long count) {
        if (isEnd()) return false;
        if (!isScheduled) {
            start();
        }
        add(count);
        return true;
    }

    /**
     * 实现了SftpProgressMonitor接口的end方法
     */
    public void end() {
        setEnd(true);
        logger.info("File: {} transfer done.", dest);
    }

    private synchronized void add(long count) {
        transfered = transfered + count;
    }

    private synchronized long getTransfered() {
        return transfered;
    }

    public synchronized void setTransfered(long transfered) {
        this.transfered = transfered;
    }

    private synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    private synchronized boolean isEnd() {
        return isEnd;
    }

    public void init(int op, String src, String dest, long max) {
    	this.src = src;
    	this.dest = dest;
    	logger.info("传输文件初始化：源文件{},目标文件{}",src,dest);
    }
}
