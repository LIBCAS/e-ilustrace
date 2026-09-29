# syntax = docker/dockerfile:experimental

## BUILD image ##
FROM node:26-alpine AS builder
WORKDIR /build

# Install Corepack and activate the pnpm version used by the workspace.
RUN npm install -g corepack && corepack enable pnpm && corepack prepare pnpm@11.1.0 --activate

# Copy sources before install so source changes re-run install
COPY ./package.json ./pnpm-workspace.yaml ./pnpm-lock.yaml ./
COPY ./fe ./fe/
COPY ./fe-shared ./fe-shared

RUN pnpm install --frozen-lockfile --ignore-scripts
RUN pnpm dlx update-browserslist-db@latest && pnpm --filter fe build


## RUN Image ##
FROM httpd:alpine

# Apache conf
COPY ./fe/docker/httpd.conf /usr/local/apache2/conf/httpd.conf

COPY --from=builder /build/fe/dist/ /usr/local/apache2/htdocs/
COPY ./fe/docker/.htaccess /usr/local/apache2/htdocs/
