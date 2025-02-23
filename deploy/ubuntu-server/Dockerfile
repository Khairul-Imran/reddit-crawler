FROM ubuntu:latest

# Add build argument for password
ARG DEPLOY_PASSWORD
ENV DEPLOY_PASSWORD=${DEPLOY_PASSWORD}

# Install necessary packages and Docker
RUN apt-get update && apt-get install -y \
    sudo \
    curl \
    gnupg \
    lsb-release \
    && curl -fsSL https://get.docker.com -o get-docker.sh \
    && sh get-docker.sh \
    && rm get-docker.sh \
    && apt-get clean

# Install OpenSSH Server
RUN apt-get install -y openssh-server \
    && mkdir /var/run/sshd

# Create a non-root user 'deploy'
RUN useradd -m -s /bin/bash deploy \
    && echo "deploy:${DEPLOY_PASSWORD}" | chpasswd \
    && usermod -aG sudo deploy \
    && groupadd -f docker \
    && usermod -aG docker deploy

# Set up SSH for the non-root user
RUN mkdir -p /home/deploy/.ssh \
    && chown deploy:deploy /home/deploy/.ssh \
    && chmod 700 /home/deploy/.ssh

# Create a new SSH configuration file with secure settings
RUN echo 'PermitRootLogin no' > /etc/ssh/sshd_config.d/custom.conf \
    && echo 'PasswordAuthentication no' >> /etc/ssh/sshd_config.d/custom.conf \
    && echo 'PubkeyAuthentication yes' >> /etc/ssh/sshd_config.d/custom.conf

# Use the SSH key
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]

# Start SSH service
CMD ["/usr/sbin/sshd", "-D"]
