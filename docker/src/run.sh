#!/usr/bin/env bash


#includes lsb functions
. /lib/lsb/init-functions

# Start cron for needed regular logrotate
CRON=`which cron`
log_daemon_msg "Starting ${CRON}"
$CRON

log_daemon_msg "starting nginx"

service nginx start

log_daemon_msg "starting logrotate"
logrotate -f /etc/logrotate.conf

log_daemon_msg "starting splash app"
cd /opt
java -Xmx512m -jar splash.jar
