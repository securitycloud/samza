#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

#######################################################   START ##################################################################################
cd /home/securitycloud/samza
#ssh 100.64.25.102 "cd ~/samza/target ; python -m SimpleHTTPServer"

#######################################################   TEST EMPTY ##################################################################################
for var in {1..3}
do
  /home/securitycloud/hadoop-2.7.1/sbin/start-yarn.sh  
  ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
  sleep 25

#  start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_empty.properties
  sleep 80
  echo "konec spani, spoustim test"
  # send data to kafka
  ssh 100.64.25.106 "cd ~/ekafsender ; ./run.sh >> /tmp/results_samza_empty"
  sleep 80
  /home/securitycloud/hadoop-2.7.1/sbin/stop-yarn.sh
  sleep 20
done

#######################################################   TEST FILTER  ##################################################################################
for var in {1..3}
do
  /home/securitycloud/hadoop-2.7.1/sbin/start-yarn.sh
  ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
  sleep 30

  # start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_filter.properties
  sleep 80

  # send data to kafka
  ssh 100.64.25.106 "cd ~/ekafsender ; ./run.sh >> /tmp/results_samza_filter"
  sleep 120
  /home/securitycloud/hadoop-2.7.1/sbin/stop-yarn.sh
  sleep 20
done

#######################################################   TEST COUNT  ##################################################################################
for var in {1..3}
do
  /home/securitycloud/hadoop-2.7.1/sbin/start-yarn.sh
  ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
  sleep 30

  # start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_count.properties
  sleep 80

  # send data to kafka
  ssh 100.64.25.106 "cd ~/ekafsender ; ./run.sh >> /tmp/results_samza_count"
  sleep 120
  /home/securitycloud/hadoop-2.7.1/sbin/stop-yarn.sh
  sleep 20
done

#######################################################   TEST AGGREGATE  ##################################################################################
for var in {1..3}
do
  /home/securitycloud/hadoop-2.7.1/sbin/start-yarn.sh
  ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
  sleep 30

  # start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_aggregate.properties
  sleep 80

  # send data to kafka
  ssh 100.64.25.106 "cd ~/ekafsender ; ./run.sh >> /tmp/results_samza_aggregate"
  sleep 120
  /home/securitycloud/hadoop-2.7.1/sbin/stop-yarn.sh
  sleep 20
done

#######################################################   TEST TOP 10  ##################################################################################
for var in {1..3}
do
  /home/securitycloud/hadoop-2.7.1/sbin/start-yarn.sh
  ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
  sleep 30

  # start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_topN.properties
  sleep 80

  # send data to kafka
  ssh 100.64.25.106 "cd ~/ekafsender ; ./run.sh >> /tmp/results_samza_topN"
  sleep 120
  /home/securitycloud/hadoop-2.7.1/sbin/stop-yarn.sh
  sleep 20
done

#######################################################   TEST SCAN  ##################################################################################
for var in {1..3}
do
  /home/securitycloud/hadoop-2.7.1/sbin/start-yarn.sh
  ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
  sleep 30

  # start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_scan.properties
  sleep 80

  # send data to kafka
  ssh 100.64.25.106 "cd ~/ekafsender ; ./run.sh >> /tmp/results_samza_scan"
  sleep 120
  /home/securitycloud/hadoop-2.7.1/sbin/stop-yarn.sh
  sleep 20
done

#######################################################   CLEANUP  ##################################################################################
ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
sleep 30


