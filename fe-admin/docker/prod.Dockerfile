# syntax = docker/dockerfile:experimental

## BUILD image ##
FROM node:26-alpine AS builder
WORKDIR /build

# Install Corepack and activate the pnpm version used by the workspace.
RUN npm install -g corepack && corepack enable pnpm && corepack prepare pnpm@11.1.0 --activate

# Copy sources before install so source changes re-run install
COPY ./package.json ./pnpm-workspace.yaml ./pnpm-lock.yaml ./
COPY ./fe-admin ./fe-admin/
COPY ./fe-shared ./fe-shared

RUN pnpm install --frozen-lockfile --ignore-scripts
RUN pnpm dlx update-browserslist-db@latest && pnpm --filter fe-admin build


## RUN Image ##
FROM httpd:alpine

# Apache conf
COPY ./fe-admin/docker/httpd.conf /usr/local/apache2/conf/httpd.conf

COPY --from=builder /build/fe-admin/dist/ /usr/local/apache2/htdocs/
COPY ./fe-admin/docker/.htaccess /usr/local/apache2/htdocs/
