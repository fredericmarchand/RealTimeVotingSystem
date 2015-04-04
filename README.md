# Real-Time Electronic Voting System

An electronic version of the Canadian electoral system. This project is implemented as a distributed client-server application in Java.

Running instructions
--------------------

From command line:

> `cd RealTimeVotingSystem` 

> `java controller.DistrictServer Ottawa Ontario 60002`

> `java controller.ClientController 1 60002`


From eclipse:

1. open eclipse and select: file > import
2. navigate to RealTimeVotingSystem and click finish
3. open DistrictServer in controller package.
4. edit run configurations > arguments > add the values: `Ottawa Ontario 60002`
5. open ClientController, and add the command line arguments: `0 60002 test_files test_results`
6. edit the ClientController command line arguments to `1 60002` and re run to launch the GUI

See documentation/RealTimeVotingSystem.pdf for documentation and UML diagrams.
