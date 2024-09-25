/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: CommonMetricService.java
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

import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.tool.utils.GsonUtils;
import org.konggradio.core.tool.utils.RedisUtil;
import org.konggradio.enums.CachePrefix;
import org.konggradio.mertrics.MetricFilter;
import org.konggradio.mertrics.MetricRecoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CommonMetricService {

	private static final ConcurrentMap<String, MetricRecoder> REGISTRIES =
		new ConcurrentHashMap<>();
	private static final Set<String> SKIP_SETS = MetricService.SKIP_SETS;

	@Resource
	RedisUtil redisUtil;

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

	public void removeMetric(String name) {
		String nameListKey = CachePrefix.METRIC_REG_NAME_SETS_PRE.genKey("");
		redisUtil.setRemove(nameListKey, name);
	}

	private MetricRecoder addMetricRegistry(String name) {
		MetricRecoder metricRecoder;
		String nameListKey = CachePrefix.METRIC_REG_NAME_SETS_PRE.genKey("");
		redisUtil.sSet(nameListKey, name);
		String nameCacheKey = CachePrefix.METRIC_REG_PRE.genKey(name);
		if (redisUtil.hasKey(nameCacheKey)) {
			try {
				String value = String.valueOf(redisUtil.get(nameCacheKey));
				metricRecoder = GsonUtils.getGson().fromJson(value, MetricRecoder.class);
				metricRecoder.setShadow(metricRecoder.getShadow() + 1);
				metricRecoder.inc(metricRecoder.getShadow());
			} catch (Exception e) {
				//System.err.println("[Metric]" + e.getMessage());
				metricRecoder = new MetricRecoder();
			}
		} else {
			metricRecoder = new MetricRecoder();
			metricRecoder.setName(name);
			metricRecoder.setShadow(1L);
			metricRecoder.inc(metricRecoder.getShadow());
		}
		if (metricRecoder != null) {
			add(name, metricRecoder);
		}

		return metricRecoder;
	}

	public MetricRecoder addTimeMetric(String uri, String name, Long timeUsed) {
		if (checkIfSkip(uri)) {
			return null;
		}
		MetricRecoder metricRecoder = addMetricRegistry(name);
		if (metricRecoder == null) return null;
		metricRecoder.setName(name);
		metricRecoder.setTimeUsed(timeUsed);
		cacheMetricRecoder(name, metricRecoder);
		return metricRecoder;
	}

	private void cacheMetricRecoder(String name, MetricRecoder metricRecoder) {
		String nameCacheKey = CachePrefix.METRIC_REG_PRE.genKey(name);
		String save = GsonUtils.getGson().toJson(metricRecoder);
		//System.out.println(name + "->" + save);
		redisUtil.set(nameCacheKey, save, 3600 * 24 * 90, TimeUnit.SECONDS);

	}

	private boolean checkIfSkip(String uri) {
		for (String tag : SKIP_SETS) {
			if (uri.startsWith(tag)) {
				//System.out.println("Skip:" + uri + "," + name);
				return true;
			}
		}
		return false;
	}

	public MetricRecoder addMetric(String uri, String name) {
		if (checkIfSkip(uri)) {
			return null;
		}
		MetricRecoder metricRecoder = addMetricRegistry(name);
		if (metricRecoder == null) return null;
		metricRecoder.setName(name);
		cacheMetricRecoder(name, metricRecoder);

		return metricRecoder;
	}

	public TreeMap<Long, MetricRecoder> namesTop(int number) {

		return getCounters(number);
	}


	public TreeMap<Long, MetricRecoder> getCounters(int number) {
		return getCounters(MetricFilter.ALL, number);
	}

	public TreeMap<Long, MetricRecoder> getTimeUsedCounters(int number) {
		return getTimeUsedCounters(MetricFilter.ALL, number);
	}

	public TreeMap<Long, MetricRecoder> getCounters(MetricFilter filter, int number) {
		return getMetrics(MetricRecoder.class, filter, number);
	}

	public TreeMap<Long, MetricRecoder> getTimeUsedCounters(MetricFilter filter, int number) {
		return getTimeUsedMetrics(MetricRecoder.class, filter, number);
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
			//System.out.println(key + "->" + timers.get(key).getName());
			resultMap.put(key, timers.get(key));
			if (i >= number) {
				break;
			}

		}
		return resultMap;
	}

	private <T extends MetricRecoder> TreeMap<Long, T> getTimeUsedMetrics(Class<T> klass, MetricFilter filter, int number) {
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
				timers.put(metrics.getTimeUsed(), (T) metrics);
			}
		}
		int i = 0;
		for (Long key : timers.keySet()) {
			i++;
			System.out.println(key + "->" + timers.get(key).getName());
			resultMap.put(key, timers.get(key));
			if (i >= number) {
				break;
			}

		}
		return resultMap;
	}

	public TreeMap<Long, MetricRecoder> timeUsedTop(int number) {
		return getTimeUsedCounters(number);
	}
}
