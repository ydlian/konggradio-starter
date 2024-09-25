/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioEventConsumer.java
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

package org.konggradio.core.boot.event.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.konggradio.core.boot.event.entity.EventDelayMessage;
import org.konggradio.core.boot.event.handle.KongGradioEventInterface;
import org.konggradio.core.boot.event.handle.impl.KongGradioEventRegisterImpl;
import org.konggradio.core.enums.KongEventEnum;
import org.konggradio.core.tool.log.LogDebuger;
import org.konggradio.core.tool.utils.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @description: 延迟队列消费
 * @author: xxx
 * @create: xxx
 **/
@Component
//@Slf4j
@AllArgsConstructor
public class KongGradioEventConsumer<T> {
	private static final Logger logger = LoggerFactory.getLogger(LogDebuger.class);
	private static ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build().setTimeZone(TimeZone.getTimeZone("GMT+8"));
	;
	@Resource
	private KongGradioEventQueueService delayingQueueService;
	@Autowired
	private KongGradioEventRegisterImpl kongGradioSyncRegister;
	@Resource
	KongGradioEventInterface kongGradioSyncInterface;


	public Boolean doSync(KongEventEnum kongEventEnum, Object data) {
		KongGradioEventInterface kongGradioSyncInterface = kongGradioSyncRegister.getServiceImpl(kongEventEnum.getEventName());
		logger.info("KongCloudEventInterface实现类为：{}", kongEventEnum.getEventName());
		return kongGradioSyncInterface.sync(data);
	}

	/**
	 * 消费队列中的数据
	 * zset会对score进行排序 让最早消费的数据位于最前
	 * 拿最前的数据跟当前时间比较 时间到了则消费
	 */

	public boolean consume(String groupCode, int size, List<T> consumeList, List<EventDelayMessage> out) {
		AtomicLong count = new AtomicLong(0L);
		List<EventDelayMessage> msgList;
		if (size > 0) {
			msgList = delayingQueueService.pull(groupCode, (int) (size * 1.5));
		} else {
			msgList = delayingQueueService.pull(groupCode);
		}
		if (ObjectUtil.isNotEmpty(msgList)) {
			out.addAll(msgList);
		}
		if (null != msgList && !msgList.isEmpty()) {

			for (EventDelayMessage msg : msgList) {
				long current = System.currentTimeMillis();
				if (current >= msg.getDelayTime()) {
					try {
						boolean removeResult = delayingQueueService.remove(msg, groupCode);
						if (removeResult) {
							count.getAndIncrement();
							Object dataBody = msg.getBody();
							consumeList.add((T) dataBody);
							kongGradioSyncInterface.sync(dataBody);
							if (count.get() >= size && size > 0) {
								break;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {

			return false;
		}
		return true;
	}


}
