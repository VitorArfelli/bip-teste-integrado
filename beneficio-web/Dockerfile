FROM node:20-alpine AS build
WORKDIR /app
COPY beneficio-web/package*.json ./
RUN npm ci
COPY beneficio-web .
RUN npm run build

FROM nginx:1.25-alpine
COPY beneficio-web/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/beneficio-web/browser /usr/share/nginx/html
EXPOSE 80
