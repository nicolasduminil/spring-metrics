#!/bin/sh
echo "********************************************************"
echo "Waiting for the Node Exporter service to start $NODE_EXPORTER_PORT"
echo "********************************************************"
while ! $(nc -z node_exporter $NODE_EXPORTER_PORT); do sleep 3; done
echo ">>>>>>>>>>>> The Node Exporter service has started"

echo "********************************************************"
echo "Waiting for the cAdvisor service to start on port $CADVISOR_PORT"
echo "********************************************************"
while ! $(nc -z advisor  $CADVISOR_PORT); do sleep 3; done
echo "******* The cAdvisor service has started"

echo "********************************************************"
echo "Waiting for the Prometheus service to start on port $PROMETHEUS_PORT"
echo "********************************************************"
while ! $(nc -z prometheus  $PROMETHEUS_PORT); do sleep 3; done
echo "******* The Prometheus service has started"

echo "********************************************************"
echo "Waiting for the Grafana service to start on port $GRAFANA_PORT"
echo "********************************************************"
while ! $(nc -z grafana  $GRAFANA_PORT); do sleep 3; done
echo "******* The Grafana service has started"

echo "********************************************************"
echo "Starting the Cision Press Release service on $SERVER_PORT"
echo "********************************************************"
java -Dserver.port=$SERVER_PORT -jar /usr/local/share/hml/metric-scraper.jar
