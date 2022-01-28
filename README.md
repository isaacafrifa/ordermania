
# Ordermania

This project is the backend of an inventory management system that is implemented 
using Java Spring boot.


### Table of contents
* Description
* Motivation
* Technologies
* Run Locally
* Usage
* Future Additions


## Description
This application uses the microservices architecture and currently comprises of 4 
services namely: the discovery service(EurekaServer), product service, 
inventory service and notification service. Ordermania is fault tolerant and 
also utilizes messaging queues. In addition, all services are containerized.


## Motivation 
The main motivation was to build an robust application using modern technologies
such as Resilience4J, RabbitMQ, Docker Compose etc. 


## Technologies

+ Primary Language: Java Spring Boot 
+ Docker 
+ Netflix Eureka 
+ Resilience4J - for fault tolerance
+ RabbitMQ 
+ MySQL 


## Run Locally

Clone the project

```bash
  git clone https://mrBlo@bitbucket.org/mrBlo/ordermania.git  #for bitbucket
  git clone https://github.com/isaacafrifa/ordermania.git  #for github
```

Go to the project directory

```bash
  cd ordermania
```

NOTE: You need [Maven](https://maven.apache.org/users/index.html), 
[Docker](https://docs.docker.com/get-docker), 
[Docker Compose](https://docs.docker.com/compose/install) installed

#### Main Steps:
+ Navigate to EurekaServer
```bash
  cd EurekaServer
```

+ Build and package jar using maven (mvn clean if needed)
```bash
  mvn clean package -DskipTests
```

+ Start the docker compose of EurekaServer (docker-compose file has two services:
discovery service and rabbitmq service) 
```bash
  docker compose up
```

+ Stop the services using 
```bash
  docker compose down
```

Apply the above steps to product-service, inventory-service and notification-service 
(in that order). 
#### Notable Ports
Access the services on the following urls:
+ discovery-service (Eureka) - http://localhost:8761 
+ rabbitmq-service - http://localhost:15672
Check for the ports of the other services from their respective docker-compose files 
or property files.


## Usage

#### Basic Flow:
+ Create product
+ Create inventory with product
+ Notification occurs when inventory item is created, edited or deleted.


## Future Work

To be considered ....
