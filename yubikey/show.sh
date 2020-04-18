echo $SSH_AUTH_SOCK
gpgconf --list-dirs agent-ssh-socket
systemctl --user show-environment | grep SSH_AUTH_SOCK

