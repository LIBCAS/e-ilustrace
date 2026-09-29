# syntax = docker/dockerfile:experimental

## BUILD image ##
FROM node:20-alpine AS builder
WORKDIR /build

# Switch to yarn 4
RUN yarn set version berry

# RUN echo "" > .yarnrc.yml

# Install dependencies
COPY ./fe/package.json ./fe/yarn.lock ./fe/
RUN cd ./fe && yarn install && cd ..

# Copy and build app
COPY ./fe/src ./fe/src
COPY ./fe/public ./fe/public
COPY ./fe/tsconfig.json ./fe/
COPY ./fe/tsconfig.node.json ./fe/
COPY ./fe/vite.config.ts ./fe/
COPY ./fe/tailwind.config.cjs ./fe/
COPY ./fe/postcss.config.cjs ./fe/
COPY ./fe/index.html ./fe/
COPY ./fe/.eslintrc.cjs ./fe/
COPY ./fe/.eslintignore ./fe/
COPY ./fe/.prettierrc.cjs ./fe/
COPY ./fe/.prettierignore ./fe/
COPY ./fe-shared ./fe-shared

RUN cd ./fe && npx update-browserslist-db@latest && yarn build && cd ..


## RUN Image ##
FROM httpd:alpine

# Apache conf
COPY ./fe/docker/httpd.conf /usr/local/apache2/conf/httpd.conf

COPY --from=builder /build/fe/dist/ /usr/local/apache2/htdocs/
COPY ./fe/docker/.htaccess /usr/local/apache2/htdocs/

