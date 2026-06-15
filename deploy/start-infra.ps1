# ============================================================
# 阶段一快速启动脚本（Windows PowerShell）
# 用法：在 deploy 目录下执行 .\start-infra.ps1
# ============================================================

Write-Host ">>> 启动 MySQL + Redis + Nacos ..." -ForegroundColor Cyan
docker compose up -d

Write-Host ""
Write-Host ">>> 基础设施启动中，请等待约 1-2 分钟" -ForegroundColor Yellow
Write-Host "    Nacos:  http://localhost:8848/nacos"
Write-Host "    MySQL:  root / root123456  (库: myit_world)"
Write-Host "    Redis:  localhost:6379"
Write-Host ""
Write-Host ">>> 下一步：" -ForegroundColor Green
Write-Host "    1. cd ..\backend && mvn clean install -DskipTests"
Write-Host "    2. 启动 auth-service:  cd auth-service && mvn spring-boot:run"
Write-Host "    3. 启动 gateway-service: cd gateway-service && mvn spring-boot:run"
Write-Host "    4. 启动前端: cd ..\frontend && npm install && npm run dev"
