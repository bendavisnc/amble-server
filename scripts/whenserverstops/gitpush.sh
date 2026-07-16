git add .
git -c user.name="$(whoami)" -c user.email="$(whoami)@$(hostname)" commit --message="Backup update"
git \
  -c http.extraHeader="Authorization: Basic $(printf '%s:%s' "$DBBACKUP_USERNAME" "$DBBACKUP_PRIVATE_KEY" | base64 -w0)" \
  push
