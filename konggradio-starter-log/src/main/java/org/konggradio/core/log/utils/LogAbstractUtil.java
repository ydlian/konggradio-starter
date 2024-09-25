/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: LogAbstractUtil.java
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

package org.konggradio.core.log.utils;

import org.konggradio.core.launch.props.kongProperties;
import org.konggradio.core.launch.server.ServerInfo;
import org.konggradio.core.log.model.LogAbstract;
import org.konggradio.core.secure.utils.SecureUtil;
import org.konggradio.core.tool.utils.*;
import org.konggradio.core.tool.utils.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Log 相关工具
 *
 * @author ydlian
 */
public class LogAbstractUtil {

	/**
	 * 向log中添加补齐request的信息
	 *
	 * @param request     请求
	 * @param logAbstract 日志基础类
	 */
	public static void addRequestInfoToLog(HttpServletRequest request, LogAbstract logAbstract) {
		if (ObjectUtil.isNotEmpty(request)) {
			logAbstract.setRemoteIp(WebUtil.getIP(request));
			logAbstract.setUserAgent(request.getHeader(WebUtil.USER_AGENT_HEADER));
			logAbstract.setRequestUri(UrlUtil.getPath(request.getRequestURI()));
			logAbstract.setMethod(request.getMethod());
			logAbstract.setParams(WebUtil.getRequestParamString(request));
			logAbstract.setCreateBy(SecureUtil.getUserAccount(request));
		}
	}

	public static void addOtherInfoToLog(LogAbstract logAbstract, kongProperties kongProperties, ServerInfo serverInfo) {
		logAbstract.setServiceId(kongProperties.getName());
		logAbstract.setServerHost(serverInfo.getHostName());
		logAbstract.setServerIp(serverInfo.getIpWithPort());
		logAbstract.setEnv(kongProperties.getEnv());
		logAbstract.setCreateTime(DateUtil.now());

		if (logAbstract.getParams() == null) {
			logAbstract.setParams(StringPool.EMPTY);
		}
	}
}
