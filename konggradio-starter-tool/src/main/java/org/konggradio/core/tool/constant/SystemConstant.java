/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: SystemConstant.java
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
package org.konggradio.core.tool.constant;


import lombok.Data;


@Data
public class SystemConstant {

	public static final Integer LOAD_INSTANCE_MIN_SIZE = 2;
	public static final String COLON = ":";
	/**
	 * 开发模式
	 */
	private boolean devMode = false;

	/**
	 * 远程上传模式
	 */
	private boolean remoteMode = false;

	/**
	 * 外网地址
	 */
	private String domain = "http://localhost:8888";

	/**
	 * 上传下载路径(物理路径)
	 */
	private String remotePath = System.getProperty("user.dir") + "/target/";

	/**
	 * 上传路径(相对路径)
	 */
	private String uploadPath = "/upload";

	/**
	 * 下载路径
	 */
	private String downloadPath = "/download";

	/**
	 * 图片压缩
	 */
	private boolean compress = false;

	/**
	 * 图片压缩比例
	 */
	private Double compressScale = 2.00;

	/**
	 * 图片缩放选择:true放大;false缩小
	 */
	private boolean compressFlag = false;

	/**
	 * 项目物理路径
	 */
	private String realPath = System.getProperty("user.dir");

	/**
	 * 项目相对路径
	 */
	private String contextPath = "/";

	private static final SystemConstant ME = new SystemConstant();

	private SystemConstant() {

	}

	public static SystemConstant me() {
		return ME;
	}

	public String getUploadRealPath() {
		return (remoteMode ? remotePath : realPath) + uploadPath;
	}

	public String getUploadCtxPath() {
		return contextPath + uploadPath;
	}

}
