#!/bin/bash

slave1="10.16.31.211"
slave2="10.16.31.212"
slave3="10.16.31.213"
slave4="10.16.31.214"
slave5="10.16.31.215"

#todo download Hadoop from some mirror, GitHub

tar -xvf hadoop-2.4.0.tar.gz
cd hadoop-2.4.0

export HADOOP_YARN_HOME=$(pwd)
mkdir conf
export HADOOP_CONF_DIR=$HADOOP_YARN_HOME/conf

echo -e "$slave1\n$slave2\n$slave3\n$slave4\n$slave5" > conf/slaves

cp ./etc/hadoop/yarn-site.xml conf
vi conf/yarn-site.xml       
#todo add yarn-site.xml

cd ..
mv capacity-scheduler.xml $HADOOP_YARN_HOME/conf/capacity-scheduler.xml   
mv scala-compiler.jar $HADOOP_YARN_HOME/share/hadoop/hdfs/lib
mv scala-library.jar $HADOOP_YARN_HOME/share/hadoop/hdfs/lib
mv grizzled-slf4j_2.10-1.0.1.jar $HADOOP_YARN_HOME/share/hadoop/hdfs/lib/grizzled-slf4j_2.10-1.0.1.jar
mv samza-yarn_2.10-0.8.0.jar $HADOOP_YARN_HOME/share/hadoop/hdfs/lib/samza-yarn_2.10-0.8.0.jar
mv samza-core_2.10-0.8.0.jar $HADOOP_YARN_HOME/share/hadoop/hdfs/lib/samza-core_2.10-0.8.0.jar


vi $HADOOP_YARN_HOME/conf/core-site.xml
#todo prepare core-site


cd hadoop-2.4.0
HADOOP_SLAVE_HOME=$HADOOP_YARN_HOME
scp -r . $slave1:$HADOOP_SLAVE_HOME
scp -r . $slave2:$HADOOP_SLAVE_HOME
scp -r . $slave3:$HADOOP_SLAVE_HOME
scp -r . $slave4:$HADOOP_SLAVE_HOME
scp -r . $slave5:$HADOOP_SLAVE_HOME
