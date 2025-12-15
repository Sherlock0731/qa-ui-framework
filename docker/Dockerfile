FROM maven:3.9.5-eclipse-temurin-17

# Install Chrome
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    unzip \
    && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Install Firefox
RUN apt-get update && apt-get install -y \
    firefox-esr \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Download dependencies
RUN mvn dependency:go-offline

# Default command
CMD ["mvn", "clean", "test"]
