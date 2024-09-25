/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ReportEndpoint.java
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

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.konggradio.core.mp.support.Condition;
import org.konggradio.core.mp.support.Query;
import org.konggradio.core.statistic.entity.ReportFileEntity;
import org.konggradio.core.statistic.service.IReportFileService;
import org.konggradio.core.tool.api.R;
import org.konggradio.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * UReport API端点
 *
 * @author ydlian
 */
@ApiIgnore
@RestController
@AllArgsConstructor
@RequestMapping("/report/rest")
public class ReportEndpoint {

	private final IReportFileService service;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	public R<ReportFileEntity> detail(ReportFileEntity file) {
		ReportFileEntity detail = service.getOne(Condition.getQueryWrapper(file));
		return R.data(detail);
	}

	/**
	 * 分页
	 */
	@GetMapping("/list")
	public R<IPage<ReportFileEntity>> list(@RequestParam Map<String, Object> file, Query query) {
		IPage<ReportFileEntity> pages = service.page(Condition.getPage(query), Condition.getQueryWrapper(file, ReportFileEntity.class));
		return R.data(pages);
	}

	/**
	 * 删除
	 */
	@PostMapping("/remove")
	public R remove(@RequestParam String ids) {
		boolean temp = service.removeByIds(Func.toLongList(ids));
		return R.status(temp);
	}

}
