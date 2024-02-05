# syntax = docker/dockerfile:experimental

## BUILD image ##
FROM node:20-alpine AS builder
WORKDIR /build

# Switch to yarn 4
RUN yarn set version berry

# RUN echo "" > .yarnrc.yml

# Install dependencies
COPY ./fe-admin/package.json ./fe-admin/yarn.lock ./fe-admin/
RUN cd ./fe-admin && yarn install && cd ..

# Copy and build app
COPY ./fe-admin/src ./fe-admin/src
COPY ./fe-admin/public ./fe-admin/public
COPY ./fe-admin/tsconfig.json ./fe-admin/
COPY ./fe-admin/tsconfig.node.json ./fe-admin/
COPY ./fe-admin/vite.config.ts ./fe-admin/
COPY ./fe-admin/tailwind.config.cjs ./fe-admin/
COPY ./fe-admin/postcss.config.cjs ./fe-admin/
COPY ./fe-admin/index.html ./fe-admin/
COPY ./fe-admin/.eslintrc.cjs ./fe-admin/
COPY ./fe-admin/.eslintignore ./fe-admin/
COPY ./fe-admin/.prettierrc.cjs ./fe-admin/
COPY ./fe-admin/.prettierignore ./fe-admin/
COPY ./fe-shared ./fe-shared

RUN cd ./fe-admin && npx update-browserslist-db@latest && yarn build && cd ..


## RUN Image ##
FROM httpd:alpine

# Apache conf
COPY ./fe-admin/docker/httpd.conf /usr/local/apache2/conf/httpd.conf

COPY --from=builder /build/fe-admin/dist/ /usr/local/apache2/htdocs/
COPY ./fe-admin/docker/.htaccess /usr/local/apache2/htdocs/

