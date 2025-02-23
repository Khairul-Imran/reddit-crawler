#!/bin/bash

# Add SSH public key
if [ ! -z "$SSH_PUBLIC_KEY" ]; then
    echo "$SSH_PUBLIC_KEY" > /home/deploy/.ssh/authorized_keys
    chmod 600 /home/deploy/.ssh/authorized_keys
    chown deploy:deploy /home/deploy/.ssh/authorized_keys
fi

# Fix docker socket permissions if it exists
if [ -e /var/run/docker.sock ]; then
    chmod 666 /var/run/docker.sock
fi

exec /usr/sbin/sshd -D
