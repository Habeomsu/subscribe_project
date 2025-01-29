# React 앱을 빌드하고 Nginx를 사용하지 않고 npm start로 실행하기 위한 Dockerfile
FROM node:16 as build

WORKDIR /app

# 의존성 설치
COPY package*.json ./
RUN npm install

# React 앱 복사
COPY . .

# 앱 빌드
RUN npm run build

# 최종 이미지로 React 앱 실행
FROM node:16

WORKDIR /app

# 앱 빌드 폴더 복사
COPY --from=build /app /app

# 앱 실행
CMD ["npm", "start"]

EXPOSE 3000
