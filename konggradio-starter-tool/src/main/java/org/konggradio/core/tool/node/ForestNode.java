/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ForestNode.java
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
package org.konggradio.core.tool.node;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 森林节点类
 *
 * @author ydlian
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ForestNode extends BaseNode {

	private static final long serialVersionUID = 1L;

	/**
	 * 节点内容
	 */
	private Object content;

	public ForestNode(Long id, Long parentId, Object content) {
		this.id = id;
		this.parentId = parentId;
		this.content = content;
	}

}
