/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: LuaScriptService.java
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

package org.konggradio.core.boot.service;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Service
@Slf4j
public class LuaScriptService {
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private ResourceLoader resourceLoader;

	public String executeLuaScriptFromFile(String luaPath,Object[] args) {
		//System.out.println(luaPath);
		//String luaPath = "classpath:myscript.lua";
		if (StringUtil.isBlank(luaPath)) {
			//luaPath = "classpath:\\script\\myscript.lua";
			return "";
		} else {
			if (!luaPath.startsWith("classpath:")) {
				luaPath = "classpath:\\script\\" + luaPath;
			}
		}

		Resource resource = resourceLoader.getResource(luaPath);
		String luaScript = null;
		try {
			// 读取文件内容
			InputStream inputStream = resource.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append(System.lineSeparator());
			}
			inputStream.close();
			luaScript = stringBuilder.toString();
			if (StringUtil.isBlank(luaScript)) {
				return "";
			}
		} catch (Exception e) {
			log.error("Unable to read Lua script file.");
			//throw new RuntimeException("Unable to read Lua script file.");
		}

		DefaultRedisScript<String> script = new DefaultRedisScript<>(luaScript, String.class);
		script.setResultType(String.class);
		String result = stringRedisTemplate.execute(script, Collections.emptyList(), args);
		System.out.println(result);
		return result;
	}

	public String executeLuaScriptFromString(String luaScript,Object[] args) {
		DefaultRedisScript<String> script = new DefaultRedisScript<>(luaScript, String.class);
		script.setResultType(String.class);
		String result = stringRedisTemplate.execute(script, Collections.emptyList(), args);
		System.out.println(result);
		return result;
	}

	public String executeLuaScriptFromRemoteFile(String url, Object[] args) {
		String programScript = HttpUtil.downloadString(url, Charset.defaultCharset());
		return executeLuaScriptFromString(programScript,args);
	}
}
