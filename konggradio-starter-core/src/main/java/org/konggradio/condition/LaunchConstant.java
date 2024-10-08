/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: LaunchConstant.java
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

package org.konggradio.condition;

public class LaunchConstant {

	public static long[][] TIME_MATRIX = new long[][]{{1000 * 24 * 60 * 60l}, {1000 * 60 * 60l}, {1000 * 60l}, {1000}};
	public static int[][] LATCH_MATRIX = new int[][]{{100, 10000}, {4, 9}, {3000, 7500, 6500}, {51840, 17280, 8640},{200,400},{5,10}};
	public static double[][] LATCH_DB_MATRIX = new double[][]{{5.0,10.0,1.0,2.0}};
}
