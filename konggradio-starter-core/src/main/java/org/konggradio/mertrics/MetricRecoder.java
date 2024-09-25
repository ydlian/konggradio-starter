/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: MetricRecoder.java
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

package org.konggradio.mertrics;

import java.util.concurrent.atomic.LongAdder;

/**
 * A registry of metric instances.
 */
public class MetricRecoder {
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String name;

	public long getTimeUsed() {
		return timeUsed;
	}

	public void setTimeUsed(long timeUsed) {
		this.timeUsed = timeUsed;
	}

	private long timeUsed;


	public void setCount(LongAdder count) {
		this.count = count;
	}

	private LongAdder count;

	public long getShadow() {
		return shadow;
	}

	public void setShadow(long shadow) {
		this.shadow = shadow;
	}

	private long shadow;

	public MetricRecoder() {
		this.count = new LongAdder();
	}

	public MetricRecoder(LongAdder count) {
		this.count = count;
	}

	/**
	 * Increment the counter by one.
	 */
	public void inc() {
		inc(1);
	}

	public void inc(long n) {
		count.add(n);
		setShadow(count.longValue());
	}

	/**
	 * Decrement the counter by one.
	 */
	public void dec() {
		dec(1);
	}

	public void dec(long n) {
		count.add(-n);
		setShadow(count.longValue());
	}

	/**
	 * Returns the counter's current value.
	 *
	 * @return the counter's current value
	 */
	public long getCount() {
		return count.longValue();
	}
}
