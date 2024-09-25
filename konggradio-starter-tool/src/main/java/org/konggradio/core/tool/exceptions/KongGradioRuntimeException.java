/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioRuntimeException.java
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

package org.konggradio.core.tool.exceptions;

/**
 * KongCloud Exception.
 */
public class KongGradioRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 8068509879445395353L;

	/**
	 * Instantiates a new KongCloud exception.
	 *
	 * @param e the e
	 */
	public KongGradioRuntimeException(final Throwable e) {
		super(e);
	}

	/**
	 * Instantiates a new KongCloud exception.
	 *
	 * @param message the message
	 */
	public KongGradioRuntimeException(final String message) {
		super(message);
	}

	/**
	 * Instantiates a new KongCloud exception.
	 *
	 * @param message   the message
	 * @param throwable the throwable
	 */
	public KongGradioRuntimeException(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
