#!/usr/bin/env bash

# includes lsb functions (optional)
if [ -f /lib/lsb/init-functions ]; then
  . /lib/lsb/init-functions
else
  log_daemon_msg() { echo "$@"; }
fi

log_daemon_msg "Starting cron (optional)"
if command -v cron >/dev/null 2>&1; then
  cron || true
fi

log_daemon_msg "Validating nginx config"
nginx -t

log_daemon_msg "Starting splash app (background)"
cd /opt

JAVA_FLAGS="-Xmx512m"
java ${JAVA_OPTS:-} ${JAVA_FLAGS} -jar splash.jar 2>&1 | tee /opt/java.log &

log_daemon_msg "Starting nginx (foreground)"
exec nginx -g 'daemon off;'
