#!/bin/bash

master="10.16.31.214"

# if you want master to serve as slave, you must put i here also
slave1="10.16.31.211"
slave2="10.16.31.212"
slave3="10.16.31.213"
slave4="10.16.31.214"
slave5="10.16.31.215"


#todo download Hadoop from some mirror, GitHub cant handle files over 100MB 
tar -xvf hadoop-2.7.1.tar.gz

cd hadoop-2.7.1

export HADOOP_YARN_HOME=$(pwd)
mkdir conf
export HADOOP_CONF_DIR=$HADOOP_YARN_HOME/conf

echo -e "$slave1\n$slave2\n$slave3\n$slave4\n$slave5" > conf/slaves

cd ..

# set master into configuration
sed -i -- "s/999.999.999.999/$master" yarn-site.xml

# copy prepared files to Hadoop
# you must configure RAM and processor cores available on each machine, defaults are 8GB and 4 cores
mv yarn-site.xml $HADOOP_YARN_HOME/conf/yarn-site.xml   

# here you must correctly set java home
mv hadoop-env.sh $HADOOP_YARN_HOME/conf/hadoop-env.sh 

# these files dont have to be modified
mv capacity-scheduler.xml $HADOOP_YARN_HOME/conf/capacity-scheduler.xml   
mv scala-compiler.jar $HADOOP_YARN_HOME/share/hadoop/hdfs/lib
mv scala-library.jar $HADOOP_YARN_HOME/share/hadoop/hdfs/lib
mv grizzled-slf4j_2.10-1.0.1.jar $HADOOP_YARN_HOME/share/hadoop/hdfs/lib/grizzled-slf4j_2.10-1.0.1.jar
mv samza-yarn_2.10-0.8.0.jar $HADOOP_YARN_HOME/share/hadoop/hdfs/lib/samza-yarn_2.10-0.8.0.jar
mv samza-core_2.10-0.8.0.jar $HADOOP_YARN_HOME/share/hadoop/hdfs/lib/samza-core_2.10-0.8.0.jar
mv core-site.xml $HADOOP_YARN_HOME/conf/core-site.xml  
      

cd hadoop-2.7.1
HADOOP_SLAVE_HOME=$HADOOP_YARN_HOME
scp -r . $slave1:$HADOOP_SLAVE_HOME
scp -r . $slave2:$HADOOP_SLAVE_HOME
scp -r . $slave3:$HADOOP_SLAVE_HOME
scp -r . $slave4:$HADOOP_SLAVE_HOME
scp -r . $slave5:$HADOOP_SLAVE_HOME

# Hadoop is ready

cd ..
mkdir -p samza/deploy/samza
tar -xvf ./hello-samza-0.8.0-dist.tar.gz -C samza/deploy/samza
