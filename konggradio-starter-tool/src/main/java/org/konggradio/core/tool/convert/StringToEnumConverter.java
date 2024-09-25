/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: StringToEnumConverter.java
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

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.tool.utils.ConvertUtil;
import org.konggradio.core.tool.utils.StringUtil;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.lang.Nullable;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 接收参数 同 jackson String -》 Enum 转换
 *
 * @author ydlian
 */
@Slf4j
public class StringToEnumConverter implements ConditionalGenericConverter {
	/**
	 * 缓存 Enum 类信息，提供性能
	 */
	private static final ConcurrentMap<Class<?>, AccessibleObject> ENUM_CACHE_MAP = new ConcurrentHashMap<>(8);

	@Nullable
	private static AccessibleObject getAnnotation(Class<?> clazz) {
		Set<AccessibleObject> accessibleObjects = new HashSet<>();
		// JsonCreator METHOD, CONSTRUCTOR
		Constructor<?>[] constructors = clazz.getConstructors();
		Collections.addAll(accessibleObjects, constructors);
		// methods
		Method[] methods = clazz.getDeclaredMethods();
		Collections.addAll(accessibleObjects, methods);
		for (AccessibleObject accessibleObject : accessibleObjects) {
			// 复用 jackson 的 JsonCreator注解
			JsonCreator jsonCreator = accessibleObject.getAnnotation(JsonCreator.class);
			if (jsonCreator != null && JsonCreator.Mode.DISABLED != jsonCreator.mode()) {
				accessibleObject.setAccessible(true);
				return accessibleObject;
			}
		}
		return null;
	}

	@Override
	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return true;
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(String.class, Enum.class));
	}

	@Nullable
	@Override
	public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (StringUtil.isBlank((String) source)) {
			return null;
		}
		Class<?> clazz = targetType.getType();
		AccessibleObject accessibleObject = ENUM_CACHE_MAP.computeIfAbsent(clazz, StringToEnumConverter::getAnnotation);
		String value = ((String) source).trim();
		// 如果为null，走默认的转换
		if (accessibleObject == null) {
			return valueOf(clazz, value);
		}
		try {
			return StringToEnumConverter.invoke(clazz, accessibleObject, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <T extends Enum<T>> T valueOf(Class<?> clazz, String value){
		return Enum.valueOf((Class<T>) clazz, value);
	}

	@Nullable
	private static Object invoke(Class<?> clazz, AccessibleObject accessibleObject, String value)
		throws IllegalAccessException, InvocationTargetException, InstantiationException {
		if (accessibleObject instanceof Constructor) {
			Constructor constructor = (Constructor) accessibleObject;
			Class<?> paramType = constructor.getParameterTypes()[0];
			// 类型转换
			Object object = ConvertUtil.convert(value, paramType);
			return constructor.newInstance(object);
		}
		if (accessibleObject instanceof Method) {
			Method method = (Method) accessibleObject;
			Class<?> paramType = method.getParameterTypes()[0];
			// 类型转换
			Object object = ConvertUtil.convert(value, paramType);
			return method.invoke(clazz, object);
		}
		return null;
	}

}
