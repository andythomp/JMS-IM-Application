To run the program, you should not need to import anything. You also should not need to start an RMI registry.

Run the chat server with the following parameters in 3 seperate instances.
1:
-s Server0 -c serverinit.cfg

2:
-s Server1 -c serverinit.cfg

3:
-s Server2 -c serverinit.cfg

This will create a ring network of 3 servers, and they will wait until the other servers have loaded up.
Once the network has been established, feel free to create multiple clients with the parameters:

-c clientinit.cfg -u [User Name]

or

-c clientinit.cfg
(The second parameters instead generate a random user name, but if it makes a user with the same name as someone else [should be a 1 in 10,000 chance] I am not responsible)

Once you have multiple clients running, feel free to kill a server. The program should lose no functionality.

IF the messages bother you, you can also go to the configuration folder and open the loggingmanager.cfg file,
and disable various message types by setting their values to false.




For sending files, I have provided two different music files. They should be able to be sent to 2 different clients, simultaneously. That being said, you can send the same file 2 times, but it will overwrite it in the file directory. Since it wasn't a requirement, I didn't feel it would be an issue.