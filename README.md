# 🚀 Entretien-RH
Este projeto é a aplicação backend do Entretien-RH, uma plataforma de gerenciamento de entrevistas por competência para RH. O objetivo é facilitar o agendamento de entrevistas por parte dos candidatos e garantir que pequenas e médias empresas tenham a oportunidade de avaliar os candidatos conforme o perfil desejado e ter maior assertividade na escolha do seu novo talento.

A aplicação backend é escrita em Kotlin e utiliza o framework Spring Boot e o gerenciador de dependências Gradle. A comunicação com o banco de dados PostgreSQL e o serviço de videochamadas é feita através do OpenVidu Server.

## 🛠️ Tecnologias Utilizadas
🚀 Kotlin - Linguagem de programação utilizada na aplicação. [Site Oficial](https://kotlinlang.org/)  
🌱 Spring Boot - Framework utilizado para desenvolver a aplicação web. [Site Oficial](https://spring.io/projects/spring-boot)  
🔗 Gradle - Gerenciador de dependências utilizado para o desenvolvimento da aplicação. [Site Oficial](https://gradle.org/)  
🐘 PostgreSQL - Banco de dados utilizado para armazenar os dados da aplicação. [Site Oficial](https://www.postgresql.org/)  
🎥 OpenVidu Server - Serviço de videochamadas utilizado na aplicação. [Site Oficial](https://openvidu.io/)  

A aplicação está contida na pasta "app", enquanto os arquivos externos à pasta "app" são responsáveis pela infraestrutura da aplicação.

## 🚀 Infraestrutura
A aplicação utiliza as seguintes tecnologias de infraestrutura:

🤖 GitHub Actions - Integração contínua e implantação contínua (CI/CD) da aplicação. [Site Oficial](https://github.com/features/actions)  
☁️ Amazon RDS PostgreSQL - Banco de dados PostgreSQL gerenciado na nuvem da Amazon Web Services. [Site Oficial](https://aws.amazon.com/rds/postgresql/)  
🚀 Amazon ECS - Serviço de contêiner da Amazon Web Services usado para implantar e gerenciar os contêineres da aplicação. [Site Oficial](https://aws.amazon.com/ecs/)  
📦 Amazon ECR - Repositório de contêiner gerenciado na nuvem da Amazon Web Services usado para registrar a imagem Docker da aplicação. [Site Oficial](https://aws.amazon.com/ecr/)  

###### A aplicação está configurada para os ambientes de desenvolvimento, homologação e produção.
