# syntax = docker/dockerfile:1

## BUILD image ##
FROM node:20-alpine AS builder
WORKDIR /build

# Switch to yarn 2
# RUN yarn set version berry

# Install dependencies
COPY ./package.json ./yarn.lock ./
RUN yarn install

# Copy and build app
COPY ./src ./src
COPY ./public ./public
COPY ./tsconfig.json ./
COPY ./tsconfig.node.json ./
COPY ./vite.config.ts ./
COPY ./postcss.config.cjs ./
COPY ./index.html ./
COPY ./.eslintrc.cjs ./
COPY ./.eslintignore ./
COPY ./.prettierrc.cjs ./
COPY ./.prettierignore ./
RUN yarn build


## RUN Image ##
FROM httpd:alpine


# Apache conf
COPY ./docker/httpd.conf /usr/local/apache2/conf/httpd.conf

COPY --from=builder /build/dist/ /usr/local/apache2/htdocs/
COPY ./docker/.htaccess /usr/local/apache2/htdocs/
