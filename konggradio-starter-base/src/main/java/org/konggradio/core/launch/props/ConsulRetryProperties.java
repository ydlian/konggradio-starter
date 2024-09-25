/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ConsulRetryProperties.java
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

package org.konggradio.core.launch.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * consul重试配置
 * @author xsx
 * @date 2019/10/31
 * @since 1.8
 */
@Data
@Configuration
@ConfigurationProperties("spring.cloud.consul.retry")
public class ConsulRetryProperties {
	/**
	 * 监测间隔（单位：ms）
	 */
	private long initialInterval = 10000L;
	/**
	 * 间隔因子（备用）
	 */
	private double multiplier = 1.1D;
	/**
	 * 最大间隔（备用）
	 */
	private long maxInterval = 20000L;
	/**
	 * 重试次数（备用）
	 */
	private int maxAttempts = 6;
}
