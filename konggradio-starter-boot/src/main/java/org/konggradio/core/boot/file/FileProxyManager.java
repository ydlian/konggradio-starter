/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: FileProxyManager.java
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
package org.konggradio.core.boot.file;

import java.io.File;

/**
 * 文件管理类
 *
 * @author ydlian
 */
public class FileProxyManager {
	private IFileProxy defaultFileProxyFactory = new KongFileProxyFactory();

	private static FileProxyManager me = new FileProxyManager();

	public static FileProxyManager me() {
		return me;
	}

	public IFileProxy getDefaultFileProxyFactory() {
		return defaultFileProxyFactory;
	}

	public void setDefaultFileProxyFactory(IFileProxy defaultFileProxyFactory) {
		this.defaultFileProxyFactory = defaultFileProxyFactory;
	}

	public String[] path(File file, String dir) {
		return defaultFileProxyFactory.path(file, dir);
	}

	public File rename(File file, String path) {
		return defaultFileProxyFactory.rename(file, path);
	}

}
