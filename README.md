# WALLET-DATA

## Introduction
The Wallet-Data microservice is a pivotal component designed for storing end-user information within a digital wallet environment. This service stands out for its modularity and its capability to communicate seamlessly with the Orion-LD Adapter, another crucial component within our ecosystem.

Built using Java 17 and Spring WebFlux, the service operates on a reactive programming paradigm, ensuring efficient resource utilization and responsive interactions. The project is managed with Gradle, simplifying dependency management and build processes.

## Main Features
* User Data Storage: Store user data by providing a steadfast service designed to manage crucial information belonging to end-users of the wallet
* Data Retrieval: Quickly and efficiently retrieve user data, ensuring that end-users and other dependent services have timely access to the information they need.
* Data Management: Easily manage user data, providing functionalities to update, delete, or modify stored information as per application requirements or user requests.
* Reactive API: Benefit from a responsive and non-blocking API, ensuring that user interactions remain swift, even under heavy load or during complex operations.

## Flows

### Register User
[![](https://www.mermaidchart.com/raw/c8cba564-fdac-4fdd-93db-7e960315d1e4?version=v0.1&theme=light&format=svg)](https://www.mermaidchart.com/raw/c8cba564-fdac-4fdd-93db-7e960315d1e4?version=v0.1&theme=light&format=svg)

> Note:These are two examples of how the get and delete methods work for credentials, but the other get and delete methods operate in a very similar manner. The methods for adding a new credential work the same as delete, but instead of removing it from the list, we add it to the existing list.
### Get User Credentials
[![](https://www.mermaidchart.com/raw/5312193f-2f6e-421b-bbde-d5ed3297b94f?version=v0.1&theme=light&format=svg)](https://www.mermaidchart.com/raw/5312193f-2f6e-421b-bbde-d5ed3297b94f?version=v0.1&theme=light&format=svg)

### Delete User Credentials
[![](https://www.mermaidchart.com/raw/3562c7aa-ee49-4ab2-b32e-e69060fdc46d?version=v0.1&theme=light&format=svg)](https://www.mermaidchart.com/raw/3562c7aa-ee49-4ab2-b32e-e69060fdc46d?version=v0.1&theme=light&format=svg)

## Getting Started
### Prerequisites
- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Gradle](https://gradle.org/install/)
- [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Docker Desktop](https://www.docker.com/)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- [Git](https://git-scm.com/)
- [Orion-LD-Adapter](https://github.com/in2workspace/in2-orionld-adapter.git)

## Api references (Local-docker Environment)
* Swagger: http://localhost:8086/swagger-ui.html
* OpenAPI: http://localhost:8086/api-docs

## Document version
* v0.0.0