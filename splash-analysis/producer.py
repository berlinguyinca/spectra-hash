#!/usr/bin/env python

import pika


N = 602723


SERVER = 'gose.fiehnlab.ucdavis.edu'


if __name__ == '__main__':
    credentials = pika.PlainCredentials('sajjan', 'fiehnlab2015')
    parameters = pika.ConnectionParameters(host = SERVER, credentials = credentials)

    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()

    channel.queue_declare(queue = 'splash_analysis')

    for i in range(N - 1):
        channel.basic_publish(exchange='', routing_key = 'splash_analysis', body = str(i))