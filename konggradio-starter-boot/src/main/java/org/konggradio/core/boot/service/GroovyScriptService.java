/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: GroovyScriptService.java
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
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class GroovyScriptService {

	@Autowired
	private ResourceLoader resourceLoader;

	public String executeScriptFromFile(String method, String scriptPath, Object[] args) {
		//System.out.println(scriptPath);
		if (StringUtil.isBlank(scriptPath)) {
			//luaPath = "classpath:\\script\\myscript.lua";
			return "";
		} else {
			if (!scriptPath.startsWith("classpath:")) {
				scriptPath = "classpath:\\script\\" + scriptPath;
			}
		}
		Resource resource = resourceLoader.getResource(scriptPath);
		String programScript = null;
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
			programScript = stringBuilder.toString();
			if (StringUtil.isBlank(programScript)) {
				return "";
			}
		} catch (Exception e) {
			log.error("Unable to read Lua script file.");
			//throw new RuntimeException("Unable to read Lua script file.");
		}
		return executeScriptFromString(method, programScript, args);
	}

	public String executeScriptFromString(String method, String programScript, Object[] args) {
		if (args == null || args.length < 1 || StringUtil.isBlank(method)) {
			return "";
		}
		GroovyShell groovyShell = new GroovyShell();
		Script script = groovyShell.parse(programScript);
		//System.out.println(programScript);
		//Object[] paramArray = Arrays.copyOfRange(args, 1, args.length);
		Object result = script.invokeMethod(method, args);
		if (result != null) System.out.println(result);
		return String.valueOf(result);
	}

	public String executeScriptFromRemoteFile(String method, String url, Object[] args) {
		if (args == null || args.length < 1) {
			return "";
		}
		String programScript = HttpUtil.downloadString(url, Charset.defaultCharset());
		return executeScriptFromString(method, programScript, args);
	}
}
