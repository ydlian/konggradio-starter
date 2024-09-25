/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioEventProvider.java
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

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.boot.event.entity.EventDelayMessage;
import org.konggradio.core.boot.event.entity.EventRequest;
import org.konggradio.core.enums.EventChannelEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @description: 消息提供者
 * @author: xxx
 * @create: xxx
 **/
@Component
@Slf4j
@AllArgsConstructor
public class KongGradioEventProvider<T> {

	@Resource
	private KongGradioEventQueueService kongGradioEventQueueService;

	public void sendMessage(T messageBody, EventChannelEnum eventChannelEnum) {
		Date date = new Date();
		String groupCode = String.valueOf(GroupCodeGen.genCode(DateUtil.format(date, DatePattern.NORM_DATETIME_FORMAT), 10));
		sendMessage(messageBody, groupCode, eventChannelEnum, date, 0);
	}

	public void sendMessage(T messageBody) {
		Date date = new Date();
		String groupCode = String.valueOf(GroupCodeGen.genCode(DateUtil.format(date, DatePattern.NORM_DATETIME_FORMAT), 10));
		sendMessage(messageBody, groupCode, EventChannelEnum.EVENT_MSG, date, 0);
	}

	public void sendMessage(T messageBody, String groupCode) {
		Date date = new Date();
		sendMessage(messageBody, groupCode, EventChannelEnum.EVENT_MSG, date, 0);
	}

	public void sendMessage(T messageBody, String groupCode, EventChannelEnum eventChannelEnum) {
		if (eventChannelEnum == null) eventChannelEnum = EventChannelEnum.EVENT_LUA_FILE;
		Date date = new Date();
		sendMessage(messageBody, groupCode, eventChannelEnum, date, 0);
	}
	public void sendMethodMessage(String method, String source, Object[] params, String groupCode, EventChannelEnum eventChannelEnum) {
		if (eventChannelEnum == null) eventChannelEnum = EventChannelEnum.EVENT_LUA_FILE;
		Date date = new Date();
		sendMessage(method,source, params, groupCode, eventChannelEnum, date, 0);
	}

	public void sendMessage(T messageBody, long delaySeconds) {
		Date date = new Date();
		String groupCode = String.valueOf(GroupCodeGen.genCode(DateUtil.format(date, DatePattern.NORM_DATETIME_FORMAT), 10));
		sendMessage(messageBody, groupCode, EventChannelEnum.EVENT_MSG, date, delaySeconds);
	}

	public void sendMessage(T messageBody, String groupCode, Date date, long delaySeconds) {
		sendMessage(messageBody, groupCode, EventChannelEnum.EVENT_MSG, date, delaySeconds);
	}


	/**
	 * 发送消息
	 *
	 * @param messageBody
	 */
	public void sendMessage(T messageBody, String groupCode, EventChannelEnum channelEnum, Date date, long delaySeconds) {
		try {
			if (messageBody != null) {
				EventDelayMessage messageContent = new EventDelayMessage<T>();
				messageContent.setBody(messageBody);
				messageContent.setChannel(String.valueOf(channelEnum.getCode()));
				messageContent.setId(UUID.fastUUID().toString());
				messageContent.setCreateTime(date);
				long time = System.currentTimeMillis();
				messageContent.setDelayTime(time + (delaySeconds * 1000));
				kongGradioEventQueueService.push(messageContent, groupCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String method, T messageBody, String groupCode, EventChannelEnum channelEnum, Date date, long delaySeconds) {
		try {
			if (messageBody != null) {
				EventDelayMessage messageContent = new EventDelayMessage<T>();
				messageContent.setBody(messageBody);
				messageContent.setMethod(method);
				messageContent.setChannel(String.valueOf(channelEnum.getCode()));
				messageContent.setId(UUID.fastUUID().toString());
				messageContent.setCreateTime(date);
				long time = System.currentTimeMillis();
				messageContent.setDelayTime(time + (delaySeconds * 1000));
				kongGradioEventQueueService.push(messageContent, groupCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String method, String source, Object[] params, String groupCode, EventChannelEnum channelEnum, Date date, long delaySeconds) {
		try {
			if (source != null) {
				EventDelayMessage messageContent = new EventDelayMessage<T>();
				messageContent.setBody(params);
				messageContent.setParams(params);
				messageContent.setSource(source);
				messageContent.setMethod(method);
				messageContent.setChannel(String.valueOf(channelEnum.getCode()));
				messageContent.setId(UUID.fastUUID().toString());
				messageContent.setCreateTime(date);
				long time = System.currentTimeMillis();
				messageContent.setDelayTime(time + (delaySeconds * 1000));
				kongGradioEventQueueService.push(messageContent, groupCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMethodMessage(EventRequest eventRequest, String groupCode, long delaySeconds) {
		String source = eventRequest.getSource();
		EventChannelEnum eventChannelEnum = EventChannelEnum.getEnumByCode(eventRequest.getCode());
		try {
			if (source != null && eventChannelEnum!=null) {
				EventDelayMessage messageContent = new EventDelayMessage<T>();
				messageContent.setBody(eventRequest.getParams());
				messageContent.setParams(eventRequest.getParams());
				messageContent.setSource(source);
				messageContent.setTaskId(eventRequest.getTaskId());
				messageContent.setMethod(eventRequest.getMethod());
				messageContent.setChannel(String.valueOf(eventChannelEnum.getCode()));
				messageContent.setId(UUID.fastUUID().toString());
				messageContent.setCreateTime(new Date());
				long time = System.currentTimeMillis();
				messageContent.setDelayTime(time + (delaySeconds * 1000));
				kongGradioEventQueueService.push(messageContent, groupCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
