/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: EventConsumerRunner.java
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

package org.konggradio.core.boot.lanucher;

import cn.hutool.http.HttpUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import org.konggradio.core.boot.event.entity.EventDelayMessage;
import org.konggradio.core.boot.event.entity.EventResponse;
import org.konggradio.core.boot.event.queue.KongGradioEventConsumer;
import org.konggradio.core.boot.service.GroovyScriptService;
import org.konggradio.core.boot.service.LuaScriptService;
import org.konggradio.core.constants.RegisterConstants;
import org.konggradio.core.enums.EventChannelEnum;
import org.konggradio.core.enums.EventGroupCodeEnum;
import org.konggradio.core.tool.log.LogDebuger;
import org.konggradio.core.tool.utils.GsonUtils;
import org.konggradio.core.tool.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
//@Slf4j
public class EventConsumerRunner implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(LogDebuger.class);
	ExecutorService executor = Executors.newFixedThreadPool(10);
	@Autowired
	private KongGradioEventConsumer kongGradioEventConsumer;

	@Autowired
	LuaScriptService luaScriptService;

	@Autowired
	GroovyScriptService groovyScriptService;

	@Autowired
	RedisUtil redisUtil;

	@Value("${spring.application.consume.batch.size:100}")
	private Integer msgConsumeSize;

	@Override
	public void run(String... args) throws Exception {
		//log.info("==========EventConsumerRunner============");
		executor.execute(new Runnable() {
			@SneakyThrows
			public void run() {
				while (true) {
					consumeEventData();
					Thread.sleep(50);
				}
			}
		});
	}

	public void consumeEventData() {
		List<Object> listConsume = Lists.newArrayList();
		List<EventDelayMessage> eventDelayMessageList = Lists.newArrayList();
		String groupCode = String.valueOf(EventGroupCodeEnum.SCRIPT_EVENT.getCode());
		try {
			kongGradioEventConsumer.consume(groupCode, msgConsumeSize, listConsume, eventDelayMessageList);
			doConsumeEventData(groupCode, eventDelayMessageList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doConsumeEventData(String groupCode, List<EventDelayMessage> eventDelayMessageList) {
		for (EventDelayMessage eventDelayMessage : eventDelayMessageList) {
			logger.info("eventDelayMessage:{}", GsonUtils.getGson().toJson(eventDelayMessage));
			Object dataBody = eventDelayMessage.getBody();
			String taskId = eventDelayMessage.getTaskId();
			String scriptFile = eventDelayMessage.getSource();
			Object[] args = eventDelayMessage.getParams();
			String method = eventDelayMessage.getMethod();
			int code = Integer.parseInt(eventDelayMessage.getChannel());
			String result = "";
			if (code == EventChannelEnum.EVENT_LUA_FILE.getCode()) {
				result = luaScriptService.executeLuaScriptFromFile(scriptFile, args);
			} else if (code == EventChannelEnum.EVENT_LUA_STRING.getCode()) {
				result = luaScriptService.executeLuaScriptFromString(scriptFile, args);
			} else if (code == EventChannelEnum.EVENT_LUA_REMOTE_FILE.getCode()) {
				String cmd = HttpUtil.downloadString(scriptFile, Charset.defaultCharset());
				result = luaScriptService.executeLuaScriptFromString(cmd, args);
			} else if (code == EventChannelEnum.EVENT_GROOVY_FILE.getCode()) {
				result = groovyScriptService.executeScriptFromFile(method, scriptFile, args);
			} else if (code == EventChannelEnum.EVENT_GROOVY_REMOTE_FILE.getCode()) {
				String cmd = HttpUtil.downloadString(scriptFile, Charset.defaultCharset());
				result = groovyScriptService.executeScriptFromString(method, cmd, args);
			}else if (code == EventChannelEnum.EVENT_WRITE_FILE.getCode()) {
				String cmd = HttpUtil.downloadString(scriptFile, Charset.defaultCharset());
				result = groovyScriptService.executeScriptFromString(method, cmd, args);
			}else if (code == EventChannelEnum.EVENT_READ_FILE.getCode()) {
				String cmd = HttpUtil.downloadString(scriptFile, Charset.defaultCharset());
				result = groovyScriptService.executeScriptFromString(method, cmd, args);
			}else if (code == EventChannelEnum.EVENT_READ_PROPERTY.getCode()) {
				String cmd = HttpUtil.downloadString(scriptFile, Charset.defaultCharset());
				result = groovyScriptService.executeScriptFromString(method, cmd, args);
			}else if (code == EventChannelEnum.EVENT_WRITE_PROPERTY.getCode()) {
				String cmd = HttpUtil.downloadString(scriptFile, Charset.defaultCharset());
				result = groovyScriptService.executeScriptFromString(method, cmd, args);
			}
			EventResponse eventResponse = new EventResponse();
			eventResponse.setSource(eventDelayMessage.getSource());
			eventResponse.setTaskId(eventDelayMessage.getTaskId());
			Map<String, Object> map = Maps.newHashMap();
			map.put(taskId, result);
			eventResponse.setResult(map);
			redisUtil.set(RegisterConstants.EVENT_TASK_KEY + taskId, GsonUtils.getGson().toJson(eventResponse),3600L*24*30*3, TimeUnit.SECONDS);
			logger.info("{}", result);
		}
	}

}
