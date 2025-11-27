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


# Install Docker CLI
RUN apt-get update && apt-get install -y apt-transport-https ca-certificates curl gnupg \
    && install -m 0755 -d /etc/apt/keyrings \
    && curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg \
    && chmod a+r /etc/apt/keyrings/docker.gpg \
    && echo \
      "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/debian \
      $(. /etc/os-release && echo \"$VERSION_CODENAME\") stable" | \
      tee /etc/apt/sources.list.d/docker.list > /dev/null \
    && apt-get update \
    && apt-get install -y docker-ce-cli

# Add vscode user to the docker group
RUN if getent group docker > /dev/null; then \
      usermod -aG docker vscode; \
    else \
      groupadd docker && usermod -aG docker vscode; \
    fi

USER vscode
WORKDIR /workspaces/flashdeal


