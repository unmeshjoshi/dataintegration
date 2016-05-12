STEP=10 #need to wait or else get exception that the broker already registerd with zookeeper
sudo docker start zookeeper

sleep $STEP

/opt/confluent-2.0.1/bin/kafka-server-start /vagrant/config/server1.properties 2>&1 > server1.log &


/opt/confluent-2.0.1/bin/kafka-server-start /vagrant/config/server2.properties 2>&1 > server2.log &


/opt/confluent-2.0.1/bin/kafka-server-start /vagrant/config/server3.properties 2>&1 > server3.log &

sudo docker logs -f zookeeper > zookeeper.log 2>&1 > zookeeper.log &

tail -f server1.log server2.log server3.log zookeeper.log

