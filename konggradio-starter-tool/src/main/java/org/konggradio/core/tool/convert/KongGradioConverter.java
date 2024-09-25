/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioConverter.java
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

package org.konggradio.core.tool.convert;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.tool.support.Try;
import org.konggradio.core.tool.utils.ClassUtil;
import org.konggradio.core.tool.utils.ConvertUtil;
import org.konggradio.core.tool.utils.ReflectUtil;
import org.springframework.cglib.core.Converter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@AllArgsConstructor
public class KongGradioConverter implements Converter {
	private static final ConcurrentMap<String, TypeDescriptor> TYPE_CACHE = new ConcurrentHashMap<>();
	private final Class<?> targetClazz;

	@Override
	@Nullable
	public Object convert(Object value, Class target, final Object fieldName) {
		if (value == null) {
			return null;
		}

		if (ClassUtil.isAssignableValue(target, value)) {
			return value;
		}
		try {
			TypeDescriptor targetDescriptor = KongGradioConverter.getTypeDescriptor(targetClazz, (String) fieldName);
			return ConvertUtil.convert(value, targetDescriptor);
		} catch (Throwable e) {
			log.warn("Converter error", e);
			return null;
		}
	}

	private static TypeDescriptor getTypeDescriptor(final Class<?> clazz, final String fieldName) {
		String srcCacheKey = clazz.getName() + fieldName;
		return TYPE_CACHE.computeIfAbsent(srcCacheKey, Try.of(k -> {

			Field field = ReflectUtil.getField(clazz, fieldName);
			if (field == null) {
				throw new NoSuchFieldException(fieldName);
			}
			return new TypeDescriptor(field);
		}));
	}
}
