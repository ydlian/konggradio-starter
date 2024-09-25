/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: RateLimitHelper.java
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

package org.konggradio.mertrics.core.limit;

import com.google.common.util.concurrent.RateLimiter;

import java.util.HashMap;
import java.util.Map;

public class RateLimitHelper {

	private RateLimitHelper(){}

	private static Map<String, RateLimiter> rateMap = new HashMap<>();

	public static RateLimiter getRateLimiter(String limitType,double limitCount ){
		try {
			RateLimiter rateLimiter = rateMap.get(limitType);
			if(rateLimiter == null){
				rateLimiter = RateLimiter.create(limitCount);
				rateMap.put(limitType,rateLimiter);
			}
			return rateLimiter;
		}catch (Exception e){

		}
		return null;

	}

}
