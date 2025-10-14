# `Project`: Onboarding Exercise 1

This project implements a **Spring Boot 3.5.6** system designed to manage **Person** entities. It utilizes **PostgreSQL** as the persistent database and **Kafka** for asynchronous event handling. The system is built with **Java 17** and supports containerized deployment using **Docker** and **Kubernetes**.

## 🚀 Overview

The system consists of two main microservice modules and shared libraries:

| Module | Description                                                                                                 |
| :--- |:------------------------------------------------------------------------------------------------------------|
| **`onboarding-exercise-1`** | Service handling **Person** entity management (CRUD operations) and **Kafka** event production/consumption. |
| **`onboarding-exercise-1-cron`** | A scheduled service module that periodically calls the core microservice's **REST API**.                    |
| **`shared`** | Contains shared libraries, domain models, and common utilities used across all modules.                     |

-----

## 🏗️ Project Structure

```
onboarding-exercise-1/
├── onboarding-exercise-1/        # Service handling Person entity
├── onboarding-exercise-1-cron/   # Scheduled service module calling REST API
├── shared/                       # Shared libraries 
├── docker.env                   # Docker environment variables for builds
├── k8s.env                      # Kubernetes environment variables for ConfigMaps
├── docker-compose.yaml           # Compose file running PostgreSQL, Kafka containers
├── k8s/                         # Kubernetes manifests (deployments, services, etc.)
└── README.md                    # This documentation file
```

-----

## 🛠️ Infrastructure Setup with Docker Compose

The `docker-compose.yaml` file is configured to provision all necessary infrastructure services required for local development.

### Required Services

* **PostgreSQL**: The persistent relational database.
* **Kafka**: The message broker for asynchronous event handling.

### Starting Infrastructure

To start the required services, run the following command from the project root:

```bash
docker-compose up -d
```

> **Note**: This command only starts the infrastructure containers (PostgreSQL, Kafka, Liquibase). The application modules (`onboarding-exercise-1` and `onboarding-exercise-1-cron`) are **not** included and must be started separately.

-----

## 💻 Running Locally

Once the infrastructure is running, you can start the application modules directly on your host machine or via your IDE.

1.  **Start Infrastructure:**
    ```bash
    docker-compose up -d
    ``` 
2**Update Database Schema** after load Maven Dependencies
    ```bash
    cd onboarding-exercise-1/onboarding-excercise-1-db-migration
    mvn liquibase:update
    ```
3**Build and Run Application:**
    * **Via Maven** 

-----

## ☁️ Building and Deploying to Kubernetes

The application is built to be deployed into a **Kubernetes** cluster, with images built using **JIB**.

### 1\. Build & Push Docker Images

Use the Maven JIB plugin to build the Docker images for each service. 

```bash
#Login to Docker
docker login
```


```bash
# Build core microservice image
cd onboarding-exercise-1/onboarding-excercise-1-app
mvn compile jib:build

# Build scheduled service image
cd onboarding-exercise-1/onboarding-excercise-1-cron-app
mvn compile jib:build
```

### 3\. Deploy to Kubernetes

The `k8s/` directory contains all necessary Kubernetes manifests (Deployments, Services, ConfigMaps, Secrets) to run the system.

Apply the manifest files to your cluster:

```bash
kubectl apply -f k8s/
```

### 4\. Verification

Check the status of your deployed pods and services:

```bash
kubectl get pods
kubectl get svc
```

> **Note on Configuration**: Environment variables for the application are configured via **Kubernetes ConfigMaps** (using values from `k8s.env`) and **Secrets** for sensitive information. The application startup and lifecycle are managed independently by Kubernetes from the shared infrastructure components (Kafka and PostgreSQL).

-----

## ⚙️ Configuration & Technology Stack

* **Runtime:** Java 17
* **Framework:** Spring Boot 3.5.6
* **Database:** PostgreSQL
* **Messaging:** Apache Kafka
* **Database Migration:** Liquibase
* **Containerization:** Docker / JIB
* **Orchestration:** Kubernetes