/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongCloudBootTest.java
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

package org.konggradio.core.test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 简化 测试
 *
 * @author ydlian
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootTest
@ExtendWith(KongGradioSpringExtension.class)
public @interface KongGradioBootTest {
	/**
	 * 服务名：appName
	 * @return appName
	 */
	@AliasFor("appName")
	String value() default "konggradio-test";
	/**
	 * 服务名：appName
	 * @return appName
	 */
	@AliasFor("value")
	String appName() default "konggradio-test";
	/**
	 * profile
	 * @return profile
	 */
	String profile() default "dev";
	/**
	 * 启用 ServiceLoader 加载 launcherService
	 * @return 是否启用
	 */
	boolean enableLoader() default false;
}
