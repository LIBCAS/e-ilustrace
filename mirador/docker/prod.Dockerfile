# syntax = docker/dockerfile:1

## BUILD image ##
FROM node:26-alpine AS builder
WORKDIR /build

# Install pnpm pinned by packageManager.
RUN npm install -g corepack \
    && corepack enable pnpm \
    && corepack prepare pnpm@11.1.0 --activate

# Install dependencies
COPY ./package.json ./pnpm-lock.yaml ./pnpm-workspace.yaml ./
RUN pnpm install --frozen-lockfile --ignore-scripts

# Copy and build app
COPY ./src ./src
COPY ./public ./public
COPY ./tsconfig.json ./
COPY ./tsconfig.node.json ./
COPY ./vite.config.ts ./
COPY ./postcss.config.cjs ./
COPY ./index.html ./
COPY ./eslint.config.js ./
COPY ./.prettierrc.cjs ./
COPY ./.prettierignore ./
RUN pnpm build


## RUN Image ##
FROM httpd:alpine


# Apache conf
COPY ./docker/httpd.conf /usr/local/apache2/conf/httpd.conf

COPY --from=builder /build/dist/ /usr/local/apache2/htdocs/
COPY ./docker/.htaccess /usr/local/apache2/htdocs/
