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
