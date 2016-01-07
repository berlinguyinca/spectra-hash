###
#  this docker file creates a the complete image for deployment of the Splash id website and test framework
###

FROM ubuntu:trusty

RUN DEBIAN_FRONTEND=noninteractive apt-get -yq upgrade

#update apt-get
RUN apt-get update -y

RUN apt-get install -y lynx mc vim
RUN apt-get -y install unzip curl
RUN apt-get install awstats -y libnet-ip-perl libgeo-ipfree-perl
RUN apt-get install -y nginx

####
# do the actual installation
####
RUN \
    echo "deb http://archive.ubuntu.com/ubuntu trusty main universe" > /etc/apt/sources.list && \
    echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" > /etc/apt/sources.list.d/webupd8team-java.list && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys C2518248EEA14886 && \
    \
    \
    echo "===> add webupd8 repository..."  && \
    echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list  && \
    echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list  && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886  && \
    apt-get update  && \
    \
    \
    echo "===> install Java"  && \
    echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections  && \
    echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections  && \
    DEBIAN_FRONTEND=noninteractive  apt-get install -y --force-yes oracle-java7-installer oracle-java7-set-default  && \
    \
    \
    echo "===> installing utilities" && \
    apt-get install -y \
      unzip \
      mc \
      openssh-server \
      vim \
      links2 \
      ant  && \
    echo "===> clean up..."  && \
    rm -rf /var/cache/oracle-jdk7-installer


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
