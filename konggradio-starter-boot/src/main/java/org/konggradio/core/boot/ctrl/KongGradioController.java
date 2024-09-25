/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioController.java
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
package org.konggradio.core.boot.ctrl;

import org.konggradio.core.boot.file.KongFileUtil;
import org.konggradio.core.boot.file.WrappedFile;
import org.konggradio.core.constant.AppConstant;
import org.konggradio.core.secure.KongGradioUser;
import org.konggradio.core.secure.utils.SecureUtil;
import org.konggradio.core.swagger.SwaggerProperties;
import org.konggradio.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public class KongGradioController {



	@Autowired
	private HttpServletRequest request;


	public HttpServletRequest getRequest() {
		return this.request;
	}

	public KongGradioUser getUser() {
		return SecureUtil.getUser();
	}

	public <T> R<T> data(T data) {
		return R.data(data);
	}

	/**
	 * 返回ApiResult
	 *
	 * @param data 数据
	 * @param msg  消息
	 * @param <T>  T 泛型标记
	 * @return R
	 */
	public <T> R<T> data(T data, String msg) {
		return R.data(data, msg);
	}

	/**
	 * 返回ApiResult
	 *
	 * @param data 数据
	 * @param msg  消息
	 * @param code 状态码
	 * @param <T>  T 泛型标记
	 * @return R
	 */
	public <T> R<T> data(T data, String msg, int code) {
		return R.data(code, data, msg);
	}

	/**
	 * 返回ApiResult
	 *
	 * @param msg 消息
	 * @return R
	 */
	public R success(String msg) {
		return R.success(msg);
	}

	/**
	 * 返回ApiResult
	 *
	 * @param msg 消息
	 * @return R
	 */
	public R fail(String msg) {
		return R.fail(msg);
	}

	/**
	 * 返回ApiResult
	 *
	 * @param flag 是否成功
	 * @return R
	 */
	public R status(boolean flag) {
		return R.status(flag);
	}


	public WrappedFile getFile(MultipartFile file) {
		return KongFileUtil.getFile(file);
	}

	/**
	 * 获取GradioFile.封装类
	 *
	 * @param file 文件
	 * @param dir  目录
	 * @return GradioFile.
	 */
	public WrappedFile getFile(MultipartFile file, String dir) {
		return KongFileUtil.getFile(file, dir);
	}

	/**
	 * 获取GradioFile.封装类
	 *
	 * @param file        文件
	 * @param dir         目录
	 * @param path        路径
	 * @param virtualPath 虚拟路径
	 * @return GradioFile.
	 */
	public WrappedFile getFile(MultipartFile file, String dir, String path, String virtualPath) {
		return KongFileUtil.getFile(file, dir, path, virtualPath);
	}

	/**
	 * 获取GradioFile.封装类
	 *
	 * @param files 文件集合
	 * @return GradioFile.
	 */
	public List<WrappedFile> getFiles(List<MultipartFile> files) {
		return KongFileUtil.getFiles(files);
	}

	/**
	 * 获取GradioFile.封装类
	 *
	 * @param files 文件集合
	 * @param dir   目录
	 * @return GradioFile.
	 */
	public List<WrappedFile> getFiles(List<MultipartFile> files, String dir) {
		return KongFileUtil.getFiles(files, dir);
	}

	/**
	 * 获取GradioFile.封装类
	 *
	 * @param files       文件集合
	 * @param dir         目录
	 * @param path        目录
	 * @param virtualPath 虚拟路径
	 * @return GradioFile.
	 */
	public List<WrappedFile> getFiles(List<MultipartFile> files, String dir, String path, String virtualPath) {
		return KongFileUtil.getFiles(files, dir, path, virtualPath);
	}


	public ApiInfo apiInfo(SwaggerProperties swaggerProperties) {
		return new ApiInfoBuilder()
			.title(swaggerProperties.getTitle())
			.description(swaggerProperties.getDescription() + "," + swaggerProperties.getAppName())
			.license(String.format("%s,%s", swaggerProperties.getLicense(), swaggerProperties.getBootInter()))
			.licenseUrl(swaggerProperties.getLicenseUrl())
			.termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
			.contact(new Contact(swaggerProperties.getContact().getName(), swaggerProperties.getContact().getUrl(), swaggerProperties.getContact().getEmail()))
			.version(swaggerProperties.getVersion())
			.termsOfServiceUrl(String.format("%b,%s,%s,%s", swaggerProperties.isEnvMarkStatus(), swaggerProperties.getBootCounter(), swaggerProperties.getBootInfo(), swaggerProperties.getExtInfo()))
			.build();
	}


	protected boolean unFilter(String authKey) {
		return AppConstant.APPLICATION_ROOT_USER_NAME.equalsIgnoreCase(authKey);
	}
	/*
	protected boolean unFilter(String authKey) {
		//return AppConstant.APPLICATION_ROOT_USER_NAME.equalsIgnoreCase(authKey);
		String key = RegisterConstants.GATEWAY_ENCODE_KEY;
		authKey = cn.hutool.crypto.SecureUtil.md5(key + authKey);
		return RegisterBuilder.checkSecondarySecurity(authKey);
	}
	*/

}
