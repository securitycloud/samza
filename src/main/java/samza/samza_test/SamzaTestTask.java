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

public class SamzaTestTask implements StreamTask, InitableTask, WindowableTask {

    private int totalFlows = 0;
    private Map<String, Integer> counts = new HashMap<>();

    //filters
    private int filtered = 0;


    private static final Logger log = Logger.getLogger(SamzaTestTask.class.getName());
    private static Handler fh;
    private Long start;
    private int counter;

    
    
    @Override
    public void init(Config config, TaskContext context) {
        try {
            fh = new FileHandler("/tmp/statsLog.xml");
            Logger.getLogger("").addHandler(fh);
            log.addHandler(fh);
            log.setLevel(Level.INFO);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(SamzaTestTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        totalFlows += 1;
        if (counter == 0) {
            start = System.currentTimeMillis();
        }
        counter++;
        if (counter == 1_000_000) {
            log.log(Level.INFO, "rychlost: {0} toků/s", (1_000_000l*1000) / (System.currentTimeMillis() - start));
            counter = 0;
        }
        
        filterIP(envelope, collector, coordinator);

    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) {
        counts.put("totalFlows", totalFlows);
        counts.put("filtered", filtered);

        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), counts));

        // Reset counts after windowing.
        totalFlows = 0;
        filtered = 0;
        counts = new HashMap<>();
    }

    private void filterIP(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        try {
           // String input = (String) envelope.getMessage();
//            String srcIP = FlowParser.getSrcIP(input);
//            String IPFilter = "194.164.3.172";
//            if (srcIP.equals(IPFilter)) {
            if((totalFlows % 3) == 0){
                filtered++;
                collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-filter"), envelope.getMessage()));
            }
        } catch (Exception e) {
            Logger.getLogger(SamzaTestTask.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
