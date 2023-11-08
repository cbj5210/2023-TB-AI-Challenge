# Define global args
ARG RUNTIME_VERSION="16"

# Stage 1 - bundle base image + runtime
# Grab a Fresh Copy
FROM node:${RUNTIME_VERSION}-slim AS node-aws

# Stage 2 Install library and dependencies
FROM node-aws AS build-image
WORKDIR /app
COPY . .
# install node modules and build assets
RUN npm install --legacy-peer-deps
RUN npm run build

EXPOSE 3000

ENTRYPOINT ["npm","run","start"]

