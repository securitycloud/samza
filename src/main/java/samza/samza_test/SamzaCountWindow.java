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
    private long startLast;
    private int startLastFlows;

    @Override
    public void init(Config config, TaskContext context) {
        this.totalFlows = 0;
        this.packets = 0;
        this.startLast = 0;
        this.startLastFlows = 0;
        this.filtered = 0;
        this.timeStart = Long.MAX_VALUE;
        this.timeEnd = 0;
        this.mapper = new ObjectMapper();
        this.conf = config;
        this.top = new HashMap<>();
        this.windowLimit = config.getInt("securitycloud.test.windowLimit");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        try {
            String input = mapper.readValue((byte[]) envelope.getMessage(), String.class);
            String[] parts = input.split(" ");

            long timestamp = Long.parseLong(parts[1]);
            //long timestamp = System.currentTimeMillis();
            if (timestamp < timeStart) {
                timeStart = timestamp;
            }
            if (timestamp > timeEnd) {
                timeEnd = timestamp;
            }

            if (Integer.parseInt(parts[2]) == 0) {
                startLast = timestamp;
                startLastFlows = totalFlows;
            }

////////////////////////////////////////          EMPTY FRAMEWORK          //////////////////////////////////////// 
            if (parts[0].equals("empty")) {
                totalFlows += Integer.parseInt(parts[2]);
                if (totalFlows == windowLimit) {
                    long postProcessingTime = System.currentTimeMillis();
                    if (timeEnd < postProcessingTime) {
                        timeEnd = postProcessingTime;
                    }
                    long speed = windowLimit / (timeEnd - timeStart); //rychlost v tocich za milisekundu = prumer v tisicich toku za vterinu
                    String msg = "CountWindow se dopocital na hodnotu " + String.valueOf(windowLimit) + " toku :), prumerna rychlost zpracovani byla " + String.valueOf(speed) + "k toku za vterinu";
                    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "out"), mapper.writeValueAsBytes(msg)));
                    cleanVars();
                }
                if (totalFlows > windowLimit) {
                    String msg = "Chyba zpracovani, soucet toku nesedi do count okna!";
                    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "out"), mapper.writeValueAsBytes(msg)));
                    cleanVars();
                }
            }

////////////////////////////////////////          TEST FILTER          //////////////////////////////////////// 
            if (parts[0].equals("filter")) {
                totalFlows += Integer.parseInt(parts[2]);
                filtered += Integer.parseInt(parts[3]);
                String IP = parts[4];
                if (totalFlows == windowLimit) {
                    long postProcessingTime = System.currentTimeMillis();
                    if (timeEnd < postProcessingTime) {
                        timeEnd = postProcessingTime;
                    }
                    long speed = windowLimit / (timeEnd - timeStart); //rychlost v tocich za milisekundu = prumer v tisicich toku za vterinu
                    String msg = "CountWindow se dopocital na hodnotu " + String.valueOf(windowLimit) + " toku :), IP adresa " + IP + " mela " + String.valueOf(filtered) + " toku. Prumerna rychlost zpracovani byla " + String.valueOf(speed) + "k toku za vterinu";
                    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "out"), mapper.writeValueAsBytes(msg)));
                    cleanVars();
                }
                if (totalFlows > windowLimit) {
                    String msg = "Chyba zpracovani, soucet toku nesedi do count okna!";
                    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "out"), mapper.writeValueAsBytes(msg)));
                    cleanVars();
                }
            }

////////////////////////////////////////          TEST COUNT          //////////////////////////////////////// 
            if (parts[0].equals("count")) {
                totalFlows += Integer.parseInt(parts[2]);
                packets += Integer.parseInt(parts[3]);
                String IP = parts[4];
                if (totalFlows == windowLimit) {
                    long postProcessingTime = System.currentTimeMillis();
                    if (timeEnd < postProcessingTime) {
                        timeEnd = postProcessingTime;
                    }
                    long speed = windowLimit / (timeEnd - timeStart); //rychlost v tocich za milisekundu = prumer v tisicich toku za vterinu
                    String msg = "CountWindow se dopocital na hodnotu " + String.valueOf(windowLimit) + " toku :), IP adresa " + IP + " mela " + String.valueOf(packets) + " paketu. Prumerna rychlost zpracovani byla " + String.valueOf(speed) + "k toku za vterinu";
                    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "out"), mapper.writeValueAsBytes(msg)));
                    //speed = (windowLimit-startLastFlows)/(timeEnd-startLast);
                    //msg = "Mereni od startu posledniho: , IP adresa " + IP + " mela " + String.valueOf(packets) +" paketu. Prumerna rychlost zpracovani byla "+String.valueOf(speed)+"k toku za vterinu";
                    //collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), mapper.writeValueAsBytes(msg)));
                    cleanVars();
                }
                if (totalFlows > windowLimit) {
                    String msg = "Chyba zpracovani, soucet toku nesedi do count okna!";
                    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "out"), mapper.writeValueAsBytes(msg)));
                    cleanVars();
                }
            }

////////////////////////////////////////          TEST AGGREGATE          //////////////////////////////////////// 
            if (parts[0].equals("aggregate")) {
                totalFlows += Integer.parseInt(parts[2]);
                for (String field : parts) {
                    String[] divided = field.split("=");
                    if (divided.length > 1) {
                        String IP = divided[0];
                        if (IP.charAt(0) == '{') {
                            IP = IP.substring(1);
                        }
                        int packetsCount = Integer.parseInt(divided[1].substring(0, divided[1].length() - 1));
                        if (top.containsKey(IP)) {
                            int packetsFromMap = top.get(IP);
                            top.put(IP, packetsFromMap + packetsCount);
                        } else {
                            top.put(IP, packetsCount);
                        }
                    }
                }
                if (totalFlows == windowLimit) {
                    Iterator<String> it = top.keySet().iterator();
                    StringBuilder sb = new StringBuilder();
                    while (it.hasNext()) {
                        String key = it.next();
                        sb.append(key).append(" ").append(String.valueOf(top.get(key))).append(", ");
                    }

                    long postProcessingTime = System.currentTimeMillis();
                    if (timeEnd < postProcessingTime) {
                        timeEnd = postProcessingTime;
                    }
                    long speed = windowLimit / (timeEnd - timeStart); //rychlost v tocich za milisekundu = prumer v tisicich toku za vterinu
                    //String msg = "CountWindow se dopocital na hodnotu "+String.valueOf(windowLimit)+" toku :). Prumerna rychlost zpracovani byla "+String.valueOf(speed)+"k toku za vterinu. Vypis agregace: "+sb.toString();
                    String msg = "CountWindow se dopocital na hodnotu " + String.valueOf(windowLimit) + " toku :). Prumerna rychlost zpracovani byla " + String.valueOf(speed) + "k toku za vterinu. Vypis agregace: ne v testovacim rezimu, pro IP 141.57.244.116 je paketu:" + String.valueOf(top.get("141.57.244.116"));
                    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "out"), mapper.writeValueAsBytes(msg)));
                    cleanVars();
                }
                if (totalFlows > windowLimit) {
                    String msg = "Chyba zpracovani, soucet toku nesedi do count okna!";
                    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "out"), mapper.writeValueAsBytes(msg)));
                    cleanVars();
                }
            }

////////////////////////////////////////          TEST TOP N          //////////////////////////////////////// 
            if (parts[0].equals("topn")) {
                totalFlows += Integer.parseInt(parts[2]);
                for (String field : parts) {
                    String[] divided = field.split("=");
                    if (divided.length > 1) {
                        String IP = divided[0];
                        if (IP.charAt(0) == '{') {
                            IP = IP.substring(1);
                        }
                        int packetsCount = Integer.parseInt(divided[1].substring(0, divided[1].length() - 1));
                        if (top.containsKey(IP)) {
                            int packetsFromMap = top.get(IP);
                            top.put(IP, packetsFromMap + packetsCount);
                        } else {
                            top.put(IP, packetsCount);
                        }
                    }
                }
                if (totalFlows == windowLimit) {
                    ValueComparator bvc = new ValueComparator(top);
                    TreeMap<String, Integer> sorted = new TreeMap<>(bvc);
                    sorted.putAll(top);
                    Iterator<String> it = sorted.keySet().iterator();
                    int i = 1;
                    StringBuilder sb = new StringBuilder();
                    while (it.hasNext()) {
                        String key = it.next();
                        sb.append(String.valueOf(i)).append(" ").append(key).append(" ").append(String.valueOf(top.get(key))).append(", ");
                        i++;
                        if (i > 10) {
                            break;
                        }

                    }

                    long postProcessingTime = System.currentTimeMillis();
                    if (timeEnd < postProcessingTime) {
                        timeEnd = postProcessingTime;
                    }
                    long speed = windowLimit / (timeEnd - timeStart); //rychlost v tocich za milisekundu = prumer v tisicich toku za vterinu
                    String msg = "CountWindow se dopocital na hodnotu " + String.valueOf(windowLimit) + " toku :). Prumerna rychlost zpracovani byla " + String.valueOf(speed) + "k toku za vterinu. Vypis TOP 10: " + sb.toString();
                    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "out"), mapper.writeValueAsBytes(msg)));
                    cleanVars();
                }
                if (totalFlows > windowLimit) {
                    String msg = "Chyba zpracovani, soucet toku nesedi do count okna!";
                    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "out"), mapper.writeValueAsBytes(msg)));
                    cleanVars();
                }
            }

////////////////////////////////////////          TEST SYN SCAN          //////////////////////////////////////// 
            if (parts[0].equals("scan")) {
                totalFlows += Integer.parseInt(parts[2]);
                for (String field : parts) {
                    String[] divided = field.split("=");
                    if (divided.length > 1) {
                        String IP = divided[0];
                        if (IP.charAt(0) == '{') {
                            IP = IP.substring(1);
                        }
                        int packetsCount = Integer.parseInt(divided[1].substring(0, divided[1].length() - 1));
                        if (top.containsKey(IP)) {
                            int packetsFromMap = top.get(IP);
                            top.put(IP, packetsFromMap + packetsCount);
                        } else {
                            top.put(IP, packetsCount);
                        }
                    }
                }
                if (totalFlows == windowLimit) {
                    ValueComparator bvc = new ValueComparator(top);
                    TreeMap<String, Integer> sorted = new TreeMap<>(bvc);
                    sorted.putAll(top);
                    Iterator<String> it = sorted.keySet().iterator();
                    int i = 1;
                    StringBuilder sb = new StringBuilder();
                    while (it.hasNext()) {
                        String key = it.next();
                        sb.append(String.valueOf(i)).append(" ").append(key).append(" ").append(String.valueOf(top.get(key))).append(", ");
                        i++;
                        if (i > 100) {
                            break;
                        }

                    }

                    long postProcessingTime = System.currentTimeMillis();
                    if (timeEnd < postProcessingTime) {
                        timeEnd = postProcessingTime;
                    }
                    long speed = windowLimit / (timeEnd - timeStart); //rychlost v tocich za milisekundu = prumer v tisicich toku za vterinu
                    String msg = "CountWindow se dopocital na hodnotu " + String.valueOf(windowLimit) + " toku :). Prumerna rychlost zpracovani byla " + String.valueOf(speed) + "k toku za vterinu. Vypis TOP 10: " + sb.toString();
                    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "out"), mapper.writeValueAsBytes(msg)));
                    cleanVars();
                }
                if (totalFlows > windowLimit) {
                    String msg = "Chyba zpracovani, soucet toku nesedi do count okna!";
                    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "out"), mapper.writeValueAsBytes(msg)));
                    cleanVars();
                }
            }

        } catch (IOException | NumberFormatException e) {
            Logger.getLogger(SamzaCountWindow.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void cleanVars() {
        totalFlows = 0;
        packets = 0;
        filtered = 0;
        startLastFlows = 0;
        startLast = 0;
        top = new HashMap<>();
        this.timeStart = Long.MAX_VALUE;
        this.timeEnd = 0;
    }
}

class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;

    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    @Override
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }
}
