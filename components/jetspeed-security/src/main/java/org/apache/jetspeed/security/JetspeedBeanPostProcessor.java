/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author <a href="mailto:vkumar@apache.org">Vivek Kumar</a>
 * @version $Id:
 */
public class JetspeedBeanPostProcessor implements ApplicationListener
{
    private ApplicationContext context;
    private List<String> beanList = null;

    /**
     * @param beanList
     */
    public JetspeedBeanPostProcessor(List<String> beanList)
    {
        this.beanList = beanList;
    }

    public void onApplicationEvent(ApplicationEvent appEvent)
    {
        if (appEvent instanceof ContextRefreshedEvent)
        {
            ContextRefreshedEvent event = (ContextRefreshedEvent) appEvent;
            this.context = event.getApplicationContext();
            processBeans();
        }
    }

    private void processBeans()
    {
        if (beanList != null)
        {
            JetspeedBeanInitializer intializer = null;
            for (String bean : beanList)
            {
                try
                {
                    intializer = (JetspeedBeanInitializer) context.getBean(bean);
                    intializer.intialize();
                }
                catch (Exception e)
                {
                    // Log error messages
                    e.printStackTrace();
                }
            }
        }
    }
}
