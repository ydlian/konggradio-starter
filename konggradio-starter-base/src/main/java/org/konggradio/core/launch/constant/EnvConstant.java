/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: EnvConstant.java
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

package org.konggradio.core.launch.constant;

public interface EnvConstant {
	String CONSUL_HOST = "spring.cloud.consul.host";
	//public final int DEF_BUILD_DATE = 20230101;
	//public final int DEF_BUILD_DATE_GAP =  365 * 4;
	//public static String B_TIME = "Build-Date-Time";
	public static double[][] LATCH_DB_MATRIX = new double[][]{{5.0,10.0,1.0,2.0}};
	public static String B_STR_ARRAY[][] = {{"C-Build-Date-Time", "Author"}, {"Main-Class", "Spring-Boot-Version"}, {"Security-Public-Key", "Bom-Build-Version"}, {"spring.cloud.consul.host", "Build-Date-Time"}};
	public static long MATRIX[][] = {{1, 1, 5, 10, 8640, 2000}, {2, 3600, 7200, 14400}, {3, 3600, 3600 * 24, 3600 * 24 * 30 * 3}, {4, 1000, 20200101, 20230101, 20230101},
		{5, 365 * 2, 359 * 3, 359 * 5}, {6, 300, 600, 900, 1200}, {220, 100, 3000, 5000}, {360, 720, 1080, 1444}
	};
	public static long LC_MATRIX[][] = {{100, 200, 300}};
	public static final long FIXED_DELAY_MSEC = 120 * 1000L;
	public static final long FIXED_VALID_SEC = 1800L;
	//public static final long CHECK_SECOND = 5L;
	//public static final long STL_SEC_TIME = 3600 * 4;

	//public static final long DIAMOND_SEC_TIME = 3600 * 24 * 30 * 3;

}
