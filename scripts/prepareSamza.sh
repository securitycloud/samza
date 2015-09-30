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

# This script will download, setup, start, and stop servers for Kafka, YARN, and ZooKeeper,
# as well as downloading, building and locally publishing Samza

cd ..

master="10.16.31.214"
kafka="10.16.31.201"  

mvn clean package
mkdir -p deploy/samza
tar -xvf ./target/hello-samza-0.8.0-dist.tar.gz -C deploy/samza

# configure as much as possible               
sed -i -- "s/999.999.999.999/$master/" deploy/samza/config/samza_test_empty.properties
sed -i -- "s/888.888.888.888/$kafka/" deploy/samza/config/samza_test_empty.properties
sed -i -- "s/999.999.999.999/$master/" deploy/samza/config/samza_test_filter.properties
sed -i -- "s/888.888.888.888/$kafka/" deploy/samza/config/samza_test_filter.properties
sed -i -- "s/999.999.999.999/$master/" deploy/samza/config/samza_test_count.properties
sed -i -- "s/888.888.888.888/$kafka/" deploy/samza/config/samza_test_count.properties
sed -i -- "s/999.999.999.999/$master/" deploy/samza/config/samza_test_aggregate.properties
sed -i -- "s/888.888.888.888/$kafka/" deploy/samza/config/samza_test_aggregate.properties
sed -i -- "s/999.999.999.999/$master/" deploy/samza/config/samza_test_topN.properties
sed -i -- "s/888.888.888.888/$kafka/" deploy/samza/config/samza_test_topN.properties
sed -i -- "s/999.999.999.999/$master/" deploy/samza/config/samza_test_scan.properties
sed -i -- "s/888.888.888.888/$kafka/" deploy/samza/config/samza_test_scan.properties
sed -i -- "s/999.999.999.999/$master/" deploy/samza/config/count_window.properties
sed -i -- "s/888.888.888.888/$kafka/" deploy/samza/config/count_window.properties
