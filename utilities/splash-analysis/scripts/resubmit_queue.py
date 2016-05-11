#!/usr/bin/env python

from __future__ import print_function
import pika, uuid


SERVER = 'gose.fiehnlab.ucdavis.edu'

FOUT = open(str(uuid.uuid1()) +'.csv', 'wb')

def callback(ch, method, properties, body):
    FOUT.write(body)
    FOUT.write(b'\n')
    FOUT.flush()


if __name__ == '__main__':
	credentials = pika.PlainCredentials('sajjan', 'fiehnlab2015')
	parameters = pika.ConnectionParameters(host = SERVER, credentials = credentials)

	connection = pika.BlockingConnection(parameters)
	channel = connection.channel()

	channel.queue_declare(queue = 'splash_aggregation')
	channel.basic_consume(callback, queue = 'splash_analysis', no_ack = True)
	channel.start_consuming()