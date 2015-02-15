import java.io.IOException;

public class Test
{
    public static void main ( String args[] ) 
    throws IOException, ClassNotFoundException {
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
