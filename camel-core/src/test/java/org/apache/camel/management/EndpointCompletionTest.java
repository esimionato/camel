/**
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
package org.apache.camel.management;

import java.util.HashMap;
import java.util.List;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointCompletionTest extends ManagementTestSupport {
    private static final transient Logger LOG = LoggerFactory.getLogger(EndpointCompletionTest.class);

    @SuppressWarnings("unchecked")
    public void testEndpointCompletion() throws Exception {
        MBeanServer mbeanServer = getMBeanServer();
        ObjectName on = ObjectName.getInstance(
                "org.apache.camel:context=localhost/camel-1,type=context,name=\"camel-1\"");
        assertNotNull(on);
        mbeanServer.isRegistered(on);

        String componentName = "file";
        HashMap<String, Object> properties = new HashMap<String, Object>();
        List<String> completions = assertCompletion(mbeanServer, on, componentName, properties, "");
        completions = assertCompletion(mbeanServer, on, componentName, properties, "po");
        completions = assertCompletion(mbeanServer, on, componentName, properties, "/");
        completions = assertCompletion(mbeanServer, on, componentName, properties, "/usr/local");
        completions = assertCompletion(mbeanServer, on, componentName, properties, "/usr/local/");
        completions = assertCompletion(mbeanServer, on, componentName, properties, "/usr/local/b");
    }

    public void testEndpointConfigurationJson() throws Exception {
        MBeanServer mbeanServer = getMBeanServer();
        ObjectName on = ObjectName.getInstance(
                "org.apache.camel:context=localhost/camel-1,type=context,name=\"camel-1\"");
        assertNotNull(on);
        mbeanServer.isRegistered(on);

        assertParameterJsonSchema(mbeanServer, on, "bean");
        assertParameterJsonSchema(mbeanServer, on, "timer");
    }

    private List<String> assertCompletion(MBeanServer mbeanServer, ObjectName on, String componentName,
                                          HashMap<String, Object> properties, String completionText) throws Exception {
        Object[] params = {componentName, properties, completionText};
        String[] signature = {"java.lang.String",  "java.util.Map",  "java.lang.String"};

        List completions = assertIsInstanceOf(List.class,
                mbeanServer.invoke(on, "completeEndpointPath", params, signature));

        LOG.info("Component " + componentName + " with '" + completionText + "' Returned: " + completions);
        return completions;
    }

    private String assertParameterJsonSchema(MBeanServer mbeanServer, ObjectName on, String componentName) throws Exception {
        Object[] params = {componentName};
        String[] signature = {"java.lang.String"};

        String answer = assertIsInstanceOf(String.class,
                mbeanServer.invoke(on, "componentParameterJsonSchema", params, signature));

        LOG.info("Component " + componentName + " returned JSON: " + answer);
        return answer;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // noop
            }
        };
    }

}
