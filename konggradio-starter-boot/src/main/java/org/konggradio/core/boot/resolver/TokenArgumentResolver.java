/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: TokenArgumentResolver.java
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
package org.konggradio.core.boot.resolver;

import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.secure.KongGradioUser;
import org.konggradio.core.secure.utils.SecureUtil;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


@Slf4j
public class TokenArgumentResolver implements HandlerMethodArgumentResolver {

	/**
	 * 入参筛选
	 *
	 * @param methodParameter 参数集合
	 * @return 格式化后的参数
	 */
	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return methodParameter.getParameterType().equals(KongGradioUser.class);
	}

	/**
	 * 出参设置
	 *
	 * @param methodParameter       入参集合
	 * @param modelAndViewContainer model 和 view
	 * @param nativeWebRequest      web相关
	 * @param webDataBinderFactory  入参解析
	 * @return 包装对象
	 */
	@Override
	public Object resolveArgument(MethodParameter methodParameter,
								  ModelAndViewContainer modelAndViewContainer,
								  NativeWebRequest nativeWebRequest,
								  WebDataBinderFactory webDataBinderFactory) {
		return SecureUtil.getUser();
	}

}
