###
#  this docker file creates a the complete image for deployment of the Splash id website and test framework
###

FROM java

RUN DEBIAN_FRONTEND=noninteractive apt-get -yq upgrade

#update apt-get
RUN apt-get update -y

RUN apt-get install -y lynx mc vim
RUN apt-get -y install unzip curl
RUN apt-get install awstats -y libnet-ip-perl libgeo-ipfree-perl logrotate
RUN apt-get install -y nginx

####
# do the actual installation
####
RUN \
    \
    echo "===> installing utilities" && \
    apt-get install -y \
      unzip \
      mc \
      openssh-server \
      vim \
      links2 \
      ant

ADD target/splash.jar /opt/splash.jar

RUN rm /etc/nginx/sites-enabled/default

#configure nginx logrotate
ADD nginx/logrotate /etc/logrotate.d/nginx
RUN chmod 644 /etc/logrotate.d/nginx

ADD nginx/nginx.conf /etc/nginx/nginx.conf
ADD nginx/client.conf /etc/nginx/sites-enabled/client.conf
ADD nginx/awtstats.conf /etc/awstats/awstats.client.conf


ADD src/run.sh /opt/run.sh

EXPOSE 80

CMD ["/bin/bash","/opt/run.sh"]
