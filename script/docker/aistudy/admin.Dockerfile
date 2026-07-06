FROM node:24-alpine AS build

WORKDIR /workspace
COPY yudao-ui/yudao-ui-admin-vue3 ./yudao-ui/yudao-ui-admin-vue3
COPY script/docker/aistudy/admin/.env.prod ./yudao-ui/yudao-ui-admin-vue3/.env.prod

WORKDIR /workspace/yudao-ui/yudao-ui-admin-vue3
ENV COREPACK_NPM_REGISTRY=https://registry.npmmirror.com
RUN corepack enable \
  && corepack prepare pnpm@11.7.0 --activate \
  && pnpm config set registry https://registry.npmmirror.com \
  && pnpm config set fetch-timeout 600000 \
  && pnpm config set fetch-retries 5 \
  && pnpm config set fetch-retry-mintimeout 10000 \
  && pnpm config set fetch-retry-maxtimeout 120000 \
  && pnpm install --frozen-lockfile \
  && pnpm build:prod

FROM nginx:1.27-alpine

COPY script/docker/aistudy/admin/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/yudao-ui/yudao-ui-admin-vue3/dist-prod /usr/share/nginx/html

EXPOSE 80
