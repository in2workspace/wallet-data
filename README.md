<div align="center">

<h1>Wallet Data</h1>
<span>by </span><a href="https://in2.es">in2.es</a>
<p><p>


[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=in2workspace_wallet-data&metric=alert_status)](https://sonarcloud.io/dashboard?id=in2workspace_wallet-data)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=in2workspace_wallet-data&metric=bugs)](https://sonarcloud.io/summary/new_code?id=in2workspace_wallet-data)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=in2workspace_wallet-data&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=in2workspace_wallet-data)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=in2workspace_wallet-data&metric=security_rating)](https://sonarcloud.io/dashboard?id=in2workspace_wallet-data)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=in2workspace_wallet-data&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=in2workspace_wallet-data)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=in2workspace_wallet-data&metric=ncloc)](https://sonarcloud.io/dashboard?id=in2workspace_wallet-data)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=in2workspace_wallet-data&metric=coverage)](https://sonarcloud.io/summary/new_code?id=in2workspace_wallet-data)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=in2workspace_wallet-data&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=in2workspace_wallet-data)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=in2workspace_wallet-data&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=in2workspace_wallet-data)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=in2workspace_wallet-data&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=in2workspace_wallet-data)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=in2workspace_wallet-data&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=in2workspace_wallet-data)

</div>

## Introduction
The Wallet-Data microservice is a key component in the digital wallet environment, focusing on storing and managing end-user information. Notable for its modularity, the service now directly interacts with the Context Broker, aligning with NGSI-LD standards. The integration of the Wallet-Crypto module remains crucial, handling cryptographic elements tied to user data, especially in managing and deleting decentralized identifiers (DIDs) and their corresponding private keys.

Built using Java 17 and Spring WebFlux, the service operates on a reactive programming paradigm, ensuring efficient resource utilization and responsive interactions. The project is managed with Gradle, simplifying dependency management and build processes.

## Main Features
* User Data Storage: Store user data by providing a steadfast service designed to manage crucial information belonging to end-users of the wallet
* Data Retrieval: Quickly and efficiently retrieve user data, ensuring that end-users and other dependent services have timely access to the information they need.
* Data Management: Easily manage user data, providing functionalities to update, delete, or modify stored information as per application requirements or user requests.
* Reactive API: Benefit from a responsive and non-blocking API, ensuring that user interactions remain swift, even under heavy load or during complex operations.

## Installation
### Prerequisites
- [Docker Desktop](https://www.docker.com/)
- [Git](https://git-scm.com/)

### Dependencies for Installation
To install and run the Wallet-Data, you will need the following dependencies:
* Context Broker: Wallet-Data now directly interfaces with the Context Broker using NGSI-LD standards for handling user data.
* Wallet-Crypto: This component is critical for handling cryptographic elements associated with user data. Specifically, it ensures that when a DID (Decentralized Identifier) linked to a private key is deleted, Wallet-Crypto is called upon to securely erase the corresponding private key. For its installation, follow the guide provided here: [Wallet-Crypto Configuration Component.](https://github.com/in2workspace/wallet-crypto)

Once you have these dependencies set up and running, you can proceed with configuring the Wallet-Data.

## Configuration
Configure wallet-data with this docker-compose, aligning environment variables with your Context Broker and Wallet-Crypto settings.
* Wallet-Crypto Configuration
```yaml
wallet-data:
  image: in2kizuna/wallet-data:v2.0.0
  environment:
    OPENAPI_SERVER_URL: "http://localhost:8086"
    BROKER_DOMAIN: "http://scorpio:9090"
    BROKER_URL: "/ngsi-ld/v1/entities"
    WALLET-CRYPTO_DOMAIN: "http://wallet-crypto:8080"
    WALLET-CRYPTO_URL: "/api/v2/secrets"
  ports:
    - "8086:8080"
```
## Project Status
The project is currently at version **2.0.0** and is in a stable state.

## Contact
For any inquiries or collaboration, you can contact us at:
* **Email:** [info@in2.es](mailto:info@in2.es)
* **Name:** IN2, Ingeniería de la Información
* **Website:** [https://in2.es](https://in2.es)

## Creation Date and Update Dates
* **Creation Date:** October 26, 2023
* **Last Updated:** January 2, 2024

