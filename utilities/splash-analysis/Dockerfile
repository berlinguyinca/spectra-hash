###
#  this docker file creates a the complete image for deployment of the Splash analysis program
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
    rm -rf /var/cache/oracle-jdk7-installer && \
    mkdir /data


ADD target/splash-analysis-0.0.1-SNAPSHOT.jar /opt/analyze.jar
#ADD src/data/input.csv /data/input.csv
ADD complete-201510-precomputed.csv.gz /data/input.csv.gz
ADD src/run.sh /opt/run.sh


CMD ["/bin/bash","/opt/run.sh"]