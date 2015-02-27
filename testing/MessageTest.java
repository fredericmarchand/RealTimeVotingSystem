package testing;
import java.io.IOException;

import networking.Message;
import networking.MessageCorruptException;

//
// illustrates general usage of Message class
//

public class MessageTest
{
    public static void main ( String args[] ) 
    throws IOException, ClassNotFoundException, MessageCorruptException {
        Message req = new Message(
                Message.Method.POST,
                "client-vote",
                "Justin Trudeau");

        byte[] send_this_over_UDP = req.getBytes();

        byte[] receive_over_UDP = send_this_over_UDP; 

        Message res = new Message(receive_over_UDP);

        System.out.println(res);
    }
}
