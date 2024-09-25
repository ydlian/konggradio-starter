/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: IClientDetails.java
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
package org.konggradio.core.secure.provider;

import java.io.Serializable;

/**
 * 多终端详情接口
 *
 * @author ydlian
 */
public interface IClientDetails extends Serializable {

	/**
	 * 客户端id.
	 *
	 * @return String.
	 */
	String getClientId();

	/**
	 * 客户端密钥.
	 *
	 * @return String.
	 */
	String getClientSecret();

	/**
	 * 客户端token过期时间
	 *
	 * @return Integer
	 */
	Integer getAccessTokenValidity();

	/**
	 * 客户端刷新token过期时间
	 *
	 * @return Integer
	 */
	Integer getRefreshTokenValidity();

}
