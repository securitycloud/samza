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

//public class SamzaTestTask implements StreamTask, InitableTask, WindowableTask {
public class SamzaTestTask implements StreamTask, InitableTask {

    private int totalFlows = 0;
    private Map<String, Integer> counts = new HashMap<>();
    private Map<String, String> countsEnd = new HashMap<>();
    private ObjectMapper mapper;
    private int windowSize;

    //filters
    private int filtered = 0;
    private int foo = 0;


    private static final Logger log = Logger.getLogger(SamzaTestTask.class.getName());
    private static Handler fh;
    private Long start;
    private Long currentTime;
    private long bytes = 0;
    private long packets = 0;
    private long flows = 0;

	private Config myConf;
   // private KeyValueStore<String, Integer> store;

    
    
    @Override
    public void init(Config config, TaskContext context) {
	this.totalFlows = 0;
	this.myConf = config;
	this.mapper = new ObjectMapper();
	this.windowSize = config.getInt("securitycloud.test.countWindow.batchSize");
       // this.store = (KeyValueStore<String, Integer>) context.getStore("samza-store");
        try {
            fh = new FileHandler("/tmp/statsLog.txt");
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
	totalFlows++;
	if(totalFlows == 1_500_001){
		coordinator.shutdown(TaskCoordinator.RequestScope.CURRENT_TASK);
	}
        if (totalFlows == 1) {
		int testNumber = myConf.getInt("securitycloud.test.number");
        	start = System.currentTimeMillis();
		log.log(Level.INFO, "zacatek zpracovani: ",start);
		countsEnd.put("Log:", "zacatek zpracovani testu " + testNumber + ": " + start);
		
		try{
			byte[] myArray = mapper.writeValueAsBytes(countsEnd.toString());
			collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), myArray));
		} catch (Exception e) {
            		Logger.getLogger(SamzaTestTask.class.getName()).log(Level.SEVERE, null, e);
        	}
                countsEnd = new HashMap<>();
        }  

	//filterIP(envelope, collector, coordinator);
        count(envelope, collector, coordinator);  
    
        if (totalFlows % windowSize == 0) {
		currentTime = System.currentTimeMillis();
		String msg = new String("V case: " + currentTime + ", rychlost na tomto uzlu: " + windowSize/(currentTime - start) + "k toku za vterinu");
        	log.log(Level.INFO, msg);
		start = currentTime;

		countsEnd.put("totalFlows", String.valueOf(totalFlows));
 	        countsEnd.put("filtered", String.valueOf(filtered));
		countsEnd.put("bytes", String.valueOf(bytes));
		countsEnd.put("packtes", String.valueOf(packets));
		countsEnd.put("flows", String.valueOf(flows));
		countsEnd.put("Log:", msg);
		try{
			byte[] myArray = mapper.writeValueAsBytes(countsEnd.toString());
			collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), myArray));
			myArray = mapper.writeValueAsBytes(windowSize);
			collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-count-window"), myArray));
		} catch (Exception e) {
            		Logger.getLogger(SamzaTestTask.class.getName()).log(Level.SEVERE, null, e);
        	}
                countsEnd = new HashMap<>();
        }

             
/*
	int storedValue = 0;  
	try{
	   storedValue = store.get("totalFlows");
	   storedValue++;
     	   store.put("totalFlows", new Integer(storedValue));
	}catch (Exception e) {
		store.put("totalFlows", new Integer(1));
            Logger.getLogger(SamzaTestTask.class.getName()).log(Level.SEVERE, null, e);
        }
	if(totalFlows % 1_000_000 == 0){
		//countsEnd.put("fromDB", String.valueOf(storedValue));
		countsEnd.put("totalFlows", String.valueOf(totalFlows));
 	        countsEnd.put("filtered", String.valueOf(filtered));
		countsEnd.put("Mam milion", String.valueOf(System.currentTimeMillis()));
		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), countsEnd));
                countsEnd = new HashMap<>();
	}
*/
    }
/*
    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) {

        try{
 	     int storedValue = store.get("totalFlows");
             counts.put("fromDB", storedValue);
	}catch (Exception e) {
            Logger.getLogger(SamzaTestTask.class.getName()).log(Level.SEVERE, null, e);
        }
        counts.put("totalFlows", totalFlows);
        counts.put("filtered", filtered);

        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), counts));

        // Reset counts after windowing.
        //totalFlows = 0;
        filtered = 0;
        foo = 0;
        counts = new HashMap<>();
	if(totalFlows == 2593080){
		coordinator.shutdown(TaskCoordinator.RequestScope.CURRENT_TASK);
	}
    }
*/
    private void filterIP(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        try {
            Map<String, Object> input = (Map<String, Object>) envelope.getMessage();
            String srcIP = (String) input.get("src_ip_addr");
            String IPFilter = "194.164.3.172";
            if (srcIP.equals(IPFilter)) {
                filtered++;
                collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-filter"), envelope.getMessage()));
            }
        } catch (Exception e) {
            Logger.getLogger(SamzaTestTask.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void count(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
	try {
            Flow flow = mapper.readValue((byte[]) envelope.getMessage(), Flow.class);
            String dstIP = flow.getDst_ip_addr();
            String IPFilter = "62.148.241.49";
            if (dstIP.equals(IPFilter)) {
                filtered++;
                flows++;
                bytes = bytes + flow.getBytes();
                packets = packets + flow.getPackets();
                //collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "wikipedia-filter"), jsonObject));
            }
        } catch (Exception e) {
            Logger.getLogger(SamzaTestTask.class.getName()).log(Level.SEVERE, null, e);
        }
        /*try {
            Map<String, Object> input = (Map<String, Object>) envelope.getMessage();
            String srcIP = (String) input.get("src_ip_addr");
            String IPFilter = "194.164.3.172";
            if (srcIP.equals(IPFilter)) {
                filtered++;
                flows++;
                bytes = bytes + (int) input.get("bytes");
                packets = packets + (int) input.get("packets");
                //collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "wikipedia-filter"), jsonObject));
            }
        } catch (Exception e) {
            Logger.getLogger(SamzaTestTask.class.getName()).log(Level.SEVERE, null, e);
        }*/
    }

    private void aggregate(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {

    }

    private void topN(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
    /*    //top 10 src IP adress ordered by bytes
        String input = "";
        try {
            input = (String) envelope.getMessage();
            String srcIP = FlowParser.getSrcIP(input);
            if (top.containsKey(srcIP)) {
                FlowInfo record = new FlowInfo();
                record.setBytes(top.get(srcIP).getBytes() + FlowParser.getBytes(input));
                record.setPackets(top.get(srcIP).getPackets() + FlowParser.getPackets(input));
                record.setFlows(top.get(srcIP).getFlows() + 1);
                top.put(srcIP, record);
            } else {
                FlowInfo newRecord = new FlowInfo();
                newRecord.setBytes(FlowParser.getBytes(input));
                newRecord.setPackets(FlowParser.getPackets(input));
                newRecord.setFlows(1);
                top.put(srcIP, newRecord);
            }
        } catch (Exception e) {
            Logger.getLogger(SamzaTestTask.class.getName()).log(Level.SEVERE, null, e);
        }*/
    }
}
