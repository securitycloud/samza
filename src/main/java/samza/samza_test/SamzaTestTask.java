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
import org.apache.samza.storage.kv.KeyValueStore;
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
    private KeyValueStore<String, Integer> store;

    //filters
    private int filtered = 0;

    //counts
    private Long all = 0l;
    private Long flows = 0l;
    private Long bytes = 0l;
    private Long packets = 0l;

    private static final Logger log = Logger.getLogger(FileReaderConsumer.class.getName());
    private static Handler fh;
    private Long start;

    //topN
    Map<String, FlowInfo> top = new HashMap<>();
    
    
    public void init(Config config, TaskContext context) {
        this.store = (KeyValueStore<String, Integer>) context.getStore("samza-stats");
        try {
            fh = new FileHandler("/tmp/statsLog.xml");
            Logger.getLogger("").addHandler(fh);
            log.addHandler(fh);
            log.setLevel(Level.INFO);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(FileReaderConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        totalFlows += 1;
        if (all == 0l) {
            start = System.currentTimeMillis();
        }
        all++;
        if (all == 1_000_000l) {
            log.log(Level.INFO, "rychlost: {0} toků/s", (1_000_000l*1000) / (System.currentTimeMillis() - start));
            all = 0l;
        }
        
        filterIP(envelope, collector, coordinator);
//        filterPort(envelope, collector, coordinator);
//        filterMultiple(envelope, collector, coordinator);
//        count(envelope, collector, coordinator);
//        topN(envelope, collector, coordinator);

    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) {
//        List<Map.Entry<String, FlowInfo>> list = new LinkedList<>(top.entrySet());
//        list.sort(new Comparator<Map.Entry<String, FlowInfo>>() {
//
//            @Override
//            public int compare(Map.Entry<String, FlowInfo> o1, Map.Entry<String, FlowInfo> o2) {
//                return (int) (o2.getValue().getBytes() - o1.getValue().getBytes());
//            }
//        });
//
//        StringBuilder str = new StringBuilder();
//        if (list.size() >= 11) {
//            for (int i = 0; i < 10; i++) {
//                str.append(list.get(i).toString());
//                str.append("                  ");
//            }
//        } else {
//            str.append("too few flows");
//        }
//            
//        counts.put(str.toString(), totalFlows);
        counts.put("totalFlows", totalFlows);
        counts.put("filtered", filtered);
//        counts.put("flows", flows.intValue());
//        counts.put("bytes", bytes.intValue());
//        counts.put("packets", packets.intValue());

        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-stats"), counts));

        // Reset counts after windowing.
        totalFlows = 0;
        filtered = 0;
        packets = 0l;
        flows = 0l;
        bytes = 0l;
        counts = new HashMap<String, Integer>();
//        top.clear();
    }

    private void filterMultiple(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        try {
            String input = (String) envelope.getMessage();
            String srcIP = FlowParser.getSrcIP(input);
            String IPFilter = "194.164.3.172";
            if (srcIP.equals(IPFilter)) {
                Integer srcPort = FlowParser.getSrcPort(input);
                Integer srcPortFilter = new Integer(80);
                if (srcPort.equals(srcPortFilter)) {
                    String proto = FlowParser.getProtocol(input);
                    String protoFilter = "UDP";
                    if (proto.equals(protoFilter)) {
                        filtered++;
                        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-filter"), input));
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(SamzaTestTask.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void filterIP(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        try {
            String input = (String) envelope.getMessage();
            String srcIP = FlowParser.getSrcIP(input);
            String IPFilter = "194.164.3.172";
            if (srcIP.equals(IPFilter)) {
                filtered++;
                collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-filter"), input));
            }
        } catch (Exception e) {
            Logger.getLogger(SamzaTestTask.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void filterPort(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        try {
            String input = (String) envelope.getMessage();
            Integer srcPort = FlowParser.getSrcPort(input);
            Integer srcPortFilter = new Integer(80);
            if (srcPort.equals(srcPortFilter)) {
                filtered++;
                collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-filter"), input));
            }
        } catch (Exception e) {
            Logger.getLogger(SamzaTestTask.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void filterProto(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        try {
            String input = (String) envelope.getMessage();
            String proto = FlowParser.getProtocol(input);
            String protoFilter = "UDP";
            if (proto.equals(protoFilter)) {
                filtered++;
                collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-filter"), input));
            }
        } catch (Exception e) {
            Logger.getLogger(SamzaTestTask.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void count(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        try {
            String input = (String) envelope.getMessage();
            String srcIP = FlowParser.getSrcIP(input);
            String IPFilter = "194.164.3.172";
            if (srcIP.equals(IPFilter)) {
                filtered++;
                flows++;
                bytes = bytes + FlowParser.getBytes(input);
                packets = packets + FlowParser.getPackets(input);
                //collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "samza-filter"), jsonObject));
            }
        } catch (Exception e) {
            Logger.getLogger(SamzaTestTask.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void aggregate(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {

    }

    private void topN(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        //top 10 src IP adress ordered by bytes
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
        }
    }
}
