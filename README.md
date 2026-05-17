# Yuhaigui AI Game

Yuhaigui AI Game is a full-stack web game project that combines a Java/Spring Boot backend with a modern Vue 3 + Vite frontend. The backend provides APIs for user accounts, chat rooms, and AI-driven game interactions, while the frontend delivers the interactive UI for gameplay and chat experiences.

This repository contains:
- `yuhaigui-backend/`: Spring Boot service with persistence, API endpoints, and AI/game logic
- `yuhaigui-frontend/`: Vue 3 application with routing, API integration, and game views


## Getting Started
Run the following docker compose file to deploy the project

Please provide your api key and jwt secret in the environment variables section of the backend service before running the docker compose command.
```yaml
services:
  db:
    image: mariadb:11
    container_name: yuhaigui-db
    environment:
      MARIADB_ROOT_PASSWORD: rootpass
      MARIADB_DATABASE: yuhaigui
      MARIADB_USER: yuhaigui
      MARIADB_PASSWORD: yuhaigui_pass
    ports:
      - "3306:3306"
    volumes:
      - db_data:/var/lib/mysql

  backend:
    build:
      context: ./yuhaigui-backend
    container_name: yuhaigui-backend
    environment:
      DATABASE_HOST: db
      DATABASE_PORT: 3306
      DATABASE_NAME: yuhaigui
      DATABASE_USERNAME: yuhaigui
      DATABASE_PASSWORD: yuhaigui_pass
      JWT_SECRET: "replace_me"
      AI_API_KEY: "replace_me"
      AI_BASE_URL: "https://api.deepseek.com"
      AI_MODEL: "deepseek-v4-flash"
    ports:
      - "8080:8080"
    depends_on:
      - db

volumes:
  db_data:
```

Run
```bash
docker compose up -d
```
If you are running an older version of docker compose, you should use
```bash
docker-compose up -d
```


