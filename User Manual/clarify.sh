# These commands make the prompt and the output of "ls" plain,
# so that "script" files look as neat as possible.
alias ls=/bin/ls
unset PROMPT_COMMAND
if [ -w /etc/passwd ]
then
    PS1='# '
else
    PS1='$ '
fi