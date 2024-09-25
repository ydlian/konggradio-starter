/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioEventQueueService.java
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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.boot.event.entity.EventDelayMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @description: 延时队列功能类
 * @author: xxx
 * @create: xxx
 **/
@Component
@Slf4j
@AllArgsConstructor
public class KongGradioEventQueueService {

	private static ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build().setTimeZone(TimeZone.getTimeZone("GMT+8"));
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	/**
	 * 可以不同业务用不同的key
	 */
	public static final String QUEUE_NAME = "Kongradio:event:message:queue:";


	/**
	 * 插入消息
	 *
	 * @param message
	 * @return
	 */
	@SneakyThrows
	public Boolean push(EventDelayMessage message, String groupCode) {
		String key = QUEUE_NAME + groupCode;
		Boolean addFlag = stringRedisTemplate.opsForZSet().add(key, mapper.writeValueAsString(message), message.getDelayTime());
		stringRedisTemplate.expire(key, 3600 * 24 * 365, TimeUnit.SECONDS);
		return addFlag;
	}

	/**
	 * 移除消息
	 *
	 * @param message
	 * @return
	 */
	@SneakyThrows
	public Boolean remove(EventDelayMessage message, String groupCode) {
		Long remove = stringRedisTemplate.opsForZSet().remove(QUEUE_NAME + groupCode, mapper.writeValueAsString(message));
		return remove > 0 ? true : false;
	}


	/**
	 * 拉取最新需要
	 * 被消费的消息
	 * rangeByScore 根据score范围获取 0-当前时间戳可以拉取当前时间及以前的需要被消费的消息
	 *
	 * @return
	 */
	public List<EventDelayMessage> pull(String groupCode) {
		Set<String> strings = stringRedisTemplate.opsForZSet().rangeByScore(QUEUE_NAME + groupCode, 0, System.currentTimeMillis());
		if (strings == null) {
			return null;
		}
		List<EventDelayMessage> msgList = strings.stream().map(msg -> {
			EventDelayMessage message = null;
			try {
				message = mapper.readValue(msg, EventDelayMessage.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return message;
		}).collect(Collectors.toList());
		return msgList;
	}

	/**
	 * 拉取最新需要
	 * 被消费的消息
	 * rangeByScore 根据score范围获取 0-当前时间戳可以拉取当前时间及以前的需要被消费的消息
	 *
	 * @return
	 */
	public List<EventDelayMessage> pull(String groupCode, int size) {
		Set<String> strings = stringRedisTemplate.opsForZSet().rangeByScore(QUEUE_NAME + groupCode, 0, System.currentTimeMillis(), 0, size);
		if (strings == null) {
			return null;
		}
		List<EventDelayMessage> msgList = strings.stream().map(msg -> {
			EventDelayMessage message = null;
			try {
				message = mapper.readValue(msg, EventDelayMessage.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return message;
		}).collect(Collectors.toList());
		return msgList;
	}

}
