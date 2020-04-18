#!/bin/sh

echo "Create required directories"
mkdir ~/.config/autostart
mkdir ~/.config/environment.d

echo "==> Disable Gnome-Keyring ssh component"
cp /etc/xdg/autostart/gnome-keyring-ssh.desktop ~/.config/autostart
echo "Hidden=true" >> ~/.config/autostart/gnome-keyring-ssh.desktop

echo "==> Point ssh agent socket environment variable to GnuPG"
cat > ~/.config/environment.d/99-gpg-agent_ssh.conf <<'EOF'
SSH_AUTH_SOCK=${XDG_RUNTIME_DIR}/gnupg/S.gpg-agent.ssh
EOF

echo "==> Done"
echo
echo "Logout and after login GnuPG will be your ssh-agent"
echo
