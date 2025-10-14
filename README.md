
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