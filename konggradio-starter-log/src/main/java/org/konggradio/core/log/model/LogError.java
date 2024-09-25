/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: LogError.java
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
package org.konggradio.core.log.model;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 服务 异常
 *
 * @author ydlian
 */
@Data
@TableName("konggradio_log_error")
public class LogError extends LogAbstract implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 堆栈信息
	 */
	private String stackTrace;
	/**
	 * 异常名
	 */
	private String exceptionName;
	/**
	 * 异常消息
	 */
	private String message;

	/**
	 * 文件名
	 */
	private String fileName;

	/**
	 * 代码行数
	 */
	private Integer lineNumber;


}
