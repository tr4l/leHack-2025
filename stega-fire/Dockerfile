# Use a Node.js image as the builder stage
FROM node:alpine AS builder

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application files
COPY src ./src

# Build the project
RUN npm run build

# Hide the flag
COPY hideFlag.js ./
RUN mv dist/js/fire.min.js.map dist/fire.min.js.map
RUN node hideFlag.js "Ok folk were will be the flag now LH{th1s_is_a_Fl4G}" dist/fire.min.js.map > dist/js/fire.min.js.map

# Use the official Nginx image as the base image
FROM nginx:alpine

# Copy the built files from the builder stage to the Nginx HTML directory
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy the custom nginx configuration file to the container
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80 to allow external access
EXPOSE 80

# Start Nginx when the container launches
CMD ["nginx", "-g", "daemon off;"]