/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ReportPlaceholderProvider.java
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
package org.konggradio.core.statistic.provider;

import com.bstek.ureport.UReportPropertyPlaceholderConfigurer;
import org.konggradio.core.statistic.props.ReportProperties;

import java.util.Properties;

/**
 * UReport自定义配置
 *
 * @author ydlian
 */
public class ReportPlaceholderProvider extends UReportPropertyPlaceholderConfigurer {

	public ReportPlaceholderProvider(ReportProperties properties) {
		Properties props = new Properties();
		props.setProperty("ureport.disableHttpSessionReportCache", properties.getDisableHttpSessionReportCache().toString());
		props.setProperty("ureport.disableFileProvider", properties.getDisableFileProvider().toString());
		props.setProperty("ureport.fileStoreDir", properties.getFileStoreDir());
		props.setProperty("ureport.debug", properties.getDebug().toString());
		this.setProperties(props);
	}

}
