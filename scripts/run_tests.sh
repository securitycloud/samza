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
#cd /home/securitycloud/samza
kafkaServer="10.16.31.201"
cd ../target 
python -m SimpleHTTPServer > /dev/null 2>&1 &
cd ..

#######################################################   TEST EMPTY ##################################################################################
for var in {1..3}
do
  $HADOOP_YARN_HOME/sbin/start-yarn.sh  
  sleep 10

#  start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_empty.properties
  sleep 80

  $HADOOP_YARN_HOME/sbin/stop-yarn.sh
  sleep 20
done

#######################################################   TEST FILTER  ##################################################################################
for var in {1..3}
do
  $HADOOP_YARN_HOME/sbin/start-yarn.sh
  sleep 10

  # start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_filter.properties
  sleep 80

  $HADOOP_YARN_HOME/sbin/stop-yarn.sh
  sleep 20
done

#######################################################   TEST COUNT  ##################################################################################
for var in {1..3}
do
  $HADOOP_YARN_HOME/sbin/start-yarn.sh
  sleep 10

  # start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_count.properties
  sleep 80

  $HADOOP_YARN_HOME/sbin/stop-yarn.sh
  sleep 20
done

#######################################################   TEST AGGREGATE  ##################################################################################
for var in {1..3}
do
  $HADOOP_YARN_HOME/sbin/start-yarn.sh
  sleep 10

  # start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_aggregate.properties
  sleep 80

  $HADOOP_YARN_HOME/sbin/stop-yarn.sh
  sleep 20
done

#######################################################   TEST TOP 10  ##################################################################################
for var in {1..3}
do
  $HADOOP_YARN_HOME/sbin/start-yarn.sh
  sleep 10

  # start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_topN.properties
  sleep 80

  $HADOOP_YARN_HOME/sbin/stop-yarn.sh
  sleep 20
done

#######################################################   TEST SCAN  ##################################################################################
for var in {1..3}
do
  $HADOOP_YARN_HOME/sbin/start-yarn.sh
  sleep 10

  # start count window 
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/count_window.properties
  sleep 10

  # start test
  ./deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test_scan.properties
  sleep 80

  $HADOOP_YARN_HOME/sbin/stop-yarn.sh
done

#######################################################   CLEANUP  ##################################################################################

pkill -f "python -m SimpleHTTPServer"

# download results
#scp $kafkaServer:/tmp/results_samza_empty ./results_samza_empty
#scp $kafkaServer:/tmp/results_samza_filter ./results_samza_filter
#scp $kafkaServer:/tmp/results_samza_count ./results_samza_count
#scp $kafkaServer:/tmp/results_samza_aggregate ./results_samza_aggregate
#scp $kafkaServer:/tmp/results_samza_topN ./results_samza_topN 
#scp $kafkaServer:/tmp/results_samza_scan ./results_samza_scan

