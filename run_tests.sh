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


# export vars
export JAVA_HOME=/usr/lib/jvm/java-8-oracle
export HADOOP_YARN_HOME=/root/hadoop-2.4.0
export HADOOP_CONF_DIR=$HADOOP_YARN_HOME/conf

# prepare directory structure in tmp and copy samza files there
mkdir /tmp/samza
mkdir /tmp/samza/deploy
tar -xvf /root/samza/hello-samza-0.8.0-dist.tar.gz -C /tmp/samza/deploy/samza

# start YARN
/root/hadoop-2.4.0/sbin/start-yarn.sh

# now you must make sure that SimpleHTTPServer is RUNNING and package is available
# for other machines on http://10.16.31.214:8000/hello-samza-0.8.0-dist.tar.gz
# If server is not runninig, start him using screen:
#              cd /root/samza
#              python -m SimpleHTTPServer & 

# run count window task
/tmp/samza/deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=/tmp/samza/deploy/samza/config/count_window.properties

sleep 10

# run test
/tmp/samza/deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=/tmp/samza/deploy/samza/config/samza_test.properties

sleep 120

# stop YARN after test
/root/hadoop-2.4.0/sbin/stop-yarn.sh
