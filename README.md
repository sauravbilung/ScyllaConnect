# Scylla DataBase Connectivity
A project explaining connectivity to ScyllaDB and querying to it.

<b> Requirements: </b>
1) A Java IDE
2) JDK 1.8
3) Maven (Any latest Version)
4) A running Scylla instance

For development purpose I have run the scylla instance in a docker container. But please note that it is not suitable in a production environment.
For best performance, a regular OS install is recommended.

<b> Steps for running Scylla in docker : </b>

1) Setting up a 3-node docker Scylla Cluster 
     ```
       docker run --name Node_X -d scylladb/scylla:3.0.10
       docker run --name Node_Y -d scylladb/scylla:3.0.10 --seeds="$(docker inspect --format='{{ .NetworkSettings.IPAddress }}' Node_X)" 
       docker run --name Node_Z -d scylladb/scylla:3.0.10 --seeds="$(docker inspect --format='{{ .NetworkSettings.IPAddress }}' Node_X)"        
     ```
2) Wait for some minutes and check the node status : ``` docker exec -it Node_Z nodetool status  ```
     
     Once the nodes are up and running we will get the output like this 
     ```
      sauravbilung@sauravbilung-HP-Pavilion-15-Notebook-PC:~$ docker exec -it Node_Z nodetool status  
      Datacenter: datacenter1
      =======================
      Status=Up/Down
      |/ State=Normal/Leaving/Joining/Moving
      --  Address     Load       Tokens       Owns    Host ID                               Rack
      UN  172.17.0.3  ?          256          ?       283e7904-8620-4d3e-a397-1c0a7325c5fc  rack1
      UN  172.17.0.2  ?          256          ?       63956b06-e7c6-4e64-bfaa-a7b8bba5b617  rack1
      UN  172.17.0.4  371.16 KB  256          ?       0b27c17d-658d-4244-97e0-7f979a6b70fc  rack1

      Note: Non-system keyspaces don't have the same replication settings, effective ownership information is meaningless

     ```
3) To verify the changes in Scylla cluster after running the project.
    ```
     docker exec -it Node_Z cqlsh
     use catalog;
     select * from catalog.superheroes;
    ```
   <b> Running the project : </b>
   1) Download and import the project in the IDE of choice.
   2) Change the contact points if they are different in your case in ```com.app.service.ScyllaService``` . Contact points are the list of addresses of nodes in the Scylla cluster through which a client driver communicates.
   3) ```com.app.driver.App``` is the main driver class. It has various functions which performs some tasks. Uncomment the functions which you need to run and comment which you don't.
   4) Run the project.
   
   Note : I have created a 3-node Scylla cluster because I have set the replication startegy of 3.
