set container_name=%1

docker cp run.sh %container_name%:/home/
docker cp create.cql %container_name%:/home/
docker exec %container_name% /bin/bash /home/run.sh