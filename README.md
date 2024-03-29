# Real-Time Electronic Voting System

An electronic version of the Canadian electoral system. This project is implemented as a distributed client-server application in Java.

Running instructions
--------------------

From command line:

> `cd RealTimeVotingSystem` 

> `java controller.CentralServer` 

> `java controller.DistrictServer Ottawa Ontario 60002`

> `java controller.ClientController 1 60002`

You can start as many district servers and clients as you want with different ports.

From eclipse:

1. open eclipse and select: file > import
2. navigate to RealTimeVotingSystem and click finish
3. open CentralServer in controller package and run it
4. open DistrictServer in controller package.
5. edit run configurations > arguments > add the values: `Ottawa Ontario 60002`
6. open ClientController, and add the command line arguments: `0 60002 test_files test_results`
7. edit the ClientController command line arguments to `1 60002` and re run to launch the GUI
8. Re-iterate steps 4 - 7 with different ports to run multiple District Servers and Clients

See documentation/RealTimeVotingSystem.pdf for documentation and UML diagrams.
