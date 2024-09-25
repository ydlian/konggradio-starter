/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: OssRule.java
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
package org.konggradio.core.oss.rule;

/**
 * Oss通用规则
 *
 * @author ydlian
 */
public interface OssRule {

	/**
	 * 获取存储桶规则
	 *
	 * @param bucketName 存储桶名称
	 * @return String
	 */
	String bucketName(String bucketName);

	/**
	 * 获取文件名规则
	 *
	 * @param originalFilename 文件名
	 * @return String
	 */
	String fileName(String originalFilename);

}
