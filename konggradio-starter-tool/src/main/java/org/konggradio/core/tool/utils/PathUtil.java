/*
 * Copyright (c) 2018-2024.
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
package org.konggradio.core.tool.utils;

import org.springframework.lang.Nullable;

import java.io.File;
import java.net.URL;

/**
 * 用来获取各种目录
 *
 * @author ydlian
 */
public class PathUtil {
	public static final String FILE_PROTOCOL = "file";
	public static final String JAR_PROTOCOL = "jar";
	public static final String ZIP_PROTOCOL = "zip";
	public static final String FILE_PROTOCOL_PREFIX = "file:";
	public static final String JAR_FILE_SEPARATOR = "!/";

	/**
	 * 获取jar包运行时的当前目录
	 *
	 * @return {String}
	 */
	@Nullable
	public static String getJarPath() {
		try {
			URL url = PathUtil.class.getResource("/").toURI().toURL();
			return PathUtil.toFilePath(url);
		} catch (Exception e) {
			String path = PathUtil.class.getResource("").getPath();
			return new File(path).getParentFile().getParentFile().getAbsolutePath();
		}
	}

	@Nullable
	public static String toFilePath(@Nullable URL url) {
		if (url == null) {
			return null;
		}
		String protocol = url.getProtocol();
		String file = UrlUtil.decodeURL(url.getPath(), Charsets.UTF_8);
		if (FILE_PROTOCOL.equals(protocol)) {
			return new File(file).getParentFile().getParentFile().getAbsolutePath();
		} else if (JAR_PROTOCOL.equals(protocol) || ZIP_PROTOCOL.equals(protocol)) {
			int ipos = file.indexOf(JAR_FILE_SEPARATOR);
			if (ipos > 0) {
				file = file.substring(0, ipos);
			}
			if (file.startsWith(FILE_PROTOCOL_PREFIX)) {
				file = file.substring(FILE_PROTOCOL_PREFIX.length());
			}
			return new File(file).getParentFile().getAbsolutePath();
		}
		return file;
	}

}
