#! /bin/sh

docker container stop instana-agent
docker container rm instana-agent

docker run \
  --detach \
  --name instana-agent \
  --volume /var/run:/var/run \
  --volume /run:/run \
  --volume /dev:/dev \
  --volume /sys:/sys \
  --volume /var/log:/var/log \
  --privileged \
  --net=host \
  --pid=host \
  --ipc=host \
  --env="INSTANA_AGENT_ENDPOINT=ingress-red-saas.instana.io" \
  --env="INSTANA_AGENT_ENDPOINT_PORT=443" \
  --env="INSTANA_AGENT_KEY=s-A8XW88TmCuPd-4XEPi_g" \
  --env="INSTANA_AGENT_ZONE=qa" \
  --env="INSTANA_TAGS=project=luqi,environment=qa,location=FR-Paris,division=cision,contact=infrastructure.fr@cision.com,install=docker" \
  --env="INSTANA_AGENT_UPDATES_VERSION=cd365d3a2d9333a33d08f85b93910c3fd2f39082" \
instana/agent