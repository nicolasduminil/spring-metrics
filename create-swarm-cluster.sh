#!/usr/bin/env bash

#
# Check wether all the required arguments are provided
#
if [[ $# -lt 6 || $# -gt 14 ]]
then
  echo "usage: $0 --name <cluster-name> --number <number-of-nodes> --label <cluster-label> [--port <tcp-port>] \
    [--leaders <number-of-leaders>] [--network <network-name>] [--stack <stack-name>]"
  exit 1  
fi

#
# Mount the current directory as a shared folder.
# This only works on Linux.
#
if [[ "$(uname -s )" == "Linux" ]]
then
  export VIRTUALBOX_SHARE_FOLDER="$PWD:$PWD"
fi

#
# Parse the command line and initializes the input params
#
while [[ "$#" -gt 0 ]]
do 
  case $1 in
    --name) name="$2"; shift;;
    --number) number="$2"; shift;;
    --label) label="$2"; shift;;
    --port) port="$2"; shift;;
    --leaders) leaders="$2"; shift;;
    --network) network="$2"; shift;;
    --stack) stack="$2"; shift;;
    *) echo "Unknown parameter passed: $1"; exit 1;;
  esac;
  shift
done

#
# Initialize the optional params
#
if [[ -z "$port" ]]
then
  port="2377"
fi
echo $leaders
if [[ -z "$leaders" ]]  
then
  leaders=1
fi
if [[ -z "$network" ]]
then
  network="$name-network"  
fi  
if [[ -z "$stack" ]]
then
  stack="$name-stack"  
fi

#
# Create the VMs
#
for node in $(seq $number)
do
  docker-machine create -d virtualbox $name-$node 
done

#
# Set the cluster leaders as being the first $leaders nodes
#
for node in $(seq $leaders)
do
  eval $(docker-machine env $name-$node)
  docker swarm init --advertise-addr $(docker-machine ip $name-$node)
done

#
# Get the worker token
#
TOKEN=$(docker swarm join-token -q worker)

#
# Set the workers nodes 
#
for node in $(seq $(($leaders+1)) $number)
do
  eval $(docker-machine env $name-$node)
  docker swarm join --token $TOKEN --advertise-addr $(docker-machine ip $name-$node) $(docker-machine ip $name-1):$port 
done

#
# Set the current swarm node as being the 1st leader
#
eval $(docker-machine env $name-1)

#
# Add a label to all the nodes
#
for node in $(seq $number)
do
  docker node update --label-add env=$label $name-$node 
done

#
# Create an overlay network adapter
#
docker network create --driver overlay $network

#
#  Create the stack file create-stack.yml out of a file template
#
export network
( echo "cat <<EOF >create-stack.yml";
  cat create-stack-template.yml;
  echo "";
  echo "EOF";
) >temp.yml
. temp.yml

#
# Create a stack having nginx, tomcat and mysql
#
docker stack deploy -c create-stack.yml $stack

#
# Create a service running portainer
#
docker service create --name portainer --publish 9000:9000 --replicas=1 \
  --mount type=bind,source=/var/run,destination=/var/run \
  --mount type=bind,source=/var/run/docker.sock,destination=/var/run/docker.sock \
  --constraint 'node.role == manager' portainer/portainer > /dev/null

#
# Done
#
echo "### The cluster $name is up and running nodes from $name-1 to $name-$number"
echo -e "\tVirtual Machines\n\t---------------"
docker-machine ls
echo -e "\tCluster Nodes\n\t-------------"
docker node ls
echo -e "\tServices\n\t--------"
docker service ls | awk 'FNR > 1 { print $2 }' | xargs docker service ps

echo "### Creating the node-exporter service"
#
# Create the node-exporter service
#
docker service create --name node-exporter --mode global --network $network --mount "type=bind,source=/proc,target=/host/proc" \
  --mount "type=bind,source=/sys,target=/host/sys" --mount "type=bind,source=/,target=/rootfs" prom/node-exporter:v0.14.0 \
  -collector.procfs /host/proc -collector.sysfs /host/proc -collector.filesystem.ignored-mount-points "^/(sys|proc|dev|host|etc)($|/)" \
  -collector.textfile.directory /etc/node-exporter/ \
  -collectors.enabled="conntrack,diskstats,entropy,filefd,filesystem,loadavg,mdadm,meminfo,netdev,netstat,stat,textfile,time,vmstat,ipvs"
docker service ps node-exporter

#
# Create the cadvisor service
#
docker service create --name cadvisor -p 8088:8088 --mode global --network $network --mount "type=bind,source=/,target=/rootfs" \
  --mount "type=bind,source=/var/run,target=/var/run" --mount "type=bind,source=/sys,target=/sys" \
  --mount "type=bind,source=/var/lib/docker,target=/var/lib/docker" google/cadvisor:v0.24.1
docker service ps cadvisor

#
# Create the Prometheus service
#
docker service create --name prometheus --network $network -p 9090:9090 \
  --mount "type=bind,source=$PWD/prometheus.yml,target=/etc/prometheus/prometheus.yml" prom/prometheus:v1.2.1
#--mount "type=bind,source=$PWD/docker/prometheus,target=/prometheus" prom/prometheus:v1.2.1 \
docker service ps prometheus

#
# Create the grafana service
#
docker service create --name grafana --network $network -p 3000:3000 grafana/grafana:3.1.1
docker service ps grafana

#
# Create the ElasticSearch service
#
#docker service create --name elasticsearch --network $network --reserve-memory 300m -p 9200:9200 elasticsearch:2.4
#docker service ps elasticsearch

#
# Create the LogStash service 
#
#docker service create --name logstash --network $network --mount "type=bind,source=$PWD,target=/conf" \
#  -e LOGSPOUT=ignore logstash:2.4 logstash -f /conf/logstash.conf
#docker service ps logstash

#
# Create the LogSpout service
#
#docker service create --name logspout --network $network --mode global \
#  --mount "type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock" \
#  -e SYSLOG_FORMAT=rfc3164 gliderlabs/logspout syslog://logstash:51415
#docker service ps logspout

#
# Put the mysql service in group db
#
docker service update --container-label-add com.docker.stack.namespace=db $stack_mysql

#
# Put the nginx service in group backend
#
docker service update --container-label-add com.docker.stack.namespace=frontend $stack_nginx

#
# Put the tomcat service in group db
#
docker service update --container-label-add com.docker.stack.namespace=backend $stack_tomcat

#
# Done
#
#for i in {1..100}
#do
#    curl "$(docker-machine ip monitor-1)/demo/hello"
#done

#for i in {1..100}
#do
#    curl "$(docker-machine ip monitor-1)/demo/random-error"
#done

echo "### The monitoring infrastructure service is up and running"  

#
# End of script
#
echo "### The script terminated successfully"