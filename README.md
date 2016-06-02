This is vagrant setup which can help running bottledwater_pg and confluent platform. This setup downloads and installs all the dependencies required to compile bottledwater_pg as well as connfluent platform. It also has a sample connector to read data published by bottledwater and publish it on s3. The setup also creates a sample ecommerce schema with seed data to help experiment with kafka connect.

## Setup instructions
1. Install Vagrant https://www.vagrantup.com/downloads.html
2. Install Ansible http://docs.ansible.com/ansible/intro_installation.html. Make sure you have ansible 2.0
3. Checkout dataintegration repository.
   git clone 
4. cd dataintegration
5. Run command vagrant up.
   This will pull ubuntu/trusty64 basebox and install following set of packages
   - jdk1.8.0_65
   - postgresql-9.5, postgresql-server-dev-9.5, libpq-dev
   - libsnappy-dev
   - asciidoc
   - g++
   - libjansson-dev
   - libcurl4-openssl-dev
   - git
   - pkg-config
   - cmake
   - linux-image-extra-3.13.0-85-generic
   - apparmor
   - docker-engine
  This will also create database called 'ecommerce' in Postgres. This will then be used to setup ecommerce schema and seed data.

  All these packages are required to compile avro-c, librdkafka and bottledwater.
  * The setup will also make neccessary changes to pg_hba.conf and postgresql.conf to allow replication.
  * It will then 
      * download avro-c from http://redrockdigimark.com/apachemirror/avro/avro-1.8.0/c/avro-c-1.8.0.tar.gz and build it
      * git clone https://github.com/edenhill/librdkafka.git and build it
      * git clone https://github.com/confluentinc/bottledwater-pg.git in /vagrant folder
        Pulling it in /vagrant folder allows you make changes to source code if needed and build it again.
        After successful build and deploy. The setup will create bottledwater extension in database.
      * It will build and install bottledwater.
    * Downloads http://packages.confluent.io/archive/2.0/confluent-2.0.1-2.11.7.zip and unzips it in /opt/confluent-2.0.1
6. This completes the basic setup required to execute and test bottledwater.
7. After initial setup, run 'vagrant reload' to restart the vm. (This is needed as postgres is not picking the libraries in /usr/local/lib'
# Setting up ecommerce test schema and seed data.
8. This setup has Postgres version of http://www.mysqltutorial.org/mysql-sample-database.aspx. 
9. cd /vagrant/ecommerce
10. Run command './gradlew flywayMigrate -i --stacktrace'
This will create ecommerce schema and insert seed data to be used by connectors.

9. Open 4 tabs on terminal. These are needed to run following
    - kafka worker1, schema-registry, zookeeper
    - postgres
    - bottledwater
    - terminal to setnd http requests to connect broker
10. In each of the tab run 'vagrant ssh' to get onto the vagrant vm.

This will create a table to used to add data to be connsumed by bottledwater.
Note that this schema does not have timestamp fields. Timestamp are converted to union types which are not supported by kafka-connect's avro converter yet.

11. cd /vagrant
    run ./startAll.sh
   This will start zookeeper, kakfa server1, server2 and  server3, and schema-registry.

12. In the terminal opened for bottedwater. run following command    - cd /vagrant/bottledwater-pg/kafka
    - ./bottledwater --postgres=postgres://postgres:password@localhost --broker=localhost:9093
    Now bottledwater is all set to start publishing database changes to kafka
  
 13. You can start kafka consumer to see all the messaegs
    - /opt/confluent-2.0.1/bin/kafka-avro-console-consumer --topic address --from-beginning --zookeeper localhost:2181
 14. In the postgresql terminal, insert some data in address table.
      
      insert into address (street_address, district, city, postal_code, phone) values ('1 main street', 'ma', 'lexington', '211002', '781-989-9999');

  You should see records in console consumer window.  
      {"address_id":{"int":1},"street_address":{"string":"1 main street"},"district":{"string":"ma"},"city":{"string":"lexington"},"postal_code":{"string":"211002"},"phone":{"string":"781-989-9999"}}
  
 ## Test kafka connector
 
 1. The repo also includes a sample Sink connector to read avro data published by bottledwater. To try it
 2. cd /vagrant/kafka-bottledwater-connect-s3
 3. Run command './gradlew fatJar'
 4. after the jar is built, open one more terminal to run connect worker.
 5. vagrant ssh
 6. export CLASSPATH=/vagrant/kafka-bottledwater-connect-s3/build/libs/kafka-bottledwater-connect-s3-all.jar
 7.  /opt/confluent-2.0.1/bin/connect-distributed /vagrant/config/worker1.properties
 8.  Now, setup the connector with REST request 
 9.  curl -X POST -H "Content-Type: application/json" --data '{"name": "bottledwater-s3sink", "config":    {"connector.class":"com.cdc.s3.connect.S3SinkConnector", "tasks.max":"1", "topics":"address", "s3_bucket_name":"dataintegration"  }}' http://localhost:7999/connectors
 10.  This will setup the connector and after every 5 records will try to push records to s3. 
 11.  If you have s3 bucket, setup your s3 credentials in
 12.  /home/vagrant/.aws/credentials file.
