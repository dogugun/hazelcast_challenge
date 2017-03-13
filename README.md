##Hazelcast SA Challenge

This repository contains project jgroupsCluster project which is developed for the Hazelcast Solution Architect Challenge task.
The purpose of the project is that the application runs on multiple JVMs and form a cluster so that any given node will print "We are started!" only once. 

This is achieved with the help of jgroups. As soon as the aplication starts, it will either form a cluster of single or multiple node cluster, depending on joining nodes.
The coordinator node prints the message, and broadcast "OK" signal to other nodes,  other nodes will not attempt to do the same.

Once the OK message broadcasted, all nodes will carry this message so nodes joining with a delay will also recognize it. Even the coordinator node dies after printing, newly joined nodes will be aware of that the task is completed. 

I would like to explain the little delay at the beginning if the cluster in single node: the coordinator node waits for other nodes for 3 seconds before attempting.

##Files
The executables are in the target folder. 

path of source codes:
        
        hazelcast_challenge/jgroupsCluster/src/main/java/com/challenge/node/AppNode.java
        
path of unit and integration test codes

        hazelcast_challenge/jgroupsCluster/src/main/java/com/challenge/test/

(Eclipse messed up source folder and package hierarchy on mvn eclipse:eclipse run, that's why they ended up with long paths)

##Run and Parameters

        -Djava.net.preferIPv4Stack=true

        -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

        java -jar -Djava.net.preferIPv4Stack=true -Dorg.slf4j.simpleLogger.defaultLogLevel=warn appNode.jar 
