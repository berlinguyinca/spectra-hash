###
#  this docker file creates a the complete image for deployment of the Splash id website and test framework
###

FROM ubuntu:trusty

RUN DEBIAN_FRONTEND=noninteractive apt-get -yq upgrade

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
ADD src/run.sh /opt/run.sh

EXPOSE 10000

CMD ["/bin/bash","/opt/run.sh"]