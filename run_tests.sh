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

#######################################################   TEST EMPTY ##################################################################################
/home/securitycloud/hadoop-2.7.1/sbin/start-yarn.sh
cd /home/securitycloud/samza

for var in {1..5}
do
  ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
  sleep 10

  # start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_empty.properties
  sleep 60

  # send data to kafka
  ssh 100.64.25.106 "cd ~/ekafsender ; ./run.sh"
  sleep 30
done
ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
sleep 10
/home/securitycloud/hadoop-2.7.1/sbin/stop-yarn.sh
sleep 20

#######################################################   TEST FILTER  ##################################################################################
/home/securitycloud/hadoop-2.7.1/sbin/start-yarn.sh
cd /home/securitycloud/samza

for var in {1..5}
do
  ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
  sleep 10

  # start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_filter.properties
  sleep 60

  # send data to kafka
  ssh 100.64.25.106 "cd ~/ekafsender ; ./run.sh"
  sleep 30
done
ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
sleep 10
/home/securitycloud/hadoop-2.7.1/sbin/stop-yarn.sh
sleep 20

#######################################################   TEST COUNT  ##################################################################################
/home/securitycloud/hadoop-2.7.1/sbin/start-yarn.sh
cd /home/securitycloud/samza

for var in {1..5}
do
  ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
  sleep 10

  # start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_count.properties
  sleep 60

  # send data to kafka
  ssh 100.64.25.106 "cd ~/ekafsender ; ./run.sh"
  sleep 30
done
ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
sleep 10
/home/securitycloud/hadoop-2.7.1/sbin/stop-yarn.sh
sleep 20

#######################################################   TEST AGGREGATE  ##################################################################################
/home/securitycloud/hadoop-2.7.1/sbin/start-yarn.sh
cd /home/securitycloud/samza

for var in {1..5}
do
  ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
  sleep 10

  # start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_aggregate.properties
  sleep 60

  # send data to kafka
  ssh 100.64.25.106 "cd ~/ekafsender ; ./run.sh"
  sleep 30
done
ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
sleep 10
/home/securitycloud/hadoop-2.7.1/sbin/stop-yarn.sh
sleep 20

#######################################################   TEST TOP 10  ##################################################################################
/home/securitycloud/hadoop-2.7.1/sbin/start-yarn.sh
cd /home/securitycloud/samza

for var in {1..5}
do
  ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
  sleep 10

  # start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_topN.properties
  sleep 60

  # send data to kafka
  ssh 100.64.25.106 "cd ~/ekafsender ; ./run.sh"
  sleep 30
done
ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
sleep 10
/home/securitycloud/hadoop-2.7.1/sbin/stop-yarn.sh
sleep 20

#######################################################   TEST SCAN  ##################################################################################
/home/securitycloud/hadoop-2.7.1/sbin/start-yarn.sh
cd /home/securitycloud/samza

for var in {1..5}
do
  ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
  sleep 10

  # start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_scan.properties
  sleep 60

  # send data to kafka
  ssh 100.64.25.106 "cd ~/ekafsender ; ./run.sh"
  sleep 30
done
ssh 100.64.25.106 "cd ~/ekafsender ; ./reset_kafka_topics.sh"
sleep 10
/home/securitycloud/hadoop-2.7.1/sbin/stop-yarn.sh
sleep 20
