printf "Criando as docker networks...\n"
docker network create elasticsearch-codeflix
docker network create kafka-catalogo-de-videos
docker network create admin-do-catalogo-services

# ----------------------------------------------------

printf "Criando os docker volumes...\n"
docker volume create elasticsearch-codeflix
docker volume create kafka-catalogo-de-videos
docker volume create kafka-connect-catalogo-de-videos

# ----------------------------------------------------

printf "Inicializando os container...\n"
docker compose -f elk/docker-compose.yml up -d
docker compose -f kafka/docker-compose.yml up -d
docker compose -f services/docker-compose.yml up -d
