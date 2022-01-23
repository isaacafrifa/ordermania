# Ordermainia

This project is the backend of an inventory management system that is implemented 
using Java Spring boot.


### Table of contents
* [Description](#description)
* [Motivation](#motivation)
* [Technologies](#technologies)
* [Run Locally](#run-locally)
* [Usage](#usage)
* [Future Additions](#future-additions)


## Description
This application uses the microservices architecture and currently comprises of 4 
services namely: the discovery service(EurekaServer), product service, 
inventory service and notification service. Ordermania is fault tolerant and 
also utilizes messaging queues. In addition, all services are containerized.


### Motivation 
The main motivation was to build an robust application using modern technologies
such as Resilience4J, RabbitMQ, Docker Compose etc. 


## Technologies
+ Primary Language: Java Spring Boot 
+ Docker 
+ Netflix Eureka 
+ Resilience4J - for fault tolerance
+ RabbitMQ - Message Broker
+ MySQL 


