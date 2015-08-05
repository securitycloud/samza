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

import scala.Serializable;

/**
 * POJO for mapping of parsed JSON messages.
 */
public class Flow implements Serializable {

    private String date_first_seen;
    private String date_last_seen;
    private double duration;
    private String src_ip_addr;
    private String dst_ip_addr;
    private int src_port;
    private int dst_port;
    private int protocol;
    private String flags;
    private int tos;
    private int packets;
    private int bytes;

    public String getDate_first_seen() {
        return date_first_seen;
    }

    public void setDate_first_seen(String date_first_seen) {
        this.date_first_seen = date_first_seen;
    }

    public String getDate_last_seen() {
        return date_last_seen;
    }

    public void setDate_last_seen(String date_last_seen) {
        this.date_last_seen = date_last_seen;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getSrc_ip_addr() {
        return src_ip_addr;
    }

    public void setSrc_ip_addr(String src_ip_addr) {
        this.src_ip_addr = src_ip_addr;
    }

    public String getDst_ip_addr() {
        return dst_ip_addr;
    }

    public void setDst_ip_addr(String dst_ip_addr) {
        this.dst_ip_addr = dst_ip_addr;
    }

    public int getSrc_port() {
        return src_port;
    }

    public void setSrc_port(int src_port) {
        this.src_port = src_port;
    }

    public int getDst_port() {
        return dst_port;
    }

    public void setDst_port(int dst_port) {
        this.dst_port = dst_port;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public int getTos() {
        return tos;
    }

    public void setTos(int tos) {
        this.tos = tos;
    }

    public int getPackets() {
        return packets;
    }

    public void setPackets(int packets) {
        this.packets = packets;
    }

    public int getBytes() {
        return bytes;
    }

    public void setBytes(int bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "Flow{date_first_seen=" + date_first_seen + ", date_last_seen="
                + date_last_seen + ", duration=" + duration + ", src_ip_addr="
                + src_ip_addr + ", dst_ip_addr=" + dst_ip_addr + ", src_port="
                + src_port + ", dst_port=" + dst_port + ", protocol=" + protocol
                + ", flags=" + flags + ", tos=" + tos + ", packets=" + packets
                + ", bytes=" + bytes + "}";
    }
}
