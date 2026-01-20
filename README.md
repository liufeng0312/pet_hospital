# Pet Hospital Management System (宠物医院管理系统)

一个基于 Spring Boot + Vue 3 的现代化宠物医院全流程管理系统。

## 功能模块

- **工作台**: 医生/护士日常操作入口。
- **客户管理**: 客户建档、宠物档案管理。
- **挂号排队**: 实时排队叫号看板。
- **医生接诊**: 病历书写、处方开具、检查检验。
- **住院护理**: 可视化床位管理、每日护理记录。
- **药品库存**: 药品入库、出库、效期预警。
- **财务结算**: 收银台、VIP 折扣、营收报表。
- **智能提醒**: 疫苗/生日自动提醒。

## 技术栈

- **后端**: Java 8, Spring Boot 2.7, MyBatis-Plus, MySQL
- **前端**: Vue 3, TypeScript, Vite, Element Plus, ECharts, Axios

## 快速运行

### 1. 数据库
导入 `mysql_design.md` 和 `test_data.sql` 中的 SQL 脚本到 MySQL 数据库 `pet_hospital`。

### 2. 后端
```bash
cd pet-hospital-backend
mvn spring-boot:run
```
服务端口: 8080

### 3. 前端
```bash
cd pet-hospital-frontend
npm install
npm run dev
```
访问地址: http://localhost:5173

## 部署
运行根目录下的 `deploy.sh` 脚本进行一键构建：
```bash
./deploy.sh
```

## 开发日志
详见 `day_by_day_dev.md`。
