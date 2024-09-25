/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: GitUtil.java
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

package org.konggradio.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {"classpath:git.properties"}, ignoreResourceNotFound = true)
public class GitUtil {

	public String getBranch() {
		return branch.replaceAll("\"","");
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getGitCommitId() {
		return gitCommitId.replaceAll("\"","");
	}

	public void setGitCommitId(String gitCommitId) {
		this.gitCommitId = gitCommitId;
	}
	@Value("${git.branch:}")
	private String branch;

	@Value("${git.commit.id:}")
	private String gitCommitId;


	@Value("${git.commit.id.abbrev:}")
	private String gitCommitShortId;

	@Value("${git.commit.time:}")
	private String gitCommitTime;
	@Value("${git.build.time:}")
	private String gitBuildTime;

	public String getGitCommitTime() {
		return gitCommitTime.replaceAll("\"","");
	}

	public void setGitCommitTime(String gitCommitTime) {
		this.gitCommitTime = gitCommitTime;
	}


	public String getGitCommitShortId() {
		return gitCommitShortId.replaceAll("\"","");
	}

	public void setGitCommitShortId(String gitCommitShortId) {
		this.gitCommitShortId = gitCommitShortId;
	}


	public String getGitBuildTime() {
		return gitBuildTime.replaceAll("\"","");
	}

	public void setGitBuildTime(String gitBuildTime) {
		this.gitBuildTime = gitBuildTime;
	}
}
