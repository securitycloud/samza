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
import java.util.TreeMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Comparator;
import java.lang.StringBuilder;
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
    private Map<String, Integer> top;
    private ObjectMapper mapper;
    private Config conf;
    private int windowLimit;
    private int packets;
    private int filtered;
    private long timeStart;
    private long timeEnd;
   
    private static final Logger log = Logger.getLogger(SamzaCountWindow.class.getName());
    private static Handler fh;
    
    @Override
    public void init(Config config, TaskContext context) {
	this.totalFlows = 0;
	this.packets = 0;
	this.filtered = 0;
	this.timeStart = Long.MAX_VALUE;
	this.timeEnd = 0;
	this.mapper = new ObjectMapper();
	this.conf = config;
	this.top = new HashMap<>();
	this.windowLimit = config.getInt("securitycloud.test.windowLimit");
        try {
            fh = new FileHandler("/tmp/statsLog.txt");
            Logger.getLogger("").addHandler(fh);
            log.addHandler(fh);
            log.setLevel(Level.INFO);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(SamzaCountWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
	try {
		//collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), envelope.getMessage()));
		//String zprava = "Jeste porad funguju a dostal jsem zpravu. Aktualni stav totalFlows: "+String.valueOf(totalFlows)+".";
		//collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), mapper.writeValueAsBytes(zprava)));
		String input = mapper.readValue((byte[]) envelope.getMessage(), String.class);
		log.log(Level.INFO, "Prichozi zprava: "+input);
		String[] parts = input.split(" ");

		//long timestamp = Long.parseLong(parts[1]);
    long timestamp = System.currentTimeMillis();
		if(timestamp < timeStart){
			timeStart = timestamp;
		}
		if(timestamp > timeEnd){
			timeEnd = timestamp;
		}

		if(parts[0].equals("count")){
			totalFlows += Integer.parseInt(parts[2]);
			packets += Integer.parseInt(parts[3]);
			String IP = parts[4];
			if(totalFlows == windowLimit){
				long speed = windowLimit/(timeEnd-timeStart); //rychlost v tocich za milisekundu = prumer v tisicich toku za vterinu
				String msg = "CountWindow se dopocital na hodnotu "+String.valueOf(windowLimit)+" toku :), IP adresa " + IP + " mela " + String.valueOf(packets) +" paketu. Prumerna rychlost zpracovani byla "+String.valueOf(speed)+"k toku za vterinu";
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), mapper.writeValueAsBytes(msg)));
				cleanVars();
			}
			if(totalFlows > windowLimit){
				String msg = "CountWindow ma hodnotu vetsi nez 4_500_000 toku WTF?, IP adresa " + IP + " mela " + String.valueOf(packets) +" paketu.";
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), mapper.writeValueAsBytes(msg)));
				cleanVars();
			}					
		}

		if(parts[0].equals("filter")){
			totalFlows += Integer.parseInt(parts[2]);
			filtered += Integer.parseInt(parts[3]);
			String IP = parts[4];
			if(totalFlows == windowLimit){
				long speed = windowLimit/(timeEnd-timeStart); //rychlost v tocich za milisekundu = prumer v tisicich toku za vterinu
				String msg = "CountWindow se dopocital na hodnotu "+String.valueOf(windowLimit)+" toku :), IP adresa " + IP + " mela " + String.valueOf(filtered) +" toku. Prumerna rychlost zpracovani byla "+String.valueOf(speed)+"k toku za vterinu";
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), mapper.writeValueAsBytes(msg)));
				cleanVars();
			}
			if(totalFlows > windowLimit){
				String msg = "CountWindow ma hodnotu vetsi nez 4_500_000 toku WTF?, IP adresa " + IP + " mela " + String.valueOf(filtered) +" toku.";
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), mapper.writeValueAsBytes(msg)));
				cleanVars();
			}					
		}

		if(parts[0].equals("aggregate")){
			totalFlows += Integer.parseInt(parts[2]);
			for(String field : parts){
				String[] divided = field.split("=");				
				if(divided.length > 1){
					String dstIP = divided[0];
					int packetsCount = Integer.parseInt(divided[1].substring(0, divided[1].length()-1));
   				        if (top.containsKey(dstIP)) {
   				             int packetsFromMap = top.get(dstIP);
   				             top.put(dstIP, packetsFromMap + packetsCount);
    				        } else {
       				             top.put(dstIP, packetsCount);
          				}
				}
			}
			if(totalFlows == windowLimit){				
				Iterator<String> it = top.keySet().iterator();
				StringBuilder sb = new StringBuilder();
				while(it.hasNext()){
					String key = it.next();
					sb.append(key+" "+String.valueOf(top.get(key))+", ");
				}				

				long speed = windowLimit/(timeEnd-timeStart); //rychlost v tocich za milisekundu = prumer v tisicich toku za vterinu
				//String msg = "CountWindow se dopocital na hodnotu "+String.valueOf(windowLimit)+" toku :). Prumerna rychlost zpracovani byla "+String.valueOf(speed)+"k toku za vterinu. Vypis agregace: "+sb.toString();
				String msg = "CountWindow se dopocital na hodnotu "+String.valueOf(windowLimit)+" toku :). Prumerna rychlost zpracovani byla "+String.valueOf(speed)+"k toku za vterinu. Vypis agregace: ne v testovacim rezimu, pro IP 62.148.241.49 je paketu:" + String.valueOf(top.get("62.148.241.49"));
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), mapper.writeValueAsBytes(msg)));
				cleanVars();
			}
			if(totalFlows > windowLimit){
				String msg = "CountWindow ma hodnotu vetsi nez 4_500_000 toku WTF?";
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), mapper.writeValueAsBytes(msg)));
				cleanVars();
			}					
		}

		if(parts[0].equals("topn")){
			totalFlows += Integer.parseInt(parts[2]);
			for(String field : parts){
				String[] divided = field.split("=");				
				if(divided.length > 1){
					String dstIP = divided[0];
					int packetsCount = Integer.parseInt(divided[1].substring(0, divided[1].length()-1));
   				        if (top.containsKey(dstIP)) {
   				             int packetsFromMap = top.get(dstIP);
   				             top.put(dstIP, packetsFromMap + packetsCount);
    				        } else {
       				             top.put(dstIP, packetsCount);
          				}
				}
			}
			if(totalFlows == windowLimit){
				ValueComparator bvc = new ValueComparator(top);
				TreeMap<String, Integer> sorted = new TreeMap<>(bvc);
				sorted.putAll(top);
				Iterator<String> it = sorted.keySet().iterator();
				int i = 1;
				StringBuilder sb = new StringBuilder();
				while(it.hasNext()){
					String key = it.next();
					sb.append(String.valueOf(i)+" "+key+" "+String.valueOf(top.get(key))+", ");
					i++;
					if(i>10) break;
					
				}				

				long speed = windowLimit/(timeEnd-timeStart); //rychlost v tocich za milisekundu = prumer v tisicich toku za vterinu
				String msg = "CountWindow se dopocital na hodnotu "+String.valueOf(windowLimit)+" toku :). Prumerna rychlost zpracovani byla "+String.valueOf(speed)+"k toku za vterinu. Vypis TOP 10: "+sb.toString();
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), mapper.writeValueAsBytes(msg)));
				cleanVars();
			}
			if(totalFlows > windowLimit){
				String msg = "CountWindow ma hodnotu vetsi nez 4_500_000 toku WTF?";
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), mapper.writeValueAsBytes(msg)));
				cleanVars();
			}					
		}

		if(parts[0].equals("scan")){
			totalFlows += Integer.parseInt(parts[2]);
			for(String field : parts){
				String[] divided = field.split("=");				
				if(divided.length > 1){
					String dstIP = divided[0];
					int packetsCount = Integer.parseInt(divided[1].substring(0, divided[1].length()-1));
   				        if (top.containsKey(dstIP)) {
   				             int packetsFromMap = top.get(dstIP);
   				             top.put(dstIP, packetsFromMap + packetsCount);
    				        } else {
       				             top.put(dstIP, packetsCount);
          				}
				}
			}
			if(totalFlows == windowLimit){
				ValueComparator bvc = new ValueComparator(top);
				TreeMap<String, Integer> sorted = new TreeMap<>(bvc);
				sorted.putAll(top);
				Iterator<String> it = sorted.keySet().iterator();
				int i = 1;
				StringBuilder sb = new StringBuilder();
				while(it.hasNext()){
					String key = it.next();
					sb.append(String.valueOf(i)+" "+key+" "+String.valueOf(top.get(key))+", ");
					i++;
					if(i>10) break;
					
				}				

				long speed = windowLimit/(timeEnd-timeStart); //rychlost v tocich za milisekundu = prumer v tisicich toku za vterinu
				String msg = "CountWindow se dopocital na hodnotu "+String.valueOf(windowLimit)+" toku :). Prumerna rychlost zpracovani byla "+String.valueOf(speed)+"k toku za vterinu. Vypis TOP 10: "+sb.toString();
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), mapper.writeValueAsBytes(msg)));
				cleanVars();
			}
			if(totalFlows > windowLimit){
				String msg = "CountWindow ma hodnotu vetsi nez 4_500_000 toku WTF?";
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), mapper.writeValueAsBytes(msg)));
				cleanVars();
			}					
		}

		
	} catch (Exception e) {
            Logger.getLogger(SamzaCountWindow.class.getName()).log(Level.SEVERE, null, e);
        }
    }

  private void cleanVars(){
	totalFlows = 0;
	packets = 0;
	filtered = 0;
	top = new HashMap<>();
	this.timeStart = Long.MAX_VALUE;
	this.timeEnd = 0;
  }
}

class ValueComparator implements Comparator<String>{
	Map<String, Integer> base;
	public ValueComparator(Map<String, Integer> base){
		this.base = base;
	}
	public int compare(String a, String b){
		if(base.get(a) >= base.get(b)){
			return -1;
		} else {
			return 1;
		}
	}
}



















