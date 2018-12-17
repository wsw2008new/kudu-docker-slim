# Kudu Docker Slim
Slim docker image for [Apache Kudu](https://github.com/apache/kudu) based on CentOS 7
and RPM from [kudu-rpm](https://github.com/MartinWeindel/kudu-rpm)

Image can be found on [DockerHub](https://hub.docker.com/r/usuresearch/kudu-docker-slim/)

## Getting Started

```
docker run -d --rm --name apache-kudu --net=host usuresearch/kudu-docker-slim
```

If using the host network is no option, you can use something like
```
docker run -d --rm --name apache-kudu -p 8050:8050 -p 8051:8051 -p 7050:7050 -p 7051:7051 \
  -e KUDU_MASTER_EXTRA_OPTS='--webserver_advertised_addresses localhost:8051 --rpc_advertised_addresses localhost:7051 --rpc-encryption=disabled --rpc_authentication=disabled --unlock_unsafe_flags=true --allow_unsafe_replication_factor=true --default_num_replicas=1 --rpc_negotiation_timeout_ms=90000' \
  -e KUDU_TSERVER_EXTRA_OPTS='--webserver_advertised_addresses localhost:8050 --rpc_advertised_addresses localhost:7050 --rpc-encryption=disabled --rpc_authentication=disabled  --rpc_negotiation_timeout_ms=90000' \
  usuresearch/kudu-docker-slim
 ```
  
  
  
  
  
  
  
