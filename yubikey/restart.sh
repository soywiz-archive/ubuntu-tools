sudo killall gpg-agent
sudo killall ssh-agent
# note: eval is used because the produced STDOUT is a bunch of ENV settings
eval $( gpg-agent --daemon --enable-ssh-support )

