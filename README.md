vagrant up --provision
vagrant halt

vagrant up

vagrant ssh

sudo docker run -d --name zookeeper -p 2181:2181 confluent/zookeeper

sudo -u postgres psql

postgres> create extension bottledwater;
postgres> \password
password
password

cd /vagrant/kafka-bottledwater-connect-s3
./gradlew fatJar

cd /opt/confluent-2.0.1/bin

open 6 tabs.

sudo docker start zookeeper

./kafka-server-start.sh /vagrant/config/server1.properties //9092
./kafka-server-start.sh /vagrant/config/server2.properties //9093

./schema-registry-start.sh ../etc/config/schema-registry.properties

cd /tmp/bottledwater-pg/kafka
./bottledwater --postgres=postgres://postgres:password@localhost --broker=localhost:9093

CREATE TABLE address (                                                                                                                                               
  address_id   SERIAL,
  street_address text,
  district     text,
  city         text,
  postal_code  text,
  phone        text,
  PRIMARY KEY  (address_id)
);


insert into address (street_address, district, city, postal_code, phone) values ('1 main street', 'ma', 'lexington', '211002', '781-989-9999');
insert into address (street_address, district, city, postal_code, phone) values ('2 main street', 'ma', 'lexington', '211002', '781-989-9999');
insert into address (street_address, district, city, postal_code, phone) values ('3 main street', 'ma', 'lexington', '211002', '781-989-9999');
insert into address (street_address, district, city, postal_code, phone) values ('4 main street', 'ma', 'lexington', '211002', '781-989-9999');
insert into address (street_address, district, city, postal_code, phone) values ('5 main street', 'ma', 'lexington', '211002', '781-989-9999');
insert into address (street_address, district, city, postal_code, phone) values ('6 main street', 'ma', 'lexington', '211002', '781-989-9999');
insert into address (street_address, district, city, postal_code, phone) values ('7 main street', 'ma', 'lexington', '211002', '781-989-9999');
insert into address (street_address, district, city, postal_code, phone) values ('8 main street', 'ma', 'lexington', '211002', '781-989-9999');
insert into address (street_address, district, city, postal_code, phone) values ('9 main street', 'ma', 'lexington', '211002', '781-989-9999');


export CLASSPATH=/vagrant/kafka-bottledwater-connect-s3/build/libs/<SOMENAME>-all.jar
./connect-distributed.sh /vagrant/config/worker1.properties

curl -X POST -H "Content-Type: application/json" --data '{"name": "bottledwater-s3sink", "config": {"connector.class":"com.cdc.connect.S3SinkConnector", "tasks.max":"1", "topics":"address", "s3_bucket_name":"dataintegration"  }}' http://localhost:7999/connectors

curl -X DELETE http://localhost:7999/connectors/bottledwater-s3sink



