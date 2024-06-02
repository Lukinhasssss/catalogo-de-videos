printf "Criando as docker networks...\n"
docker network create catalogo-de-videos
docker network create kafka-catalogo-de-videos

# ----------------------------------------------------

printf "Criando os docker volumes...\n"
docker volume create kafka-catalogo-de-videos
docker volume create kafka-connect-catalogo-de-videos

# ----------------------------------------------------

printf "Inicializando os container...\n"
docker compose up -d
#docker compose up -d --build --force-recreate
