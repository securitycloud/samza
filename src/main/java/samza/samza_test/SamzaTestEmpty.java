/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package samza.samza_test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SamzaTestEmpty implements StreamTask, InitableTask {

    private int totalFlows = 0;
    private Map<String, String> countsEnd = new HashMap<>();
    private ObjectMapper mapper;
    private int windowSize;
    private int windowLimit;

    private Long start;
    private Long currentTime;

    private Config myConf;    
    
    @Override
    public void init(Config config, TaskContext context) {
	this.totalFlows = 0;
	this.myConf = config;
	this.windowSize = config.getInt("securitycloud.test.countWindow.batchSize");
        this.windowLimit = config.getInt("securitycloud.test.countWindow.limit");
        this.mapper = new ObjectMapper();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
	  totalFlows++;
  	//if(totalFlows == windowLimit){
		//  coordinator.shutdown(TaskCoordinator.RequestScope.CURRENT_TASK);
	  //}
        if (totalFlows == 1) {
		String testNumber = myConf.get("securitycloud.test.name");
        	start = System.currentTimeMillis();
		countsEnd.put("Log:", "zacatek zpracovani testu " + testNumber + ": " + start);
		
		try{
			byte[] myArray = mapper.writeValueAsBytes(countsEnd.toString());
			collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), myArray));
			myArray = mapper.writeValueAsBytes("empty " + String.valueOf(start) + " " + String.valueOf(0));
			collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-count-window"), myArray));
		} catch (Exception e) {
            		Logger.getLogger(SamzaTestEmpty.class.getName()).log(Level.SEVERE, null, e);
        	}
                countsEnd = new HashMap<>();
        }
    
        if (totalFlows % windowSize == 0) {
		currentTime = System.currentTimeMillis();
		String msg = "V case: " + currentTime + ", rychlost na tomto uzlu: " + windowSize/(currentTime - start) + "k toku za vterinu";
		start = currentTime;

		countsEnd.put("totalFlows", String.valueOf(totalFlows));
		countsEnd.put("Log:", msg);
		try{
			byte[] myArray = mapper.writeValueAsBytes(countsEnd.toString());
			collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), myArray));
			myArray = mapper.writeValueAsBytes("empty " + String.valueOf(currentTime) + " " + String.valueOf(windowSize));
			collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-count-window"), myArray));
		} catch (Exception e) {
            		Logger.getLogger(SamzaTestEmpty.class.getName()).log(Level.SEVERE, null, e);
        	}
                countsEnd = new HashMap<>();
        }

             
    }
}
