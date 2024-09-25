/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: TraceFilter.java
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

package org.konggradio.core.filter;

import lombok.extern.slf4j.Slf4j;

import org.konggradio.core.tool.log.MDCTraceUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class TraceFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
		String traceId = httpServletRequest.getHeader(MDCTraceUtil.HTTP_HEADER_TRACE_ID);
		if (StringUtils.hasText(traceId)) {
			MDCTraceUtil.putCurrentTraceId(traceId);
		} else {
			// "traceId"
			MDCTraceUtil.putCurrentTraceId();
		}
		filterChain.doFilter(httpServletRequest, httpServletResponse);
		MDCTraceUtil.removeCurrentTraceId();
	}
}

