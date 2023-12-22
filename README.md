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
The Wallet-Data microservice is a pivotal component designed for storing and managing end-user information within a digital wallet environment. This service is distinguished by its modularity and its ability to interact seamlessly with key components in our ecosystem: the Broker-Adapter and the Wallet-Crypto module. The Broker-Adapter plays a vital role in persisting data in a context broker using NGSI-LD standards, while the Wallet-Crypto module ensures the secure handling of cryptographic elements associated with user data, particularly in the management and deletion of decentralized identifiers (DIDs) and their corresponding private keys.

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
* Broker-Adapter: This component plays a crucial role in receiving and managing user data. Whenever a new user-related entity is created or existing data is updated, Broker-Adapter is responsible for persisting these changes in a context broker operating with NGSI-LD standards. For detailed installation instructions, refer to the [Broker-Adapter Configuration Component.](https://github.com/in2workspace/broker-adapter)

* Wallet-Crypto: This component is critical for handling cryptographic elements associated with user data. Specifically, it ensures that when a DID (Decentralized Identifier) linked to a private key is deleted, Wallet-Crypto is called upon to securely erase the corresponding private key. For its installation, follow the guide provided here: [Wallet-Crypto Configuration Component.](https://github.com/in2workspace/wallet-crypto)

Once you have these dependencies set up and running, you can proceed with configuring the Wallet-Data.

## Configuration
Now that you have the necessary dependencies, you can configure the wallet-data using the following docker-compose. Ensure to adjust the environment variables to match your Broker-Adapter and Wallet-Crypto configurations.
* Wallet-Crypto Configuration
```yaml
wallet-data:
  image: in2kizuna/wallet-data:v2.0.0
  environment:
    OPENAPI_SERVER_URL: "http://localhost:8086"
    BROKER-ADAPTER_URL: "http://broker-adapter:8080"
    WALLET-CRYPTO_URL: "http://wallet-crypto:8080"
  ports:
    - "8086:8080"
  links:
    - broker-adapter
```
## Project Status
The project is currently at version **2.0.1** and is in a stable state.

## Contact
For any inquiries or collaboration, you can contact us at:
* **Email:** [info@in2.es](mailto:info@in2.es)
* **Name:** IN2, Ingeniería de la Información
* **Website:** [https://in2.es](https://in2.es)

## Creation Date and Update Dates
* **Creation Date:** October 26, 2023
* **Last Updated:** December 5, 2023

