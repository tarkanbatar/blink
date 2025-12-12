# ============================================
# BLINK E-Commerce Kubernetes Deployment
# PowerShell Script for Windows
# ============================================

function Write-ColorOutput($ForegroundColor) {
    $fc = $host.UI.RawUI.ForegroundColor
    $host.UI.RawUI.ForegroundColor = $ForegroundColor
    if ($args) {
        Write-Output $args
    }
    $host.UI.RawUI.ForegroundColor = $fc
}

function Write-Green($message) {
    Write-Host $message -ForegroundColor Green
}

function Write-Yellow($message) {
    Write-Host $message -ForegroundColor Yellow
}

function Write-Blue($message) {
    Write-Host $message -ForegroundColor Blue
}

function Write-Red($message) {
    Write-Host $message -ForegroundColor Red
}

Write-Host ""
Write-Blue "============================================================"
Write-Blue "          BLINK E-Commerce Kubernetes Deployment           "
Write-Blue "============================================================"
Write-Host ""

if (!(Get-Command kubectl -ErrorAction SilentlyContinue)) {
    Write-Red "kubectl bulunamadi! Lutfen kubectl kurun."
    exit 1
}

Write-Green "[1/10] Creating namespace..."
kubectl apply -f k8s/namespace.yaml
Write-Host ""

Write-Green "[2/10] Creating secrets..."
kubectl apply -f k8s/infrastructure/mongodb/secret.yaml
kubectl apply -f k8s/infrastructure/redis/secret.yaml
kubectl apply -f k8s/services/user-service/secret.yaml
Write-Host ""

Write-Green "[3/10] Creating persistent volume claims..."
kubectl apply -f k8s/infrastructure/mongodb/pvc.yaml
kubectl apply -f k8s/infrastructure/redis/pvc.yaml
Write-Host ""

Write-Green "[4/10] Deploying infrastructure..."

Write-Host "  -> MongoDB..."
kubectl apply -f k8s/infrastructure/mongodb/deployment.yaml
kubectl apply -f k8s/infrastructure/mongodb/service.yaml

Write-Host "  -> Redis..."
kubectl apply -f k8s/infrastructure/redis/deployment.yaml
kubectl apply -f k8s/infrastructure/redis/service.yaml

Write-Host "  -> Zookeeper..."
kubectl apply -f k8s/infrastructure/kafka/zookeeper-deployment.yaml
kubectl apply -f k8s/infrastructure/kafka/zookeeper-service.yaml
Write-Host ""

Write-Yellow "[5/10] Waiting for Zookeeper to be ready..."
kubectl wait --for=condition=ready pod -l app=zookeeper -n blink --timeout=120s 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "  -> Zookeeper starting..."
}

Write-Host "  -> Kafka..."
kubectl apply -f k8s/infrastructure/kafka/kafka-deployment.yaml
kubectl apply -f k8s/infrastructure/kafka/kafka-service.yaml
Write-Host ""

Write-Yellow "[6/10] Waiting for infrastructure to be ready..."

Write-Host "  -> Waiting for MongoDB..."
kubectl wait --for=condition=ready pod -l app=mongodb -n blink --timeout=180s 2>$null
if ($LASTEXITCODE -ne 0) { Write-Host "  -> MongoDB starting..." }

Write-Host "  -> Waiting for Redis..."
kubectl wait --for=condition=ready pod -l app=redis -n blink --timeout=120s 2>$null
if ($LASTEXITCODE -ne 0) { Write-Host "  -> Redis starting..." }

Write-Host "  -> Waiting for Kafka..."
kubectl wait --for=condition=ready pod -l app=kafka -n blink --timeout=180s 2>$null
if ($LASTEXITCODE -ne 0) { Write-Host "  -> Kafka starting..." }
Write-Host ""

Write-Green "[7/10] Deploying Discovery Server..."
kubectl apply -f k8s/services/discovery-server/deployment.yaml
kubectl apply -f k8s/services/discovery-server/service.yaml

Write-Host "  -> Waiting for Discovery Server to be ready..."
kubectl wait --for=condition=ready pod -l app=discovery-server -n blink --timeout=180s 2>$null
if ($LASTEXITCODE -ne 0) { Write-Host "  -> Discovery Server starting..." }
Write-Host ""

Write-Green "[8/10] Deploying application services..."

Write-Host "  -> User Service..."
kubectl apply -f k8s/services/user-service/configmap.yaml
kubectl apply -f k8s/services/user-service/deployment.yaml
kubectl apply -f k8s/services/user-service/service.yaml

Write-Host "  -> Product Service..."
kubectl apply -f k8s/services/product-service/configmap.yaml
kubectl apply -f k8s/services/product-service/deployment.yaml
kubectl apply -f k8s/services/product-service/service.yaml

Write-Host "  -> Cart Service..."
kubectl apply -f k8s/services/cart-service/configmap.yaml
kubectl apply -f k8s/services/cart-service/deployment.yaml
kubectl apply -f k8s/services/cart-service/service.yaml

Write-Host "  -> Order Service..."
kubectl apply -f k8s/services/order-service/configmap.yaml
kubectl apply -f k8s/services/order-service/deployment.yaml
kubectl apply -f k8s/services/order-service/service.yaml

Write-Host "  -> Notification Service..."
kubectl apply -f k8s/services/notification-service/configmap.yaml
kubectl apply -f k8s/services/notification-service/deployment.yaml
kubectl apply -f k8s/services/notification-service/service.yaml
Write-Host ""

Write-Green "[9/10] Deploying API Gateway..."
kubectl apply -f k8s/services/api-gateway/configmap.yaml
kubectl apply -f k8s/services/api-gateway/deployment.yaml
kubectl apply -f k8s/services/api-gateway/service.yaml
Write-Host ""

Write-Green "[10/10] Deploying Ingress..."
kubectl apply -f k8s/ingress/ingress.yaml
Write-Host ""

Write-Yellow "Waiting for all pods to be ready (this may take a few minutes)..."
Start-Sleep -Seconds 10

Write-Host ""
Write-Blue "============================================================"
Write-Blue "                    CLUSTER STATUS                          "
Write-Blue "============================================================"
Write-Host ""

Write-Green "Pods:"
kubectl get pods -n blink
Write-Host ""

Write-Green "Services:"
kubectl get services -n blink
Write-Host ""

Write-Green "Ingress:"
kubectl get ingress -n blink
Write-Host ""

Write-Blue "============================================================"
Write-Blue "                  ACCESS INFORMATION                        "
Write-Blue "============================================================"
Write-Host ""

$minikubeExists = Get-Command minikube -ErrorAction SilentlyContinue
if ($minikubeExists) {
    $minikubeIP = minikube ip 2>$null
    if ($minikubeIP) {
        Write-Green "Minikube IP: $minikubeIP"
        Write-Host ""
        Write-Yellow "Add to C:\Windows\System32\drivers\etc\hosts:"
        Write-Host "  $minikubeIP api.blink.local"
        Write-Host ""
        Write-Yellow "Then access:"
        Write-Host "  http://api.blink.local/api/products"
    }
}

Write-Host ""
Write-Green "============================================================"
Write-Green "    DEPLOYMENT COMPLETE! Blink is running on K8s!          "
Write-Green "============================================================"
Write-Host ""
