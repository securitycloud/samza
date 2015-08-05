# samza

Vsechno zatim odkazuje na localhost

#### Instalace:
  `bin/grid bootstrap`
  
nainstaluje a spusti Kafku, Yarn a Zookeeper
    
#### Kompilace:
 ```
 mvn clean package
 mkdir -p deploy/samza
 tar -xvf ./target/hello-samza-0.8.0-dist.tar.gz -C deploy/samza
 ```
 
#### Deploy do YARN (=spusteni)
  `deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test.properties`
  
#### Ukonceni tasku:
`deploy/samza/bin/kill-yarn-job.sh application_1234654815_0001`

ID aplikace na zabiti generuje hadoop -> nalezneme na localhost:8088/cluster v prehledu vsech aplikaci v clusteru

#### Vypnuti clusteru:
 `bin/grid stop all`
 
 nebo 
 
 ```
 bin/grid stop yarn
 bin/grid stop kafka
 bin/grid stop zookeeper
 ```
 vypne yarn, kafku a zookeepera a s nimi i vsechny aplikace, ktere na nich bezi
 
#### Nasazeni do testedu (systemy jsou naistalovane):
 
  ```
  mvn clean package
scp -i /home/dazle/Documents/id_rsa target/hello-samza-0.8.0-dist.tar.gz root@10.16.31.214:/tmp/samzatest/

# resource manager - cluster master
ssh -i /home/dazle/Documents/id_rsa root@10.16.31.214
cd /tmp/samzatest/
mkdir -p deploy/samza
tar -xvf ./hello-samza-0.8.0-dist.tar.gz -C deploy/samza
. /root/samza/exportVars.sh
/root/hadoop-2.4.0/sbin/start-yarn.sh
deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/samza_test.properties
deploy/samza/bin/kill-yarn-job.sh application_1434534677109_0001
/root/hadoop-2.4.0/sbin/stop-yarn.sh

ssh -i /home/dazle/Documents/id_rsa root@10.16.31.214
cd /tmp/samzatest/ 
python -m SimpleHTTPServer

# consume data
ssh -i /home/dazle/Documents/id_rsa root@10.16.31.201
kafka/kafka_2.11-0.8.2.1/bin/kafka-console-consumer.sh --zookeeper 10.16.31.201:2181 --topic samza-stats

#produce data
ssh -i /home/dazle/Documents/id_rsa root@10.16.31.200
./produce_testing_data.sh
 ```
