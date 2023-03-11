# ENTRETIEN-RH
[![Docker Hub Repo](https://img.shields.io/docker/pulls/netomantonio/entretien-backend-app.svg)](https://hub.docker.com/repository/docker/netomantonio/entretien-backend-app)
## Instructions

1. Check dependencies:
   1. Docker/Docker Compose
2. Check environment variables(Intellij):
   1. DB_PASSWORD
   2. DB_USERNAME
   3. OPENVIDU_SECRET
3. Export environments variables from system with code:
   1. `export set POSTGRES_USER=user POSTGRES_PASSWORD=password OPENVIDU_SECRET=entretien-rh POSTGRES_DB=db`
4. Add environments variables from run configurations IDE with code:
   1. `POSTGRES_USER=user;POSTGRES_PASSWORD=password;OPENVIDU_SECRET=entretien-rh;POSTGRES_DB=db`