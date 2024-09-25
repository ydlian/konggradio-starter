/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: YamlConfig.java
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

package org.konggradio.core.launch.config;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class YamlConfig {
	private Map<String, Object> properties = new HashMap<>();

	private static YamlConfig instance;

	public static String getYamlFile() {
		return yamlFile;
	}

	public static void setYamlFile(String yamlFile) {
		YamlConfig.yamlFile = yamlFile;
	}

	private static String yamlFile;

	public static YamlConfig getInstance(String fileName) {
		YamlConfig.setYamlFile(fileName);
		return getInstance();
	}

	public static YamlConfig getInstance() {
		Assert.hasText(yamlFile, "yaml file not found!");
		if (instance == null) {
			synchronized (YamlConfig.class) {
				if (instance == null) {
					instance = new YamlConfig();
				}
			}
		}
		return instance;
	}

	private YamlConfig() {
		try {
			properties = initWithResource(yamlFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 从文件中获取配置的方法
	public Map<String, Object> initWithResource(String yamlFile) {
		//System.out.println("[Init configuration]Loading yaml:"+yamlFile+" ...");
		if(!StringUtils.hasText(yamlFile)){
			return properties;
		}
		InputStream inputStream = this.getClass()
			.getClassLoader()
			.getResourceAsStream(yamlFile);
		Yaml yaml = new Yaml();
		try{
			properties = yaml.load(inputStream);
		}catch (YAMLException e){
			e.printStackTrace();
		}

		return properties;
	}

	public String getString(String param) {
		return String.valueOf(getValueByKey(param,""));
	}

	public String getString(String param, String defaultValue) {
		return String.valueOf(getValueByKey(param,defaultValue));
	}

	public Map<String, Object> getConfig() {
		return properties;
	}

	/**
	 * 从Map中获取配置的值
	 * 传的key支持两种形式, 一种是单独的,如user.path.key
	 * 一种是获取数组中的某一个,如 user.path.key[0]
	 * @param key
	 * @return
	 */
	public <T> T getValueByKey(String key, T defaultValue) {
		String separator = ".";
		String[] separatorKeys = null;
		if (key.contains(separator)) {
			// 取下面配置项的情况, user.path.keys 这种
			separatorKeys = key.split("\\.");
		} else {
			Object res = properties.get(key);
			return res == null ? defaultValue : (T) res;
		}

		String finalValue = null;
		Object tempObject = properties;
		for (int i = 0; i < separatorKeys.length; i++) {
			String innerKey = separatorKeys[i];
			Integer index = null;
			if (innerKey.contains("[")) {
				// 如果是user[0]的形式,则index = 0 , innerKey=user
				index = Integer.valueOf(getSubstringBetween(innerKey, "[", "]"));
				innerKey = innerKey.substring(0, innerKey.indexOf("["));
			}
			Map<String, Object> mapTempObj = (Map) tempObject;
			Object object = mapTempObj.get(innerKey);
			if (object == null) {
				return defaultValue;
			}
			Object targetObj = object;
			if (index != null) {
				// 如果是取的数组中的值,在这里取值
				targetObj = ((ArrayList) object).get(index);
			}
			tempObject = targetObj;
			if (i == separatorKeys.length - 1) {
				//循环结束
				return (T) targetObj;
			}


		}
		return null;
	}

	private String getSubstringBetween(String innerKey, String s1, String s2) {
		int pos1 = innerKey.indexOf(s1);
		int pos2 = innerKey.indexOf(s2);
		Assert.isTrue(pos1 != -1,"Error getSubstringBetween");
		Assert.isTrue(pos2 != -1,"Error getSubstringBetween");
		return innerKey.substring(pos1, pos2);
	}
}
