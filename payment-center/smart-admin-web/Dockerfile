FROM node:18-alpine AS builder
ARG DEPLOY

WORKDIR /app

# copy package.json only
COPY package.json ./

# install project dependencies
RUN rm -f package-lock.json && npm install --legacy-peer-deps --no-optional && \
    npm install @rollup/rollup-linux-arm64-musl --save-dev

# copy project files and folders to the current working directory (i.e. 'app' folder)
COPY . .

# build app for production with minification
ENV VITE_APP_API_URL=http://localhost:1681
RUN NODE_OPTIONS="--max-old-space-size=4096" npm run build:${DEPLOY}

# production stage
FROM nginx:alpine

ENV TZ=Asia/Shanghai

COPY --from=builder /app/dist/ /usr/share/nginx/html/

COPY nginx/default.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
