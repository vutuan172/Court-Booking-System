#!/bin/bash
# Script kiểm tra và build Docker image cho Court Booking System
# Hỗ trợ cả Intel x86_64 và Apple Silicon ARM64

set -e

echo "🔍 Kiểm tra môi trường..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Kiểm tra Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker chưa được cài đặt"
    echo "   Tải Docker Desktop tại: https://www.docker.com/products/docker-desktop"
    exit 1
fi

# Kiểm tra Docker Compose
if ! docker compose version &> /dev/null; then
    echo "❌ Docker Compose V2 chưa được cài đặt"
    exit 1
fi

echo "✅ Docker version: $(docker --version)"
echo "✅ Docker Compose version: $(docker compose version)"

# Kiểm tra architecture
ARCH=$(uname -m)
echo "✅ Architecture: $ARCH"

if [ "$ARCH" = "arm64" ]; then
    echo "   🍎 Detected Apple Silicon (M1/M2)"
    PLATFORM="ARM64"
elif [ "$ARCH" = "x86_64" ]; then
    echo "   💻 Detected Intel x86_64"
    PLATFORM="AMD64"
else
    echo "   ⚠️  Unknown architecture: $ARCH"
    PLATFORM="UNKNOWN"
fi

echo ""
echo "🧹 Dọn dẹp containers và images cũ..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
docker compose down -v 2>/dev/null || true
docker system prune -f

echo ""
echo "🏗️  Building Docker images..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
docker compose build --no-cache --progress=plain

echo ""
echo "🚀 Khởi động services..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
docker compose up -d

echo ""
echo "⏳ Đợi MySQL khởi động (30 giây)..."
sleep 30

echo ""
echo "🔍 Kiểm tra trạng thái services..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
docker compose ps

echo ""
echo "📋 Logs từ application..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
docker compose logs --tail=50 app

echo ""
echo "🧪 Testing health endpoint..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
sleep 10

MAX_RETRIES=10
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if curl -f http://localhost:8080/actuator/health 2>/dev/null; then
        echo ""
        echo "✅ Health check PASSED!"
        break
    else
        RETRY_COUNT=$((RETRY_COUNT + 1))
        echo "⏳ Retry $RETRY_COUNT/$MAX_RETRIES..."
        sleep 5
    fi
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo "❌ Health check FAILED after $MAX_RETRIES attempts"
    echo ""
    echo "📋 Full logs:"
    docker compose logs app
    exit 1
fi

echo ""
echo "🎉 BUILD VÀ DEPLOYMENT THÀNH CÔNG!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📍 Application URL: http://localhost:8080"
echo "📍 Health Check: http://localhost:8080/actuator/health"
echo "📍 MySQL Port: 3306"
echo ""
echo "👤 Default Accounts:"
echo "   Admin: admin@example.com / strongpassword"
echo "   User:  user@example.com / password123"
echo ""
echo "📝 Useful commands:"
echo "   View logs:        docker compose logs -f app"
echo "   Stop services:    docker compose stop"
echo "   Restart:          docker compose restart"
echo "   Clean up:         docker compose down -v"
echo ""
echo "✅ Platform: $PLATFORM"
echo "✅ Ready for testing with Postman!"
