FROM node:16.13.1 AS builder
ARG DEPLOY

WORKDIR /app

# copy both 'package.json' and 'package-lock.json' (if available)
COPY package*.json ./

# install project dependencies
RUN npm install

# copy project files and folders to the current working directory (i.e. 'app' folder)
COPY . .

# build app for production with minification
RUN npm run build:${DEPLOY}

# production stage
FROM nginx:1.23

ENV TZ=Asia/Shanghai

COPY --from=builder /app/dist/ /usr/share/nginx/html/

COPY nginx/default.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
