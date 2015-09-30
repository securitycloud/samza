# samza

První krok je naklonovat repozitář

#### Instalace Hadoop YARN:

Na začátku instalačního skriptu se nastavují IP adresy clusteru a Kafky. Skript následně automaticky stáhne a nakonfiguruje Hadoop, poté tuto instalaci rozdistribuuje na slave stroje v clusteru. 

 ```
 cd scripts/
 bash install.sh
 
 # momentálně nefunguje export ze skriptu, proměnné je potřeba nastavit ručně
 cd ../hadoop-2.7.1/
 export JAVA_HOME=/usr/lib/jvm/java-8-oracle
 export HADOOP_YARN_HOME=$(pwd)
 export HADOOP_CONF_DIR=$HADOOP_YARN_HOME/conf
 ```
    
#### Installace Samzy:

Na začátku instalačního skriptu se nastavují IP adresy mastera a Kafky. Skript následně automaticky zkompiluje project a nakonfiguruje IP adresy strojů.

Pozn. na testbedu 10.16.31.2** mi nefunguje příkaz "mvn clean package", takže jsem tam musel uploadovat tar.gz ručně

 ```
 cd scripts/
 bash prepareSamza.sh
 ```
 
#### Spuštění testů:

Skript spustí na masterovi HTTP server s aktuální verzí testů, spustí YARN a testy. 

Vstup testů je Kafka topic "tst", který se čte od začátku. Výsledky jsou uloženy v Kafce v topicu out.

 ```
 cd scripts/
 bash run_tests.sh
 ``` 

#### Konfigurace:

YARN: 
- soubor $HADOOP_YARN_HOME/conf/yarn-site.xml , změny je nutné dělat na každém stroji clusteru (každému lze nastavit jiná paměť, procesory atd.)

Samza: 
- soubory v src\main\config, po jejich změně je nutné překompilovat celý projekt (prepareSamza.sh). IP adresy mastera a Kafky lze nechat na 999.. a 888.., které skript nahrazuje automaticky.
- pokud nechceme kompilovat, lze měnit soubory v deploy/samza/config/, po jejichž změně stačí pouze znovu spustit testy. Pozor ale! překompilování projektu veškeré změny v deploy/samza/ přepíše!!! 