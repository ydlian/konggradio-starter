/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ServerDTO.java
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

package org.konggradio.hardware.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;

import java.util.List;

@Data
@Builder
@Accessors(chain = true)
public class ServerDTO {
	private List<String> macAdders;
	private ComputerSystem computerSystem;
	private CentralProcessor centralProcessor;
	private GlobalMemory globalMemory;
	private HWDiskStore[] hwDiskStores;
}
