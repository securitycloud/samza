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

import org.apache.samza.Partition;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.util.BlockingEnvelopeMap;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileReaderConsumer extends BlockingEnvelopeMap {
    /**
     * Threshold used to determine when there are too many IncomingMessageEnvelopes to be put onto the BlockingQueue.
     */
    private static final int BOUNDED_QUEUE_THRESHOLD = 100;
    private final SystemStreamPartition systemStreamPartition;
    private final BufferedReader bufferedReader;

    private static final Logger log = Logger.getLogger(FileReaderConsumer.class.getName());
    private static Handler fh;
    /**
     * Sets up the SystemConsumer for reading from the specified file.
     *
     * @param systemName      The name of this system.
     * @param streamName      The name of the stream upon which to place file contents.
     * @param pathToInputFile The filesystem path to the file from which to read from.
     * @throws FileNotFoundException Thrown if the <code>pathToInputFile</code> arg does not point to a readable file.
     */
    public FileReaderConsumer(final String systemName, final String streamName, final String pathToInputFile)
            throws FileNotFoundException {
        this.systemStreamPartition = new SystemStreamPartition(systemName, streamName, new Partition(0));
        this.bufferedReader = new BufferedReader(new FileReader(pathToInputFile));
        
        try {
            fh = new FileHandler("/tmp/consumerLog.log");
            Logger.getLogger("").addHandler(fh);
            log.addHandler(fh);
            log.setLevel(Level.INFO);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(FileReaderConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void register(final SystemStreamPartition systemStreamPartition, final String startingOffset) {
        super.register(systemStreamPartition, startingOffset);
    }

    /**
     * Constructs a new BufferedReader on the previously constructed FileReader and attempts to read in the input file.
     * Reading from the file and placing the contents onto the SystemStreamPartition is done on a separate thread.
     * If the file read is successful, setIsHead() is called to specify that the SystemStreamPartition has "caught up".
     */
    @Override
    public void start() {
//        log.log(Level.INFO, "starting at: {0}", System.currentTimeMillis());
        Thread fileReadingThread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("start()");
                            readInputFiles();
                            setIsAtHead(systemStreamPartition, true);
                        } catch (InterruptedException e) {
                            e.getStackTrace();
                            stop();
                        }
                    }
                }
        );

        fileReadingThread.start();
//        log.log(Level.INFO, "end of start method at: {0}", System.currentTimeMillis());
    }

    /**
     * Frees access to file when done.
     */
    @Override
    public void stop() {
        try {
            this.bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads from the file in a BufferedReader, line-by-line, putting each line in an IncomingMessageEnvelope to be put
     * onto the specified SystemStreamPartition.
     * <p/>
     * Calling from a separate thread is advised.
     *
     * @throws InterruptedException Thrown if System is interrupted while attempting to place file contents onto the
     *                              specified SystemStreamPartition.
     */
    private void readInputFiles() throws InterruptedException {
        
//        log.log(Level.INFO, "readInputFiles(): {0}", System.currentTimeMillis());
        String line;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                for (int i = 0; i < 10; i++) {
//                    System.out.println("line = " + line);
                    put(systemStreamPartition, new IncomingMessageEnvelope(systemStreamPartition, null, null, line));
                    System.out.println("line = " + line);
                }                
            }
        } catch (IOException e) {
            System.out.println("e = " + e);
            put(systemStreamPartition, new IncomingMessageEnvelope(systemStreamPartition, null, null,
                    "ERROR: Cannot read from input file:\n" + e.getMessage()));
        }
//        log.log(Level.INFO, "end of readInputFiles(): {0}", System.currentTimeMillis());
    }

    /**
     * Constructs a new bounded BlockingQueue of IncomingMessageEnvelopes. The bound is determined by the
     * <code>BOUNDED_QUEUE_THRESHOLD</code> constant.
     *
     * @return A bounded queue used for queueing IncomingMessageEnvelopes to be sent to their specified destinations.
     */
    @Override
    protected BlockingQueue<IncomingMessageEnvelope> newBlockingQueue() {
        return new LinkedBlockingQueue<IncomingMessageEnvelope>(BOUNDED_QUEUE_THRESHOLD);
    }
    
    public static void main(String[] args) {
//        try {
            //        String line = "{\\\"date_first_seen\\\":\\\"2015-01-14T06:30:24.483+01:00\\\",\\\"duration\\\":0.992,\\\"src_ip_addr\\\":\\\"192.245.161.237\\\",\\\"dst_ip_addr\\\":\\\"1.234.181.11\\\",\\\"src_port\\\":0,\\\"dst_port\\\":8.0,\\\"protocol\\\":\\\"ICMP\",\\\"flags\\\":\\\"......\\\",\\\"tos\\\":0,\\\"packets\\\":2,\\\"bytes\\\":56}";
//        String[] parts = line.split("\\\\");
//        System.out.println("parts: " + parts[1]);
//            FileReaderConsumer fileReaderConsumer = new FileReaderConsumer("kafka", "wikipedia-raw", "/home/dazle/hello-samza/dataset_json.json");
//            
//            fileReaderConsumer.register(fileReaderConsumer.systemStreamPartition, null);
//            fileReaderConsumer.start();
//            fileReaderConsumer.stop();
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(FileReaderConsumer.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}