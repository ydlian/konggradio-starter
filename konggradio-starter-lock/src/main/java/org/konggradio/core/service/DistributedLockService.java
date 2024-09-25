/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: DistributedLockService.java
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

package org.konggradio.core.service;

import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.annotation.Lock4j;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DistributedLockService {

	@Autowired
	private LockTemplate lockTemplate;


	@Lock4j
	public void simple() {
		//do something
	}


	@Lock4j(keys = {"#user.id", "#user.name"}, expire = 60000, acquireTimeout = 1000)
	public User customMethod(User user) {
		return user;
	}

	@Lock4j(keys = "testLock", expire = 60000)
	public String testLock(int status) {
		if(status==1){
			try {
				log.info("doing....");
				Thread.sleep(8000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return "ok";
	}

	public String maticLock(int status) {

		final LockInfo lockInfo = lockTemplate.lock("maticLock", 30000L, 2000L);
		if (null == lockInfo) {
			throw new RuntimeException("业务处理中,请稍后再试");
		}

		try {
			log.info("doing....");
			Thread.sleep(8000);
			//System.out.println("执行简单方法1 , 当前线程:" + Thread.currentThread().getName() + " , counter：" + (counter++));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lockTemplate.releaseLock(lockInfo);
		}
		return "ok";

	}
}
