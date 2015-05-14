#Espmon
##Prerekvizity:
Java
Maven

##Sestavení:
V tomto adresáři:
mvn isntall

##Spuštění Esper serverů:
###Spuštění example:
java -Xmx128m -jar example/target/test-example.jar 3600 40 9999
java -Xmx128m -jar example/target/test-example.jar 3600 20 9998
java -Xmx128m -jar example/target/test-example.jar 3600 10 9997
java -Xmx128m -jar example/target/test-example.jar 3600 1 9996
Tím se spustí 4 Esper servery s testy v 40, 20, 10 a 1 vlákně na portech 9999, 9998, 9997, 9996.

###Spuštění test-event-representation:
java -Xmx128m -jar example/target/test-representation.jar 3600 POJO 9999
java -Xmx128m -jar example/target/test-representation.jar 3600 MAP 9998
java -Xmx128m -jar example/target/test-representation.jar 3600 ARRAY 9997
java -Xmx128m -jar example/target/test-representation.jar 3600 XML 9996

###Spuštění test-event-representation:
java -Xmx128m -jar example/target/test-representation.jar 3600 LISTENER 9999
java -Xmx128m -jar example/target/test-representation.jar 3600 SUBSCRIBER 9998

##Spuštění klienta (sbírá výkonostní statistiky ze spuštěných Esper instancí):
###Konfigurace:
1) Vytvoření adresáře espmon-home
2) Vytvoření systémové proměnné ESPMON_HOME odkazující na tento adresář.
3) Nakopírování obsahu espmon-client/espmon-home-template do vytvřeného adresáře.
###Spuštění:
java -jar espmon-client/target/espmon-client.jar
Konfigurace v espmon-home-template/conf/espmon.xml je nastavena na 4 servery na portech 9999, 9998, 9997 a 9996.
Je možno konfiguraci upravit a zvolit si tak vlastní umístění serverů. Pokud je v konfiguraci server, který ale neběží,
pak je přeskočen.
Do adresáře $ESPMON_HOME/logs se nyní ukládají logy. Soubor event.log obsahuje zalogované EngineMetrics a StatementMetrics.


##Spuštění vizualizace:
###Prerekvizity:
logstash 1.4.2 (https://www.elastic.co/downloads/logstash)
elasticsearch 1.4.2 (https://www.elastic.co/downloads/past-releases/elasticsearch-1-4-2)
kibana 3.1.2 (https://www.elastic.co/downloads/past-releases/kibana-3-1-2)

###Konfigurace:
1) Zkopírovat obsah adresáře client-conf/logstash do adresáře s logstash
2) V souboru logstash-espmon.conf změnit hodnotu atributu conf na 3. řádku na skutečnou absolutní cestu k souboru event.log
3) Zkopírovat obsah adresáře client-conf/elasticsearch do adresáře s elasticsearch
4) Zkopírovat obsah adresáře s kibana do adresáře s elasticsearch/plugins/kibana/_site

###Spuštění:
1) V adresáři s logstash spustit bin/logstash -f logstash-espmon.conf
2) V adresáři s elasticsearch spustit bin/elastisearch
3) Ve webovém prohlížeči zadat adresu http://localhost:9200/_plugin/kibana/ - zobrazí se titulní strana kibana
4) Nahrát do kibana předem vytvořené obrazovky, které jsou v adresáři client-conf/kibana.
Nahrání se provede klikem na ikonu "Load" v pravém horním rohu úvodní obrazovky, přejetím na "Advanced"
a vybráním příslušného souboru. Po načtení souboru je třeba kliknout v pravém horním rohu na ikonu
"Save", aby byly změny zachovány.


