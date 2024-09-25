/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: IFileProxy.java
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
 * 文件代理接口
 *
 * @author ydlian
 */
public interface IFileProxy {

	/**
	 * 返回路径[物理路径][虚拟路径]
	 *
	 * @param file 文件
	 * @param dir  目录
	 * @return String
	 */
	String[] path(File file, String dir);

	/**
	 * 文件重命名策略
	 *
	 * @param file 文件
	 * @param path 路径
	 * @return File
	 */
	File rename(File file, String path);

	/**
	 * 图片压缩
	 *
	 * @param path 路径
	 */
	void compress(String path);

}
