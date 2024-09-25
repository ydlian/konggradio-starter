/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: MetricService.java
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

package org.konggradio.core.boot.service;

import com.alibaba.csp.sentinel.context.ContextUtil;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.RegisterBuilder;
import org.konggradio.core.launch.config.FrameworkContext;
import org.konggradio.core.tool.utils.GsonUtils;
import org.konggradio.core.tool.utils.RedisUtil;
import org.konggradio.enums.CachePrefix;
import org.konggradio.mertrics.MetricFilter;
import org.konggradio.mertrics.MetricRecoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.*;

@Service
@Slf4j
public class MetricService {

	private static final ConcurrentMap<String, MetricRecoder> REGISTRIES =
		new ConcurrentHashMap<>();
	public static final Set<String> SKIP_SETS = new HashSet<>();

	static {
		SKIP_SETS.add("/auth/common");
		SKIP_SETS.add("/auth/health");
		SKIP_SETS.add("/auth/metric");
		SKIP_SETS.add("/health");
		SKIP_SETS.add("/config/");
		SKIP_SETS.add("/common/");
		SKIP_SETS.add("/actuator/health");
	}

	@Resource
	RedisUtil redisUtil;

	private static void print(String out) {
		System.out.println(out);
	}

	public void metric(RateLimiter rateLimiter) throws InterruptedException, ExecutionException {

		FrameworkContext context = FrameworkContext.getSysUserContext();
		if (rateLimiter != null) {
			context.setFlowLimitFlag(!rateLimiter.tryAcquire());
		}

		context.setTthStatus(true);
		Executor executor = Executors.newFixedThreadPool(3);
		CompletionService<String> service = new ExecutorCompletionService<>(executor);
		service.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				String msg = Thread.currentThread().getName();
				String contextName = ContextUtil.getContext().getName();
				StringBuffer sb = new StringBuffer(msg);
				String info = sb.append(contextName).append(GsonUtils.getInstance().toJson(context)).toString();
				RegisterBuilder.buildThreadInfo(info);
				return info;
			}
		}).get();
	}

	public static void clear() {
		REGISTRIES.clear();
	}

	public static Set<String> names() {
		return REGISTRIES.keySet();
	}

	public static void remove(String key) {
		REGISTRIES.remove(key);
	}

	public static MetricRecoder add(String name, MetricRecoder registry) {
		return REGISTRIES.putIfAbsent(name, registry);
	}

	public Set<Object> namesMetric() {
		String nameListKey = CachePrefix.METRIC_REG_NAME_SETS_PRE.genKey("");
		return redisUtil.sGet(nameListKey);
	}


	public TreeMap<Long, MetricRecoder> namesTop(int number) {

		return getCounters(number);
	}

	public TreeMap<Long, MetricRecoder> getCounters(int number) {
		return getCounters(MetricFilter.ALL, number);
	}

	public TreeMap<Long, MetricRecoder> getCounters(MetricFilter filter, int number) {
		return getMetrics(MetricRecoder.class, filter, number);
	}

	private <T extends MetricRecoder> TreeMap<Long, T> getMetrics(Class<T> klass, MetricFilter filter, int number) {

		TreeMap<Long, T> resultMap = new TreeMap<Long, T>(
			Comparator.reverseOrder());
		if (number <= 0) return resultMap;

		final TreeMap<Long, T> timers = new TreeMap<Long, T>(
			Comparator.reverseOrder());

		Set<Object> dataSets = namesMetric();
		for (Object obj : dataSets) {
			String name = String.valueOf(obj);
			if (!redisUtil.hasKey(CachePrefix.METRIC_REG_PRE.genKey(name))) {
				continue;
			}
			String value = String.valueOf(redisUtil.get(CachePrefix.METRIC_REG_PRE.genKey(name)));
			MetricRecoder metrics = null;
			try {
				metrics = GsonUtils.getGson().fromJson(value, MetricRecoder.class);
			} catch (Exception e) {

			}
			if (metrics != null && metrics.getName() != null) {
				timers.put(metrics.getShadow(), (T) metrics);
			}
		}


		int i = 0;
		for (Long key : timers.keySet()) {
			i++;
			resultMap.put(key, timers.get(key));
			if (i >= number) {
				break;
			}

		}
		return resultMap;
	}
}
