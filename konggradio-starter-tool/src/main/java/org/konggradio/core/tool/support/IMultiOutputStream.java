/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: IMultiOutputStream.java
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
package org.konggradio.core.tool.support;

import java.io.OutputStream;

/**
 * A factory for creating MultiOutputStream objects.
 *
 * @author ydlian
 */
public interface IMultiOutputStream {

	/**
	 * Builds the output stream.
	 *
	 * @param params the params
	 * @return the output stream
	 */
	OutputStream buildOutputStream(Integer... params);

}
