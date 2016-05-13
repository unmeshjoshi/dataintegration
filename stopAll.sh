ps ax | grep -i 'schema-registry' | grep java | grep -v grep |grep -v zookeeper | awk '{print $1}' | xargs kill -9

ps ax | grep -i 'Kafka' | grep java | grep -v grep |grep -v zookeeper | awk '{print $1}' | xargs kill -9

sudo docker stop zookeeper
