# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v2.0.0] - 2023-12-12

### Added
- Enabled centralized cross-origin resource sharing (CORS) to allow frontend applications to call the endpoints.
- Set the frontend URL dynamically through an external environment variable, enhancing configuration flexibility.
- Checkstyle for code quality.
- Add support for GitHub Actions for CI/CD.

## [v1.0.0] - 2023-12-4

### Added
- Capability to store user data, providing a reliable service for managing crucial information of wallet end-users.
- Functionality for quick and efficient retrieval of user data, ensuring timely access for end-users and other dependent services.
- Features for easy management of user data, including updating, deleting, or modifying stored information as required.
- Implementation of a responsive and non-blocking API to maintain swift user interactions under heavy load or during complex operations.
- Integration with Broker-Adapter for managing user data changes in a context broker with NGSI-LD standards.
- Integration with Wallet-Crypto for secure handling of cryptographic elements, particularly for the management and deletion of private keys associated to DIDs.
- Docker-compose configuration for easy deployment and setup
- Project status, contact information, and creation/update dates in README.

[release]:
[1.0.0]: https://github.com/in2workspace/wallet-data/releases/tag/v1.0.0
[2.0.0]: https://github.com/in2workspace/wallet-data/releases/tag/v2.0.0
