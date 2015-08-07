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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
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
import org.apache.samza.task.WindowableTask;
import org.apache.samza.storage.kv.KeyValueStore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SamzaCountWindow implements StreamTask, InitableTask {

    private int totalFlows;
    private Map<String, String> countsEnd = new HashMap<>();
    private ObjectMapper mapper;
    private Config conf;
    private int windowLimit;
    private int packets;
   
    
    @Override
    public void init(Config config, TaskContext context) {
	this.totalFlows = 0;
	this.packets = 0;
	this.mapper = new ObjectMapper();
	this.conf = config;
	this.windowLimit = config.getInt("securitycloud.test.windowLimit");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
	try {
		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), envelope.getMessage()));
		String input = mapper.readValue((byte[]) envelope.getMessage(), String.class);
		String[] parts = input.split(" ");
		totalFlows += Integer.parseInt(parts[0]);
		packets += Integer.parseInt(parts[1]);
		String IP = parts[2];
		if(totalFlows == windowLimit){
			String msg = "CountWindow se dopocital na hodnotu 4_500_000 toku :), IP adresa " + IP + " mela " + String.valueOf(packets) +" paketu.";
			collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), mapper.writeValueAsBytes(msg)));
			totalFlows = 0;
			packets = 0;
		}
		if(totalFlows > windowLimit){
			String msg = "CountWindow ma hodnotu vetsi nez 4_500_000 toku WTF?, IP adresa " + IP + " mela " + String.valueOf(packets) +" paketu.";
			collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), mapper.writeValueAsBytes(msg)));
			totalFlows = 0;
			packets = 0;
		}
	} catch (Exception e) {
            Logger.getLogger(SamzaCountWindow.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
