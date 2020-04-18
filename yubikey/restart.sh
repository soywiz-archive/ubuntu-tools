# https://www.bootc.net/archives/2013/06/09/my-perfect-gnupg-ssh-agent-setup/
gpg2 --card-status 
sudo killall ssh-agent gpg-agent
unset GPG_AGENT_INFO SSH_AGENT_PID SSH_AUTH_SOCK
eval $(gpg-agent --daemon --enable-ssh-support)
ssh-add -l

