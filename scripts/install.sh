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

master="10.16.31.214"
kafka="10.16.31.201"

# if you want master to serve as slave, you must put i here also
slave1="10.16.31.211"
slave2="10.16.31.212"
slave3="10.16.31.213"
slave4="10.16.31.214"
slave5="10.16.31.215"

cd ..
#todo download Hadoop from some mirror, GitHub cant handle files over 100MB 
wget http://apache.miloslavbrada.cz/hadoop/common/hadoop-2.7.1/hadoop-2.7.1.tar.gz 
tar -xvf hadoop-2.7.1.tar.gz

cd hadoop-2.7.1/

export JAVA_HOME=/usr/lib/jvm/java-8-oracle
export HADOOP_YARN_HOME=$(pwd)
mkdir -p conf
export HADOOP_CONF_DIR=$HADOOP_YARN_HOME/conf

echo -e "$slave1\n$slave2\n$slave3\n$slave4\n$slave5" > conf/slaves

cd ..

# set master into configuration
sed -i -- "s/999.999.999.999/$master/" yarn-site.xml

# copy prepared files to Hadoop
# you must configure RAM and processor cores available on each machine, defaults are 8GB and 4 cores
cp yarn-site.xml $HADOOP_YARN_HOME/conf/yarn-site.xml   

# here you must correctly set java home
cp hadoop-env.sh $HADOOP_YARN_HOME/conf/hadoop-env.sh 

# these files dont have to be modified
cp capacity-scheduler.xml $HADOOP_YARN_HOME/conf/capacity-scheduler.xml   
cp scala-compiler.jar $HADOOP_YARN_HOME/share/hadoop/hdfs/lib
cp scala-library.jar $HADOOP_YARN_HOME/share/hadoop/hdfs/lib
cp grizzled-slf4j_2.10-1.0.1.jar $HADOOP_YARN_HOME/share/hadoop/hdfs/lib/grizzled-slf4j_2.10-1.0.1.jar
cp samza-yarn_2.10-0.8.0.jar $HADOOP_YARN_HOME/share/hadoop/hdfs/lib/samza-yarn_2.10-0.8.0.jar
cp samza-core_2.10-0.8.0.jar $HADOOP_YARN_HOME/share/hadoop/hdfs/lib/samza-core_2.10-0.8.0.jar
cp core-site.xml $HADOOP_YARN_HOME/conf/core-site.xml  

cd hadoop-2.7.1
HADOOP_SLAVE_HOME=$HADOOP_YARN_HOME
ssh $slave1 "mkdir -p $HADOOP_SLAVE_HOME"
scp -r . $slave1:$HADOOP_SLAVE_HOME
ssh $slave2 "mkdir -p $HADOOP_SLAVE_HOME"
scp -r . $slave2:$HADOOP_SLAVE_HOME
ssh $slave3 "mkdir -p $HADOOP_SLAVE_HOME"
scp -r . $slave3:$HADOOP_SLAVE_HOME
ssh $slave4 "mkdir -p $HADOOP_SLAVE_HOME"
scp -r . $slave4:$HADOOP_SLAVE_HOME
ssh $slave5 "mkdir -p $HADOOP_SLAVE_HOME"
scp -r . $slave5:$HADOOP_SLAVE_HOME

# Hadoop is ready
               