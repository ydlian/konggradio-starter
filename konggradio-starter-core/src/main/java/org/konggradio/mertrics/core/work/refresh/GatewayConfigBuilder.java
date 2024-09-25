/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: GatewayConfigBuilder.java
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

package org.konggradio.mertrics.core.work.refresh;

import cn.hutool.core.util.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.condition.LaunchConstant;
import org.konggradio.core.RegisterBuilder;
import org.konggradio.core.constant.AppConstant;
import org.konggradio.core.launch.constant.EnvConstant;
import org.konggradio.core.tool.utils.RedisUtil;
import org.konggradio.mertrics.core.AppCtlService;
import org.konggradio.mertrics.core.GatewayConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;


@Configuration
@EnableScheduling
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@ConditionalOnProperty(name = "spring.application.gateway.active", havingValue = "true")
public  class GatewayConfigBuilder {
	@Value("${spring.application.gateway.type:0}")
	private String gatewayType;
	@Resource
	GatewayConfigService gatewayConfigService;

	@Resource
	RedisUtil redisUtil;

	@Scheduled(cron = "0/5 * * * * ?")
	private void brownianMotion() {
		int a1 = LaunchConstant.LATCH_MATRIX[0][0];
		int a2 = LaunchConstant.LATCH_MATRIX[0][1];
		int b1 = LaunchConstant.LATCH_MATRIX[1][0];
		int b2 = LaunchConstant.LATCH_MATRIX[1][1];
		int a3 = RandomUtil.randomInt(a1, a2 > a1 ? a2 : a1 + 1);
		int a4 = RandomUtil.randomInt(a1, a2 > a1 ? a2 : a1 + 1);
		LaunchConstant.LATCH_MATRIX[0][0] = (a3 + a4) / 2;
		LaunchConstant.LATCH_MATRIX[0][1] = (a3 + a4) / 2;
		int b3 = RandomUtil.randomInt(b1, b2 > b1 ? b2 : b1 + 1);
		int b4 = RandomUtil.randomInt(b1, b2 > b1 ? b2 : b1 + 1);
		LaunchConstant.LATCH_MATRIX[1][0] = (b3 + b4) / 2;
		LaunchConstant.LATCH_MATRIX[1][1] = (b3 + b4) / 2;

	}



}
