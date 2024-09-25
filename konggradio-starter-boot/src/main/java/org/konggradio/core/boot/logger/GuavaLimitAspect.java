/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: GuavaLimitAspect.java
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

package org.konggradio.core.boot.logger;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.konggradio.core.boot.annotation.FlowLimitConfig;
import org.konggradio.enums.CoreResultCode;
import org.konggradio.mertrics.core.limit.RateLimitHelper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;

//@Aspect
//@Component
@Slf4j
public class GuavaLimitAspect {


	@Before("execution(@RateLimitConfig * *(..))")
	public void limit(JoinPoint joinPoint) {
		//1、获取当前的调用方法
		Method currentMethod = getCurrentMethod(joinPoint);
		if (Objects.isNull(currentMethod)) {
			return;
		}
		//2、从方法注解定义上获取限流的类型
		String limitType = currentMethod.getAnnotation(FlowLimitConfig.class).limitType();
		double limitCount = currentMethod.getAnnotation(FlowLimitConfig.class).limitCount();
		//使用guava的令牌桶算法获取一个令牌，获取不到先等待
		RateLimiter rateLimiter = RateLimitHelper.getRateLimiter(limitType, limitCount);
		boolean b = rateLimiter.tryAcquire();
		if (b) {
			//System.out.println("获取到令牌");
		}else {
			HttpServletResponse resp = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("success",false);
			jsonObject.put("msg", CoreResultCode.FLOW_LIMIT.getMsg());
			try {
				output(resp, jsonObject.toJSONString());
			}catch (Exception e){
				log.error("error,e:{}",e);
			}
		}
	}

	private Method getCurrentMethod(JoinPoint joinPoint) {
		Method[] methods = joinPoint.getTarget().getClass().getMethods();
		Method target = null;
		for (Method method : methods) {
			if (method.getName().equals(joinPoint.getSignature().getName())) {
				target = method;
				break;
			}
		}
		return target;
	}

	public void output(HttpServletResponse response, String msg) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		ServletOutputStream outputStream = null;
		try {
			outputStream = response.getOutputStream();
			outputStream.write(msg.getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			outputStream.flush();
			outputStream.close();
		}
	}
}
