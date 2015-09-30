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

 ```
 cd scripts/
 bash prepareSamza.sh
 ```
 
#### Spuštění testů:

Skript spustí na masterovi HTTP server s aktuální verzí testů, spustí YARN a testy. Výsledky jsou uloženy v Kafce v topicu out.

 ```
 cd scripts/
 bash run_tests.sh
 ``` 
