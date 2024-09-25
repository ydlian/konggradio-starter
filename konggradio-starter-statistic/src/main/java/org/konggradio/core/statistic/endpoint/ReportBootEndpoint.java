/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ReportBootEndpoint.java
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
package org.konggradio.core.statistic.endpoint;

import org.konggradio.core.constant.AppConstant;
import org.konggradio.core.statistic.service.IReportFileService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * UReport Boot版 API端点
 *
 * @author ydlian
 */
@ApiIgnore
@RestController
@RequestMapping("/report/rest")
public class ReportBootEndpoint extends ReportEndpoint {

	public ReportBootEndpoint(IReportFileService service) {
		super(service);
	}

}
