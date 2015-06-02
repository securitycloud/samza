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

/**
 *
 * @author dazle
 */
public class FlowParser {
    public static String getDateFirstSeen(String flow){
        String[] parts = flow.split(",");
        return parts[0].substring(20, parts[0].length()-1);
    }
    
    public static Double getDuration(String flow){
        return Double.parseDouble(flow.split(",")[1].substring(11));
    }
    
    public static String getSrcIP(String flow){
        String[] parts = flow.split(",");
        String srcIP = parts[2].substring(15, parts[2].length()-1);
        return srcIP;
    }
    
    public static String getDstIP(String flow){
        String[] parts = flow.split(",");
        return parts[3].substring(15, parts[3].length()-1);
    }
    
    public static int getSrcPort(String flow){
        return Integer.parseInt(flow.split(",")[4].substring(11));
    }
    
    public static int getDstPort(String flow){
        //TODO: vyřešit ICMP dst_port 8.0 a 3.3
        if(getSrcPort(flow) == 0) return 0;
        return Integer.parseInt(flow.split(",")[5].substring(11));
    }
    
    public static String getProtocol(String flow){
        String[] parts = flow.split(",");
        return parts[6].substring(12, parts[6].length()-1);
    }
    
    public static String getFlags(String flow){
        String[] parts = flow.split(",");
        return parts[7].substring(9, parts[7].length()-1);
    }
    
    public static long getPackets(String flow){        
        try{
            return Long.parseLong(flow.split(",")[9].substring(10));
        }catch(NumberFormatException e){
            String[] parts = flow.split(",");
            String packetString = parts[9].substring(11, parts[9].length()-1);
            if(packetString.charAt(packetString.length()-1) == 'M'){
                return Long.parseLong(packetString.substring(0, packetString.indexOf('.'))) * 1_000_000l + 
                        Long.parseLong(packetString.substring(packetString.indexOf('.')+1, packetString.length()-1)) * 100_000l;
            } else if(packetString.charAt(packetString.length()-1) == 'G'){
                return Long.parseLong(packetString.substring(0, packetString.indexOf('.'))) * 1_000_000_000l + 
                        Long.parseLong(packetString.substring(packetString.indexOf('.')+1, packetString.length()-1)) * 100_000_000l;
            } else throw e;
        }
    }
    
    public static long getBytes(String flow){
        String[] parts = flow.split(",");        
        try{
            return Long.parseLong(parts[10].substring(8, parts[10].length()-1));
        }catch(NumberFormatException e){
            String byteString = parts[10].substring(9, parts[10].length()-2);
            if(byteString.charAt(byteString.length()-1) == 'M'){
                return Long.parseLong(byteString.substring(0, byteString.indexOf('.'))) * 1_000_000l + 
                        Long.parseLong(byteString.substring(byteString.indexOf('.')+1, byteString.length()-1)) * 100_000l;
            } else if(byteString.charAt(byteString.length()-1) == 'G'){
                return Long.parseLong(byteString.substring(0, byteString.indexOf('.'))) * 1_000_000_000l + 
                        Long.parseLong(byteString.substring(byteString.indexOf('.')+1, byteString.length()-1)) * 100_000_000l;
            } else throw e;
        }
    }
    
    public static void main(String[] args) {
        String line = "{\"date_first_seen\":\"2015-01-14T06:30:24.483+01:00\",\"duration\":0.992,\"src_ip_addr\":"
                + "\"92.245.161.237\",\"dst_ip_addr\":\"1.234.181.11\",\"src_port\":0,\"dst_port\":8.0,"
                + "\"protocol\":\"ICMP\",\"flags\":\"......\",\"tos\":0,\"packets\":\"28.4M\",\"bytes\":\"13.7M\"}";
        System.out.println("result = " + getBytes(line));
    }
}
