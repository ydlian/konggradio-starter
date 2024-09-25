/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: EventDelayMessage.java
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

package org.konggradio.core.boot.event.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 消息统一封装类
 * @author: xxx
 * @create: xxx
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDelayMessage<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 消息唯一标识
     */
    private String id;
    /**
     * 消息渠道 如 订单 支付 代表不同业务类型
     * 为消费时不同类去处理
     */
    private String channel;
    /**
     * 具体消息 json
     */
    private T body;

	private Object[] params;

	private String taskId;
	/**
	 * groovy method
	 */
	private String method;

	/**
	 * script source: script/url/file path
	 */
	private String source;
    /**
     * 延时时间 被消费时间  取当前时间戳+延迟时间
     */
    private Long delayTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
