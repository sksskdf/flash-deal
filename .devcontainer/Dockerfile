FROM mcr.microsoft.com/devcontainers/java:1-17-bullseye

# Install utilities and GraphQL CLI for schema tooling
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       curl ca-certificates git jq unzip \
    && rm -rf /var/lib/apt/lists/*

# Install Node.js 20.x for frontend development
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs \
    && rm -rf /var/lib/apt/lists/*

# Install k6 for local load testing
RUN curl -fsSL https://dl.k6.io/key.gpg | gpg --dearmor -o /usr/share/keyrings/k6-archive-keyring.gpg \
    && echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | tee /etc/apt/sources.list.d/k6.list \
    && apt-get update \
    && apt-get install -y k6 \
    && rm -rf /var/lib/apt/lists/*

USER vscode
WORKDIR /workspaces/flashdeal


