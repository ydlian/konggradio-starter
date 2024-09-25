/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ApiLogAspect.java
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

package org.konggradio.core.log.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.konggradio.core.log.annotation.ApiLog;
import org.konggradio.core.log.publisher.ApiLogPublisher;

/**
 * 操作日志使用spring event异步入库
 *
 * @author ydlian
 */
@Slf4j
@Aspect
public class ApiLogAspect {

	@Around("@annotation(apiLog)")
	public Object around(ProceedingJoinPoint point, ApiLog apiLog) throws Throwable {

		String className = point.getTarget().getClass().getName();

		String methodName = point.getSignature().getName();

		long beginTime = System.currentTimeMillis();

		Object result = point.proceed();

		long time = System.currentTimeMillis() - beginTime;

		ApiLogPublisher.publishEvent(methodName, className, apiLog, time);
		return result;
	}

}
