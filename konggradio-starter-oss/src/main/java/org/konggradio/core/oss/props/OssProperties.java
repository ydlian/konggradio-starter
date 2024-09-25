/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: OssProperties.java
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
package org.konggradio.core.oss.props;

import lombok.Data;
import org.konggradio.core.constant.AppConstant;
import org.konggradio.core.tool.support.Kv;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Minio参数配置类
 *
 * @author ydlian
 */
@Data
@ConfigurationProperties(prefix = "KongGradio.oss")
public class OssProperties {

	/**
	 * 是否启用
	 */
	private Boolean enabled;

	/**
	 * 对象存储名称
	 */
	private String name;

	/**
	 * 是否开启租户模式
	 */
	private Boolean tenantMode;
	/**
	 * 对象存储服务的URL
	 */
	private String endpoint;

	/**
	 * 区域
	 */
	private String region;


	/**
	 * true path-style nginx 反向代理和S3默认支持 pathStyle模式 {http://endpoint/bucketname}
	 * false supports virtual-hosted-style 阿里云等需要配置为 virtual-hosted-style 模式{http://bucketname.endpoint}
	 * 只是url的显示不一样
	 */
	private Boolean pathStyleAccess = true;

	/**
	 * Access key
	 */
	private String accessKey;

	/**
	 * Secret key
	 */
	private String secretKey;


	/**
	 * 最大线程数，默认： 100
	 */
	private Integer maxConnections = 100;
	/**
	 * 默认的存储桶名称
	 */
	private String bucketName = AppConstant.APPLICATION_ROOT_USER_NAME;

	/**
	 * 自定义属性
	 */
	private Kv args;

}
