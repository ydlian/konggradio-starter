/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioEventRegisterImpl.java
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

package org.konggradio.core.boot.event.handle.impl;


import org.konggradio.core.boot.event.handle.KongGradioEventInterface;
import org.konggradio.core.boot.event.handle.KongGradioEventRegisterInterface;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("KongGradioEventRegisterImpl")
public class KongGradioEventRegisterImpl implements KongGradioEventRegisterInterface {
    private Map<String, KongGradioEventInterface> serviceImplMap = new HashMap<>();
    private ApplicationContext applicationContext;

    // 获取spring的上下文
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // 获取接口实现类的所有bean，并按自己定的规则放入map中
    @Override
    public void afterPropertiesSet() {
        Map<String, KongGradioEventInterface> beanMap = applicationContext.getBeansOfType(KongGradioEventInterface.class);
        // 以下代码是将bean按照自己定的规则放入map中，这里我的规则是key：service.toString();value:bean
        // 调用时，参数传入service.toString()的具体字符串就能获取到相应的bean
        // 此处也可以不做以下的操作，直接使用beanMap,在调用时，传入bean的名称
        for (KongGradioEventInterface serviceImpl : beanMap.values()) {
            serviceImplMap.put(serviceImpl.getCode(), serviceImpl);
        }
    }

    public KongGradioEventInterface getServiceImpl(String name) {
        return serviceImplMap.get(name);
    }

}
