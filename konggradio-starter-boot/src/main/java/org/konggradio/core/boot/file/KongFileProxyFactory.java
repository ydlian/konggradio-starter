/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongFileProxyFactory.java
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

import org.konggradio.core.tool.constant.SystemConstant;
import org.konggradio.core.tool.utils.DateUtil;
import org.konggradio.core.tool.utils.ImageUtil;
import org.konggradio.core.tool.utils.StringPool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * 文件代理类
 *
 * @author ydlian
 */
public class KongFileProxyFactory implements IFileProxy {

	@Override
	public File rename(File f, String path) {
		File dest = new File(path);
		f.renameTo(dest);
		return dest;
	}

	@Override
	public String[] path(File f, String dir) {
		//避免网络延迟导致时间不同步
		long time = System.nanoTime();

		StringBuilder uploadPath = new StringBuilder()
			.append(getFileDir(dir, SystemConstant.me().getUploadRealPath()))
			.append(time)
			.append(getFileExt(f.getName()));

		StringBuilder virtualPath = new StringBuilder()
			.append(getFileDir(dir, SystemConstant.me().getUploadCtxPath()))
			.append(time)
			.append(getFileExt(f.getName()));

		return new String[]{KongFileUtil.formatUrl(uploadPath.toString()), KongFileUtil.formatUrl(virtualPath.toString())};
	}

	/**
	 * 获取文件后缀
	 *
	 * @param fileName 文件名
	 * @return 文件后缀
	 */
	public static String getFileExt(String fileName) {
		if (!fileName.contains(StringPool.DOT)) {
			return ".jpg";
		} else {
			return fileName.substring(fileName.lastIndexOf(StringPool.DOT));
		}
	}

	/**
	 * 获取文件保存地址
	 *
	 * @param dir     目录
	 * @param saveDir 保存目录
	 * @return 地址
	 */
	public static String getFileDir(String dir, String saveDir) {
		StringBuilder newFileDir = new StringBuilder();
		newFileDir.append(saveDir)
			.append(File.separator).append(dir).append(File.separator).append(DateUtil.format(new Date(), "yyyyMMdd"))
			.append(File.separator);
		return newFileDir.toString();
	}


	/**
	 * 图片压缩
	 *
	 * @param path 文件地址
	 */
	@Override
	public void compress(String path) {
		try {
			ImageUtil.zoomScale(ImageUtil.readImage(path), new FileOutputStream(new File(path)), null, SystemConstant.me().getCompressScale(), SystemConstant.me().isCompressFlag());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
