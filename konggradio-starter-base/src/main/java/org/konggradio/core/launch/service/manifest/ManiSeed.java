/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ManiSeed.java
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

package org.konggradio.core.launch.service.manifest;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.konggradio.core.launch.constant.EnvConstant;

import java.util.jar.Manifest;

public class ManiSeed extends ManiSeedFred{
	public static long ManiSeed(Manifest manifest){
		String createDate = TAG;
		if (manifest != null && manifest.getMainAttributes() != null) {
			String attrValue = manifest.getMainAttributes().getValue(EnvConstant.B_STR_ARRAY[3][1]);
			if (!StringUtils.isBlank(attrValue)) createDate = attrValue;
		}
		return seed(DateUtil.parse(createDate, DatePattern.PURE_DATE_PATTERN), DateUtil.date());
	}
	public ManiSeed(){
	}

}
