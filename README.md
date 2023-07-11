<h1 align="center">Welcome to entretien-backend-app 👋</h1>
<p>
  <img alt="Version" src="https://img.shields.io/badge/version-0.0.1-blue.svg?cacheSeconds=2592000" />
  <a href="www.docs.entretienrh.com" target="_blank">
    <img alt="Documentation" src="https://img.shields.io/badge/documentation-yes-brightgreen.svg" />
  </a>
  <a href="https://www.gnu.org/licenses/gpl-3.0.html" target="_blank">
    <img alt="License: The GNU General Public License v3.0" src="https://img.shields.io/badge/License-The GNU General Public License v3.0-yellow.svg" />
  </a>
</p>

> Projeto backend para plataforma de gerenciamento de entrevistas por competências no processo de Recrutamento e Seleção de novos talentos, cujo publico alvo é ofertar a oportunidade de melhorar pontualmente, numa das etapas mais importantes e crítica do processo de R&S que é a entrevista por competência com recrutadores profissionais.

### 🏠 [Homepage](https://www.entretienrh.com) <sub><sup><sub>not working</sub></sup></sub>

### ✨ [Demo](https://www.demo.entretienrh.com) <sub><sup><sub>not working</sub></sup></sub>

## Pré-requisitos
### Instalação

Para instalar manualmente as dependências necessárias antes de executar o projeto, você precisará garantir que todas as seguintes dependências estejam instaladas em seu sistema:

- <img src="https://www.vectorlogo.zone/logos/java/java-icon.svg" alt="Java" width="16" height="16"> Java 11: Certifique-se de ter o Java Development Kit (JDK) 11 instalado. Você pode baixar e instalar o JDK 11 a partir do site oficial da Oracle ou usar um gerenciador de pacotes, como o apt no Ubuntu, para instalá-lo.

- <img src="https://www.vectorlogo.zone/logos/kotlinlang/kotlinlang-icon.svg" alt="Kotlin" width="16" height="16"> Kotlin: O Kotlin é uma linguagem de programação que é compatível com a JVM (Java Virtual Machine) e pode ser usada para desenvolver aplicativos. Você pode baixar e instalar o Kotlin a partir do site oficial do Kotlin ou usar um gerenciador de pacotes, como o apt no Ubuntu, para instalá-lo.

- <img src="https://www.vectorlogo.zone/logos/gradle/gradle-icon.svg" alt="Gradle" width="16" height="16"> Gradle: O Gradle é uma ferramenta de automação de compilação e construção de projetos. Certifique-se de ter o Gradle instalado em seu sistema. Você pode baixar e instalar o Gradle a partir do site oficial do Gradle ou usar um gerenciador de pacotes, como o apt no Ubuntu, para instalá-lo.

- <img src="https://www.vectorlogo.zone/logos/docker/docker-icon.svg" alt="Docker" width="16" height="16"> Docker: O Docker é uma plataforma de código aberto que permite automatizar o desenvolvimento, a implantação e a execução de aplicativos dentro de contêineres. Certifique-se de ter o Docker instalado em seu sistema. Você pode baixar e instalar o Docker a partir do site oficial do Docker ou seguir as instruções específicas para sua distribuição Linux.

- <img src="https://www.vectorlogo.zone/logos/docker/docker-icon.svg" alt="Docker Compose" width="16" height="16"> Docker Compose: O Docker Compose é uma ferramenta que permite definir e gerenciar aplicativos Docker compostos por vários contêineres. Certifique-se de ter o Docker Compose instalado em seu sistema. Você pode baixar e instalar o Docker Compose a partir do site oficial do Docker Compose ou seguir as instruções específicas para sua distribuição Linux.

- <img src="https://www.vectorlogo.zone/logos/getpostman/getpostman-icon.svg" alt="Postman" width="16" height="16"> Postman: O Postman é uma plataforma de colaboração para desenvolvimento de API. Certifique-se de ter o Postman instalado em seu sistema. Você pode baixar e instalar o Postman a partir do site oficial do Postman ou usar um gerenciador de pacotes, como o apt no Ubuntu, para instalá-lo.

- <img src="https://www.vectorlogo.zone/logos/jetbrains/jetbrains-icon.svg" alt="IntelliJ Community" width="16" height="16"> IntelliJ Community: O IntelliJ Community é um ambiente de desenvolvimento integrado (IDE) popular para desenvolvimento de software em várias linguagens, incluindo Java e Kotlin. Certifique-se de ter o IntelliJ Community instalado em seu sistema. Você pode baixar e instalar o IntelliJ Community a partir do site oficial da JetBrains.

- <img src="https://www.vectorlogo.zone/logos/dbeaverio/dbeaverio-icon.svg" alt="DBeaver" width="16" height="16"> DBeaver: O DBeaver é uma ferramenta de gerenciamento de banco de dados universal e gratuita que suporta vários bancos de dados, incluindo o PostgreSQL. Certifique-se de ter o DBeaver instalado em seu sistema. Você pode baixar e instalar o DBeaver a partir do site oficial do DBeaver ou usar um gerenciador de pacotes, como o apt no Ubuntu, para instalá-lo.

- <img src="https://www.vectorlogo.zone/logos/postgresql/postgresql-icon.svg" alt="PostgreSQL" width="16" height="16"> PostgreSQL: O PostgreSQL é um sistema de gerenciamento de banco de dados relacional de código aberto e poderoso. Certifique-se de ter o PostgreSQL instalado em seu sistema. Você pode baixar e instalar o PostgreSQL a partir do site oficial do PostgreSQL ou seguir as instruções específicas para sua distribuição Linux.

#### Linux-Ubuntu:
* Java 11:
```shell
  sudo apt update
  sudo apt install openjdk-11-jdk
```

* Kotlin:
```shell
  sudo apt install kotlin
```
* Gradle:
```shell
  sudo apt install gradle
```
* Docker + Compose:
```shell
sudo apt install docker.io
sudo usermod -aG docker $USER
sudo apt install docker-compose-plugin
```

* Postman:
  * ⚙️ [Postman download](https://www.postman.com/downloads/)

* Intellij Community Edition:
  * 🧰 [Intellij CE Download](https://www.jetbrains.com/idea/download)

* DBeaver Community:
  * 🐿️ [DBeaver CE Download](https://dbeaver.io/download/)

## Subir tudo com o docker de uma unica vez
na raiz do projeto entre com os comandos abaixo
## * Docker Compose
```shell
  cd app
  docker compose -f docker-compose-allcomponents.yml up -d
```

## Subindo manualmente

### Subir imagens docker (openvidu, postgresql)
Levantando dependencias externas da aplicação, que são necessárias antes de levantar a aplicação.
Na raiz do projeto digite os comandos abaixo após todos os recursos anteriores já terem sidos instalados e configurados
corretamente.
```shell
  cd app/
  docker compose up -d
```
## Build
Entre na pasta app na raiz do projeto caso não esteja e execute o comando abaixo
```sh
./gradlew clean build
```

## Iniciando a aplicação
Ainda dentro da pasta app, entre na pasta build/libs e execute o arquivo .jar terá dois arquivos de nomes parecidos, porém,
um contém "-plain" na composição do nome dor arquivo e não é o que queremos.
```shell
cd build/libs
java -jar entretien-backend-app-0.0.1-SNAPSHOT.jar
```

## Usando a ``API`` com o postman
Importe os arquivos da pasta collections na raiz do projeto.
ao importar use a environment local para criar os recursos usando a estrutura disponível.
* pasta com os arquivos ``entretien-rh-be/collection/``
  * Importe os arquivos listados abaixo, no postman.
* Postman Collection
```text
EntretienBackend.postman_collection.json
```
* Postman Environment
```text
local.postman_environment.json
```

## Usando a ``API`` com OpenAPI v3.0
Use o endpoint do swagger-ui para interagir com a api de forma simplificada, ao usar esse recurso é possível ter acesso
a todos os endpoints da aplicação além de todos os schemas usados, podendo ser facilmente substituídos e tem integração
direta com a aplicação.

* acessar usando a url [clicando aqui](http://localhost:5000/api/swagger-ui/index.html)

## Usando a ``API`` com o frontend
Rode o projeto frontend [clicando aqui](https://github.com/netomantonio/entretien-fe/) para abrir o projeto no github e seguir as instruções.

## Author

👤 **Entretien**

* Website: www.entretien.com.br
* Github: [@netomantonio](https://github.com/netomantonio) \ [@leonardowrobel](https://github.com/leonardowrobel)
* LinkedIn: [@amneto](https://linkedin.com/in/amneto) \ [@leonardowrobel](https://linkedin.com/in/leonardo-wrobel-b26b07189)

## Show your support

Give a ⭐️ if this project helped you!

## 📝 License

Copyright © 2023 [Entretien](https://github.com/netomantonio).<br />
This project is [The GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.html) licensed.

***
_This README was generated with ❤️ by [readme-md-generator](https://github.com/kefranabg/readme-md-generator)_