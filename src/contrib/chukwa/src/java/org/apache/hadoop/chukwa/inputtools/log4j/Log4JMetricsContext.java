/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.chukwa.inputtools.log4j;

import java.io.*;

import org.apache.hadoop.metrics.ContextFactory;
import org.apache.hadoop.metrics.MetricsException;
import org.apache.hadoop.metrics.spi.AbstractMetricsContext;
import org.apache.hadoop.metrics.spi.OutputRecord;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class Log4JMetricsContext extends AbstractMetricsContext {

  Logger out = null; //Logger.getLogger(Log4JMetricsContext.class);
  
  /* Configuration attribute names */
//  protected static final String FILE_NAME_PROPERTY = "fileName";
  protected static final String PERIOD_PROPERTY = "period";

    
  /** Creates a new instance of FileContext */
  public Log4JMetricsContext() {}
     
  public void init(String contextName, ContextFactory factory) {
    super.init(contextName, factory);
  /*      
    String fileName = getAttribute(FILE_NAME_PROPERTY);
    if (fileName != null) {
      file = new File(fileName);
    }
    */
    out = Logger.getLogger("chukwa.hadoop.metrics."+contextName);
    String periodStr = getAttribute(PERIOD_PROPERTY);
    if (periodStr != null) {
      int period = 0;
      try {
        period = Integer.parseInt(periodStr);
      } catch (NumberFormatException nfe) {
      }
      if (period <= 0) {
        throw new MetricsException("Invalid period: " + periodStr);
      }
      setPeriod(period);
    }
  }
  
  @Override
  protected void emitRecord(String contextName, String recordName, OutputRecord outRec)
      throws IOException
  {
	JSONObject json = new JSONObject();
    try {
		json.put("contextName", contextName);
		json.put("recordName", recordName);
		json.put("chukwa_timestamp", System.currentTimeMillis());
	    for (String tagName : outRec.getTagNames()) {
            json.put(tagName, outRec.getTag(tagName));
	    }
	    for (String metricName : outRec.getMetricNames()) {
	    	json.put(metricName, outRec.getMetric(metricName));
	    }
    } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}    
    out.info(json.toString());
  }

}
