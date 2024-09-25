/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: PathUtil.java
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

package org.konggradio.core.deploy.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class PathUtil {
	/**
	* Logger for this class
	*/
	private static final Logger logger = LoggerFactory.getLogger(PathUtil.class);

	/**
	 * 获取当前jar/war包所在路径
	 * 用于从相对路径查找配置文件等
	 * @author ops@KongGradio.com
	 * @date   2016年1月5日
	 */
	public static String getJarPath() {
		File file = new File("nonexist").getAbsoluteFile();
		try {
			return file.getParentFile().getCanonicalPath();
		} catch (IOException e) {
			logger.error("jar路径获取失败!",e);
		}
		throw new NullPointerException("jar路径获取失败!");
	}

	public static void main(String[] args) {
		System.out.println(getJarPath());
//	    com.jfinal.plugin.activerecord.dialect.Sqlite3Dialect
//		System.out.println(getClassPath(Unit.class));
	}
}
