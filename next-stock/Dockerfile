# Stage 1: Build
FROM node:22-alpine AS builder
ARG ACTION_PASSWORD="password"
ENV NEXT_TELEMETRY_DISABLED=1
WORKDIR /app

# Install dependencies
COPY package.json package-lock.json ./
RUN npm install

# Copy source files
COPY . .
RUN sed -i s/PASSW0RD/${ACTION_PASSWORD}/g src/app/actions/getFlagAction.ts
RUN cat  src/app/actions/getFlagAction.ts
# Build the project
RUN npm run build

# Stage 2: Serve
FROM node:22-alpine AS runner
ENV NEXT_TELEMETRY_DISABLED=1
ENV THE_FLAG=DummyFromDocker

WORKDIR /app

# Copy built files from the builder stage
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/package.json ./package.json

# Install only production dependencies
RUN npm install --production

# Expose the port and start the server
EXPOSE 3000
CMD ["npm", "run", "start"]
