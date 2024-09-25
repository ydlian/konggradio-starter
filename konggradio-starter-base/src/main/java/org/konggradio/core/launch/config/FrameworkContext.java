/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: FrameworkContext.java
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

package org.konggradio.core.launch.config;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author:
 * @Date:
 */

@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FrameworkContext {

    private static final TransmittableThreadLocal<FrameworkContext> HOLDER = new TransmittableThreadLocal<>();
    private String applicationName;
    private String threadName;
	private String workerName;

	private String ip;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	private String hostName;

	public String getAppRunFlag() {
		return appRunFlag;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setAppRunFlag(String appRunFlag) {
		this.appRunFlag = appRunFlag;
	}

	public String appRunFlag;

	public boolean isGatewayFlag() {
		return gatewayFlag;
	}

	public void setGatewayFlag(boolean gatewayFlag) {
		this.gatewayFlag = gatewayFlag;
	}

	private boolean gatewayFlag;

	public String getGatewayDomain() {
		return gatewayDomain;
	}

	public void setGatewayDomain(String gatewayDomain) {
		this.gatewayDomain = gatewayDomain;
	}

	private String gatewayDomain;

	public String getJarFullPath() {
		return jarFullPath;
	}

	public void setJarFullPath(String jarFullPath) {
		this.jarFullPath = jarFullPath;
	}

	private String jarFullPath;
	public Boolean getLaunchCheck() {
		return launchCheck;
	}

	public void setLaunchCheck(Boolean launchCheck) {
		this.launchCheck = launchCheck;
	}

	private Boolean launchCheck;

	public String getFileHash() {
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	private String fileHash;
	public String getLastLaunch() {
		return lastLaunch;
	}

	public void setLastLaunch(String lastLaunch) {
		this.lastLaunch = lastLaunch;
	}

	private String lastLaunch;


	public Boolean getFlowLimitFlag() {
		return flowLimitFlag;
	}

	public void setFlowLimitFlag(Boolean flowLimitFlag) {
		this.flowLimitFlag = flowLimitFlag;
	}

	private Boolean flowLimitFlag;

	public Boolean getPerfLockFlag() {
		return perfLockFlag;
	}

	public void setPerfLockFlag(Boolean perfLockFlag) {
		this.perfLockFlag = perfLockFlag;
	}

	private Boolean perfLockFlag;

	public String getFlowLimitCount() {
		return flowLimitCount;
	}

	public void setFlowLimitCount(String flowLimitCount) {
		this.flowLimitCount = flowLimitCount;
	}

	private String flowLimitCount;
	public Boolean getTthStatus() {
		return tthStatus;
	}

	public void setTthStatus(Boolean tthStatus) {
		this.tthStatus = tthStatus;
	}

	private Boolean tthStatus;

	public Boolean getLongTthStatus() {
		return longTthStatus;
	}

	public void setLongTthStatus(Boolean longTthStatus) {
		this.longTthStatus = longTthStatus;
	}

	private Boolean longTthStatus;

	public Long getTtHo() {
		return ttHo;
	}

	public void setTtHo(Long ttHo) {
		this.ttHo = ttHo;
	}

	private Long ttHo;

	public String getWorkerName() {
		return workerName;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	private String profile;

	private Integer port;

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	private String traceId;
    private String sessionId;

    private String siteName;

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	private String instanceId;



	public static void setSysUserContext(FrameworkContext userContext) {
        HOLDER.set(userContext);
    }

    public static FrameworkContext getSysUserContext() {
        return HOLDER.get();
    }

    public static void remove() {
        HOLDER.remove();
    }



}
