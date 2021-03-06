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
package org.apache.camel.component.weather;

import java.net.URL;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherConsumer extends ScheduledPollConsumer {
    public static final long DEFAULT_CONSUMER_DELAY = 60 * 60 * 1000L;
    private static final transient Logger LOG = LoggerFactory.getLogger(WeatherConsumer.class);
    private final String query;

    public WeatherConsumer(WeatherEndpoint endpoint, Processor processor, String query) {
        super(endpoint, processor);
        this.query = query;
    }

    @Override
    protected int poll() throws Exception {
        LOG.debug("Going to execute the Weather query {}", query);
        String weather = getEndpoint().getCamelContext().getTypeConverter().mandatoryConvertTo(String.class, new URL(query));
        LOG.debug("Got back the Weather information {}", weather);
        if (ObjectHelper.isEmpty(weather)) {
            throw new IllegalStateException("Got the unexpected value '" + weather + "' as the result of the query '" + query + "'");
        }

        Exchange exchange = getEndpoint().createExchange();
        exchange.getIn().setBody(weather);
        exchange.getIn().setHeader(WeatherConstants.WEATHER_QUERY, query);
        getProcessor().process(exchange);

        return 1;
    }
}