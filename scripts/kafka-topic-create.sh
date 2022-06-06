#!/bin/bash
/bin/kafka-topics --bootstrap-server localhost:9092 --create --topic kaspar --partitions 2
/bin/kafka-topics --bootstrap-server localhost:9092 --describe --topic kaspar