/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: MDCTraceUtil.java
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

package org.konggradio.core.tool.log;

import org.slf4j.MDC;

import java.util.UUID;

public class MDCTraceUtil {
	public final static String HTTP_HEADER_TRACE_ID = "trace_id";
	public static final String TRACE_ID_TAG = "TRACE_ID";

	public static String putCurrentTraceId() {
		String traceId = System.currentTimeMillis() + UUID.randomUUID().toString().split("-")[4];

		String tagValue = MDC.get(TRACE_ID_TAG);
		if (tagValue == null) {
			MDC.put(TRACE_ID_TAG, traceId);
		}
		return traceId;
	}

	public static String putCurrentTraceId(String traceId) {
		MDC.put(TRACE_ID_TAG, traceId);
		return traceId;
	}

	public static String getCurrentTraceId() {
		return MDC.get(TRACE_ID_TAG);
	}

	public static void removeCurrentTraceId() {
		MDC.remove(TRACE_ID_TAG);
	}
}
