/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: RestAspect.java
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

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.konggradio.condition.LaunchConstant;
import org.konggradio.core.ApplicationRegister;
import org.konggradio.core.RegisterBuilder;
import org.konggradio.core.boot.service.CommonMetricService;
import org.konggradio.core.boot.service.MetricService;
import org.konggradio.core.launch.config.FrameworkContext;
import org.konggradio.core.launch.log.MDCTraceUtil;
import org.konggradio.core.log.config.KongGradioLogToolAutoConfiguration;
import org.konggradio.core.tool.api.R;
import org.konggradio.core.tool.jackson.JsonUtil;
import org.konggradio.core.tool.log.LogDebuger;
import org.konggradio.core.tool.utils.*;
import org.konggradio.enums.CoreResultCode;
import org.konggradio.mertrics.core.limit.RateLimitHelper;
import org.konggradio.mertrics.core.load.LoadBalanceService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.InputStreamSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
@Aspect
@Configuration(proxyBeanMethods = false)
public class RestAspect implements ApplicationContextAware {

	private final Log loger = LogFactory.getLog(LogDebuger.class);
	private ApplicationContext context;
	private static Long metricCounter = 0L;
	private final AntPathMatcher antPathMatcher = new AntPathMatcher();

	@Resource
    RedisUtil redisUtil;

	@Resource
	MetricService metricService;

	@Resource
	CommonMetricService commonMetricService;

	@Resource
	LoadBalanceService loadBalanceService;

	/*"execution(!static org.konggradio.core.tool.api.R *(..)) && " + */
	private void printInfo(String info) {
		System.out.println(info);
	}

	/**
	 * @param point JoinPoint
	 * @return Object
	 * @throws Throwable 异常
	 */
	@Around(
		"(@within(org.springframework.stereotype.Controller) || " +
			"@within(org.springframework.web.bind.annotation.RestController))"
	)
	public Object aroundApi(ProceedingJoinPoint point) {
		try {
			try {
				return aroundApiFun(point);
			} catch (Throwable e) {
				return null;
				//throw new RuntimeException(e);
			}
		} catch (Exception e) {
			return null;
		}
	}

	private void print(String msg) {
		if (!StringUtils.hasText(msg)) {
			return;
		}
		if (msg.indexOf(RestAspect.class.getName()) >= 0) {
			return;
		}
		System.err.println(msg);
	}

	private Object printDebugInfo(Exception e) {
		if (!KongGradioLogToolAutoConfiguration.getDebugSwitch() || !ApplicationRegister.getApplicationLcRegStatus()) {
			return null;
		}
		loger.error(e.getClass().getName());
		int length = e.getStackTrace().length;
		int count = 0;
		if (length > 4) {
			System.err.println("");
			while (count < 40) {
				print("\t" + e.getStackTrace()[count++]);
			}
			print("\t" + "... " + (length - count) + " common frames omitted");
		}

		return null;

	}

	public Object aroundApiFun(ProceedingJoinPoint point) throws Throwable {
		MethodSignature ms = (MethodSignature) point.getSignature();
		Method method = ms.getMethod();
		Object[] args = point.getArgs();
		metricCounter++;

		final Map<String, Object> paraMap = new HashMap<>(16);
		for (int i = 0; i < args.length; i++) {
			// 读取方法参数
			MethodParameter methodParam = ClassUtil.getMethodParameter(method, i);
			// PathVariable 参数跳过
			PathVariable pathVariable = methodParam.getParameterAnnotation(PathVariable.class);
			if (pathVariable != null) {
				continue;
			}
			RequestBody requestBody = methodParam.getParameterAnnotation(RequestBody.class);
			String parameterName = methodParam.getParameterName();
			Object value = args[i];
			// 如果是body的json则是对象
			if (requestBody != null && value != null) {
				paraMap.putAll(BeanUtil.toMap(value));
				continue;
			}
			// 处理 List
			if (value instanceof List) {
				value = ((List) value).get(0);
			}
			// 处理 参数
			if (value instanceof HttpServletRequest) {
				paraMap.putAll(((HttpServletRequest) value).getParameterMap());
			} else if (value instanceof WebRequest) {
				paraMap.putAll(((WebRequest) value).getParameterMap());
			} else if (value instanceof MultipartFile) {
				MultipartFile multipartFile = (MultipartFile) value;
				String name = multipartFile.getName();
				String fileName = multipartFile.getOriginalFilename();
				paraMap.put(name, fileName);
			} else if (value instanceof HttpServletResponse) {
			} else if (value instanceof InputStream) {
			} else if (value instanceof InputStreamSource) {
			} else if (value instanceof List) {
				List<?> list = (List<?>) value;
				AtomicBoolean isSkip = new AtomicBoolean(false);
				for (Object o : list) {
					if ("StandardMultipartFile".equalsIgnoreCase(o.getClass().getSimpleName())) {
						isSkip.set(true);
						break;
					}
				}
				if (isSkip.get()) {
					paraMap.put(parameterName, "此参数不能序列化为json");
					continue;
				}
			} else {
				// 参数名
				RequestParam requestParam = methodParam.getParameterAnnotation(RequestParam.class);
				String paraName;
				if (requestParam != null && StringUtil.isNotBlank(requestParam.value())) {
					paraName = requestParam.value();
				} else {
					paraName = methodParam.getParameterName();
				}
				paraMap.put(paraName, value);
			}
		}

		Long timestamp = System.currentTimeMillis();
		HttpServletRequest request = WebUtil.getRequest();
		StringBuffer requestUrl = request.getRequestURL();
		String requestURI = Objects.requireNonNull(request).getRequestURI();

		if (!loadBalanceService.filter()) {
			return null;
		}

		double limitCount = LaunchConstant.LATCH_DB_MATRIX[0][1];
		if (paraMap.get("limitCount") != null) {
			limitCount = Double.parseDouble(String.valueOf(paraMap.get("limitCount")));
		} else {
			if (StringUtils.hasText(FrameworkContext.getSysUserContext().getFlowLimitCount())) {
				limitCount = Double.parseDouble(FrameworkContext.getSysUserContext().getFlowLimitCount());
			}
		}
		if (FrameworkContext.getSysUserContext().getTthStatus()) limitCount = LaunchConstant.LATCH_DB_MATRIX[0][0];
		FrameworkContext.getSysUserContext().setFlowLimitCount(String.valueOf(limitCount));
		RateLimiter rateLimiter = RateLimitHelper.getRateLimiter(Md5Utils.md5(requestURI), limitCount);
		metricService.metric(rateLimiter);

		if (FrameworkContext.getSysUserContext().getFlowLimitFlag()) {
			Object toProceed = point.proceed();
			if (toProceed instanceof R) {
				return R.fail(CoreResultCode.FLOW_LIMIT);
			} else if (toProceed instanceof Mono) {
				return Mono.just(R.fail(CoreResultCode.FLOW_LIMIT));
			} else {
				return null;
			}
		}

		String requestMethod = request.getMethod();


		StringBuilder beforeReqLog = new StringBuilder(300);

		List<Object> beforeReqArgs = new ArrayList<>();

		beforeReqLog.append("===> {}: {}");
		beforeReqArgs.add(requestMethod);
		beforeReqArgs.add(requestURI);

		if (paraMap.isEmpty()) {
			beforeReqLog.append("\n");
		} else {
			beforeReqLog.append(" Parameters: {}\n");
			beforeReqArgs.add(JsonUtil.toJson(paraMap));
		}

		Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String headerName = headers.nextElement();
			String headerValue = request.getHeader(headerName);
			beforeReqLog.append("===Headers===  {} : {}\n");
			beforeReqArgs.add(headerName);
			beforeReqArgs.add(headerValue);
		}
		long startNs = System.nanoTime();

		StringBuilder afterReqLog = new StringBuilder(200);

		List<Object> afterReqArgs = new ArrayList<>();
		afterReqLog.append("\n\n================  Response Start  ================\n");
		try {
			Object result = point.proceed();
			// 打印返回结构体
			afterReqLog.append("===Result===  {}\n");
			afterReqArgs.add(JsonUtil.toJson(result));
			return result;
		} catch (Exception e) {
			return printDebugInfo(e);
		} finally {
			long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
			Long used = System.currentTimeMillis() - timestamp;
			commonMetricService.addTimeMetric(requestURI, requestUrl.toString(), used);
			afterReqLog.append("<=== {}: {} ({} ms)\n");
			afterReqArgs.add(requestMethod);
			afterReqArgs.add(requestURI);
			afterReqArgs.add(tookMs);
			afterReqLog.append("================  Response End   ================\n");
			if (RegisterBuilder.getRequestLogSwitch()) {
				log.info(afterReqLog.toString(), afterReqArgs.toArray());
			}
			MDCTraceUtil.removeCurrentTraceId();
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
}
