# ============================================
# BLINK E-Commerce - Minikube Deployment Script
# ============================================

# Renklendirme
function Write-Step($step, $message) {
    Write-Host "`n[$step] $message" -ForegroundColor Cyan
}

function Write-Success($message) {
    Write-Host "  ✅ $message" -ForegroundColor Green
}

function Write-Wait($message) {
    Write-Host "  ⏳ $message" -ForegroundColor Yellow
}

function Write-Info($message) {
    Write-Host "  ➡️ $message" -ForegroundColor White
}

# Banner
Write-Host ""
Write-Host "============================================================" -ForegroundColor Blue
Write-Host "       BLINK E-Commerce - Minikube Deployment              " -ForegroundColor Blue
Write-Host "============================================================" -ForegroundColor Blue
Write-Host ""

# Proje dizinine git
$projectRoot = "C:\Users\tarka\Workspace\blink"
Set-Location $projectRoot
Write-Info "Working directory: $projectRoot"

# ============================================
# ADIM 1: Minikube Docker Ortamına Bağlan
# ============================================
Write-Step "1/10" "Connecting to Minikube Docker environment..."
& minikube -p minikube docker-env --shell powershell | Invoke-Expression
Write-Success "Connected to Minikube Docker"

# ============================================
# ADIM 2: Docker Image'ları Build Et
# ============================================
Write-Step "2/10" "Building Docker images..."

$services = @(
    @{Name="discovery-server"; Port=8761},
    @{Name="api-gateway"; Port=8080},
    @{Name="user-service"; Port=8081},
    @{Name="product-service"; Port=8082},
    @{Name="cart-service"; Port=8083},
    @{Name="order-service"; Port=8084},
    @{Name="notification-service"; Port=8085}
)

foreach ($service in $services) {
    $name = $service.Name
    Write-Info "Building $name..."
    docker build -t "blink/$name`:latest" "./$name" 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Success "$name built"
    } else {
        Write-Host "  ❌ Failed to build $name" -ForegroundColor Red
        exit 1
    }
}

Write-Success "All images built successfully"

# ============================================
# ADIM 3: Namespace Oluştur
# ============================================
Write-Step "3/10" "Creating namespace..."
kubectl delete namespace blink --ignore-not-found=true 2>&1 | Out-Null
Start-Sleep -Seconds 5
kubectl create namespace blink
Write-Success "Namespace 'blink' created"

# ============================================
# ADIM 4: Secrets Oluştur
# ============================================
Write-Step "4/10" "Creating secrets..."

# MongoDB Secret
kubectl create secret generic mongodb-secret `
    --from-literal=mongo-root-username=root `
    --from-literal=mongo-root-password=rootpassword `
    -n blink
Write-Success "MongoDB secret created"

# Redis Secret
kubectl create secret generic redis-secret `
    --from-literal=redis-password=redispassword `
    -n blink
Write-Success "Redis secret created"

# JWT Secret
kubectl create secret generic user-service-secret `
    --from-literal=jwt-secret=mySecretKeyForJWTTokenGenerationMinimum256BitsLong123456789 `
    -n blink
Write-Success "JWT secret created"

# ============================================
# ADIM 5: MongoDB Deploy
# ============================================
Write-Step "5/10" "Deploying MongoDB..."

# PowerShell heredoc çalışmıyor, ayrı dosya kullanalım
@"
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mongodb-pvc
  namespace: blink
spec: 
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
"@ | kubectl apply -f -

@"
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mongodb
  namespace: blink
spec: 
  replicas: 1
  selector:
    matchLabels: 
      app: mongodb
  template:
    metadata:
      labels:
        app: mongodb
    spec:
      containers:
      - name: mongodb
        image: mongo:7.0
        ports:
        - containerPort: 27017
        env:
        - name: MONGO_INITDB_ROOT_USERNAME
          valueFrom:
            secretKeyRef: 
              name: mongodb-secret
              key: mongo-root-username
        - name: MONGO_INITDB_ROOT_PASSWORD
          valueFrom:
            secretKeyRef:
              name: mongodb-secret
              key: mongo-root-password
        resources:
          requests:
            memory: "256Mi"
            cpu: "200m"
          limits:
            memory: "512Mi"
            cpu: "500m"
"@ | kubectl apply -f -

@"
apiVersion: v1
kind: Service
metadata:
  name: mongodb
  namespace: blink
spec:
  selector:
    app: mongodb
  ports:
  - port: 27017
    targetPort: 27017
"@ | kubectl apply -f -

Write-Success "MongoDB deployed"

# ============================================
# ADIM 6: Redis Deploy
# ============================================
Write-Step "6/10" "Deploying Redis..."

@"
apiVersion: apps/v1
kind: Deployment
metadata:
  name: redis
  namespace: blink
spec:
  replicas: 1
  selector:
    matchLabels:
      app: redis
  template:
    metadata: 
      labels:
        app: redis
    spec:
      containers: 
      - name: redis
        image: redis:7-alpine
        ports:
        - containerPort: 6379
        args:  ["--requirepass", "`$(REDIS_PASSWORD)"]
        env:
        - name: REDIS_PASSWORD
          valueFrom:
            secretKeyRef: 
              name: redis-secret
              key: redis-password
        resources:
          requests:
            memory: "64Mi"
            cpu: "100m"
          limits:
            memory: "128Mi"
            cpu: "200m"
"@ | kubectl apply -f -

@"
apiVersion: v1
kind: Service
metadata:
  name: redis
  namespace: blink
spec:
  selector:
    app: redis
  ports:
  - port: 6379
    targetPort: 6379
"@ | kubectl apply -f -

Write-Success "Redis deployed"

# ============================================
# ADIM 7: Kafka & Zookeeper Deploy
# ============================================
Write-Step "7/10" "Deploying Zookeeper & Kafka..."

# Zookeeper
@"
apiVersion: apps/v1
kind: Deployment
metadata:
  name: zookeeper
  namespace: blink
spec:
  replicas: 1
  selector:
    matchLabels:
      app: zookeeper
  template:
    metadata:
      labels:
        app: zookeeper
    spec:
      containers:
      - name: zookeeper
        image: confluentinc/cp-zookeeper:7.5.0
        ports:
        - containerPort: 2181
        env:
        - name: ZOOKEEPER_CLIENT_PORT
          value: "2181"
        - name: ZOOKEEPER_TICK_TIME
          value: "2000"
        resources:
          requests:
            memory: "256Mi"
            cpu: "200m"
          limits:
            memory: "512Mi"
            cpu: "500m"
"@ | kubectl apply -f -

@"
apiVersion: v1
kind: Service
metadata:
  name: zookeeper
  namespace: blink
spec: 
  selector:
    app: zookeeper
  ports:
  - port: 2181
    targetPort: 2181
"@ | kubectl apply -f -

Write-Wait "Waiting 20s for Zookeeper..."
Start-Sleep -Seconds 20

# Kafka
@"
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kafka
  namespace: blink
spec:
  replicas: 1
  selector:
    matchLabels:
      app: kafka
  template:
    metadata:
      labels:
        app: kafka
    spec:
      containers:
      - name: kafka
        image: confluentinc/cp-kafka:7.5.0
        ports:
        - containerPort: 9092
        env:
        - name: KAFKA_BROKER_ID
          value: "1"
        - name: KAFKA_ZOOKEEPER_CONNECT
          value: "zookeeper:2181"
        - name: KAFKA_ADVERTISED_LISTENERS
          value: "PLAINTEXT://kafka:9092"
        - name: KAFKA_LISTENER_SECURITY_PROTOCOL_MAP
          value: "PLAINTEXT:PLAINTEXT"
        - name: KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR
          value: "1"
        - name: KAFKA_TRANSACTION_STATE_LOG_MIN_ISR
          value: "1"
        - name: KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR
          value: "1"
        resources:
          requests:
            memory: "256Mi"
            cpu: "200m"
          limits:
            memory: "512Mi"
            cpu: "500m"
"@ | kubectl apply -f -

@"
apiVersion: v1
kind: Service
metadata:
  name: kafka
  namespace: blink
spec:
  selector:
    app: kafka
  ports:
  - port: 9092
    targetPort: 9092
"@ | kubectl apply -f -

Write-Success "Zookeeper & Kafka deployed"
Write-Wait "Waiting 30s for infrastructure to be ready..."
Start-Sleep -Seconds 30

# ============================================
# ADIM 8: Discovery Server Deploy
# ============================================
Write-Step "8/10" "Deploying Discovery Server..."

@"
apiVersion: apps/v1
kind: Deployment
metadata:
  name: discovery-server
  namespace: blink
spec:
  replicas: 1
  selector:
    matchLabels:
      app: discovery-server
  template:
    metadata:
      labels:
        app: discovery-server
    spec:
      containers:
      - name: discovery-server
        image: blink/discovery-server:latest
        imagePullPolicy: Never
        ports:
        - containerPort: 8761
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        resources:
          requests:
            memory: "256Mi"
            cpu: "200m"
          limits:
            memory: "512Mi"
            cpu: "500m"
"@ | kubectl apply -f -

@"
apiVersion: v1
kind: Service
metadata:
  name: discovery-server
  namespace: blink
spec: 
  selector:
    app: discovery-server
  ports:
  - port: 8761
    targetPort: 8761
"@ | kubectl apply -f -

Write-Success "Discovery Server deployed"
Write-Wait "Waiting 45s for Discovery Server to be ready..."
Start-Sleep -Seconds 45

# ============================================
# ADIM 9: Application Services Deploy
# ============================================
Write-Step "9/10" "Deploying Application Services..."

# User Service
Write-Info "Deploying User Service..."
@"
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
  namespace: blink
spec:
  replicas: 1
  selector:
    matchLabels:
      app: user-service
  template:
    metadata:
      labels:
        app: user-service
    spec:
      containers:
      - name: user-service
        image: blink/user-service:latest
        imagePullPolicy: Never
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
          value: "http://discovery-server:8761/eureka/"
        - name: SPRING_DATA_MONGODB_HOST
          value: "mongodb"
        - name: SPRING_DATA_MONGODB_PORT
          value: "27017"
        - name: SPRING_DATA_MONGODB_DATABASE
          value: "blink_users"
        - name: SPRING_DATA_MONGODB_USERNAME
          valueFrom:
            secretKeyRef: 
              name: mongodb-secret
              key: mongo-root-username
        - name: SPRING_DATA_MONGODB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: mongodb-secret
              key: mongo-root-password
        - name: SPRING_DATA_MONGODB_AUTHENTICATION_DATABASE
          value: "admin"
        - name: JWT_SECRET
          valueFrom: 
            secretKeyRef:
              name: user-service-secret
              key: jwt-secret
        resources:
          requests:
            memory: "256Mi"
            cpu: "200m"
          limits:
            memory: "512Mi"
            cpu: "500m"
"@ | kubectl apply -f -

@"
apiVersion: v1
kind: Service
metadata:
  name: user-service
  namespace: blink
spec:
  selector:
    app: user-service
  ports:
  - port: 8081
    targetPort: 8081
"@ | kubectl apply -f -

# Product Service
Write-Info "Deploying Product Service..."
@"
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-service
  namespace: blink
spec:
  replicas: 1
  selector:
    matchLabels:
      app: product-service
  template:
    metadata:
      labels:
        app: product-service
    spec:
      containers:
      - name: product-service
        image: blink/product-service:latest
        imagePullPolicy: Never
        ports:
        - containerPort: 8082
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
          value: "http://discovery-server:8761/eureka/"
        - name: SPRING_DATA_MONGODB_HOST
          value: "mongodb"
        - name: SPRING_DATA_MONGODB_PORT
          value: "27017"
        - name: SPRING_DATA_MONGODB_DATABASE
          value: "blink_products"
        - name: SPRING_DATA_MONGODB_USERNAME
          valueFrom:
            secretKeyRef:
              name: mongodb-secret
              key: mongo-root-username
        - name:  SPRING_DATA_MONGODB_PASSWORD
          valueFrom:
            secretKeyRef:
              name:  mongodb-secret
              key: mongo-root-password
        - name: SPRING_DATA_MONGODB_AUTHENTICATION_DATABASE
          value: "admin"
        - name: SPRING_DATA_REDIS_HOST
          value: "redis"
        - name: SPRING_DATA_REDIS_PORT
          value: "6379"
        - name: SPRING_DATA_REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: redis-secret
              key: redis-password
        resources:
          requests:
            memory: "256Mi"
            cpu: "200m"
          limits:
            memory: "512Mi"
            cpu: "500m"
"@ | kubectl apply -f -

@"
apiVersion: v1
kind: Service
metadata:
  name: product-service
  namespace: blink
spec:
  selector:
    app: product-service
  ports:
  - port: 8082
    targetPort: 8082
"@ | kubectl apply -f -

# Cart Service
Write-Info "Deploying Cart Service..."
@"
apiVersion: apps/v1
kind: Deployment
metadata:
  name: cart-service
  namespace: blink
spec:
  replicas: 1
  selector:
    matchLabels:
      app: cart-service
  template:
    metadata:
      labels:
        app: cart-service
    spec:
      containers:
      - name: cart-service
        image: blink/cart-service:latest
        imagePullPolicy: Never
        ports:
        - containerPort: 8083
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
          value: "http://discovery-server:8761/eureka/"
        - name: SPRING_DATA_REDIS_HOST
          value: "redis"
        - name: SPRING_DATA_REDIS_PORT
          value: "6379"
        - name: SPRING_DATA_REDIS_PASSWORD
          valueFrom:
            secretKeyRef: 
              name: redis-secret
              key: redis-password
        resources:
          requests:
            memory: "256Mi"
            cpu: "200m"
          limits:
            memory: "512Mi"
            cpu: "500m"
"@ | kubectl apply -f -

@"
apiVersion: v1
kind: Service
metadata:
  name: cart-service
  namespace: blink
spec:
  selector:
    app: cart-service
  ports:
  - port: 8083
    targetPort: 8083
"@ | kubectl apply -f -

# Order Service
Write-Info "Deploying Order Service..."
@"
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
  namespace: blink
spec:
  replicas: 1
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
      - name: order-service
        image: blink/order-service:latest
        imagePullPolicy: Never
        ports:
        - containerPort: 8084
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
          value: "http://discovery-server:8761/eureka/"
        - name: SPRING_DATA_MONGODB_HOST
          value: "mongodb"
        - name: SPRING_DATA_MONGODB_PORT
          value: "27017"
        - name: SPRING_DATA_MONGODB_DATABASE
          value: "blink_orders"
        - name: SPRING_DATA_MONGODB_USERNAME
          valueFrom:
            secretKeyRef: 
              name: mongodb-secret
              key: mongo-root-username
        - name: SPRING_DATA_MONGODB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: mongodb-secret
              key: mongo-root-password
        - name: SPRING_DATA_MONGODB_AUTHENTICATION_DATABASE
          value: "admin"
        - name: SPRING_KAFKA_BOOTSTRAP_SERVERS
          value: "kafka:9092"
        resources:
          requests:
            memory: "256Mi"
            cpu: "200m"
          limits:
            memory: "512Mi"
            cpu: "500m"
"@ | kubectl apply -f -

@"
apiVersion: v1
kind: Service
metadata:
  name: order-service
  namespace: blink
spec:
  selector:
    app: order-service
  ports:
  - port: 8084
    targetPort: 8084
"@ | kubectl apply -f -

# Notification Service
Write-Info "Deploying Notification Service..."
@"
apiVersion: apps/v1
kind: Deployment
metadata:
  name: notification-service
  namespace: blink
spec:
  replicas: 1
  selector:
    matchLabels:
      app: notification-service
  template:
    metadata:
      labels:
        app: notification-service
    spec:
      containers:
      - name: notification-service
        image: blink/notification-service:latest
        imagePullPolicy: Never
        ports:
        - containerPort: 8085
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
          value: "http://discovery-server:8761/eureka/"
        - name: SPRING_DATA_MONGODB_HOST
          value: "mongodb"
        - name: SPRING_DATA_MONGODB_PORT
          value: "27017"
        - name: SPRING_DATA_MONGODB_DATABASE
          value: "blink_notifications"
        - name: SPRING_DATA_MONGODB_USERNAME
          valueFrom:
            secretKeyRef:
              name: mongodb-secret
              key: mongo-root-username
        - name:  SPRING_DATA_MONGODB_PASSWORD
          valueFrom:
            secretKeyRef:
              name:  mongodb-secret
              key: mongo-root-password
        - name: SPRING_DATA_MONGODB_AUTHENTICATION_DATABASE
          value: "admin"
        - name: SPRING_KAFKA_BOOTSTRAP_SERVERS
          value: "kafka:9092"
        resources:
          requests:
            memory: "256Mi"
            cpu: "200m"
          limits:
            memory: "512Mi"
            cpu: "500m"
"@ | kubectl apply -f -

@"
apiVersion: v1
kind: Service
metadata:
  name: notification-service
  namespace: blink
spec:
  selector:
    app: notification-service
  ports:
  - port: 8085
    targetPort: 8085
"@ | kubectl apply -f -

# API Gateway
Write-Info "Deploying API Gateway..."
@"
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-gateway
  namespace: blink
spec:
  replicas: 1
  selector:
    matchLabels:
      app: api-gateway
  template:
    metadata:
      labels:
        app: api-gateway
    spec:
      containers:
      - name: api-gateway
        image: blink/api-gateway:latest
        imagePullPolicy: Never
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
          value: "http://discovery-server:8761/eureka/"
        resources:
          requests:
            memory: "256Mi"
            cpu: "200m"
          limits:
            memory: "512Mi"
            cpu: "500m"
"@ | kubectl apply -f -

@"
apiVersion: v1
kind: Service
metadata:
  name: api-gateway
  namespace: blink
spec:
  type: NodePort
  selector:
    app: api-gateway
  ports:
  - port: 8080
    targetPort: 8080
    nodePort: 30080
"@ | kubectl apply -f -

Write-Success "All application services deployed"

# ============================================
# ADIM 10: Final Status
# ============================================
Write-Step "10/10" "Waiting for all pods to be ready..."
Start-Sleep -Seconds 60

Write-Host ""
Write-Host "============================================================" -ForegroundColor Green
Write-Host "                    DEPLOYMENT STATUS                       " -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green
Write-Host ""

Write-Host "📦 Deployments:" -ForegroundColor Cyan
kubectl get deployments -n blink
Write-Host ""

Write-Host "🔷 Pods:" -ForegroundColor Cyan
kubectl get pods -n blink
Write-Host ""

Write-Host "🌐 Services:" -ForegroundColor Cyan
kubectl get services -n blink
Write-Host ""

# Minikube IP
$minikubeIP = minikube ip
Write-Host "============================================================" -ForegroundColor Yellow
Write-Host "                    ACCESS URLS                             " -ForegroundColor Yellow
Write-Host "============================================================" -ForegroundColor Yellow
Write-Host ""
Write-Host "  API Gateway:        http://${minikubeIP}:30080" -ForegroundColor White
Write-Host "  Discovery Server:  minikube service discovery-server -n blink" -ForegroundColor White
Write-Host ""
Write-Host "  Test API:" -ForegroundColor Cyan
Write-Host "  curl http://${minikubeIP}:30080/api/products" -ForegroundColor White
Write-Host ""

Write-Host "============================================================" -ForegroundColor Green
Write-Host "         DEPLOYMENT COMPLETE! Blink is on K8s!             " -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green
Write-Host ""