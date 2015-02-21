package controller;

import networking.*;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.LinkedList;
import java.io.IOException;

/**
 * work in progress, open to suggestions,
 * just an idea of how we could implement the district server
 * using the producer-consumer model.
 *
 * A producer thread listens for incoming client requests and 
 * stores them to an eventQueue buffer.
 * 
 * A consumer thread takes from the buffer and accumulates 
 * some messages in another fixed-size buffer, then flushes and 
 * sends that whole buffer to the central server for processing. 
 */
public class DistrictServer 
{
    // #messages to accumulate before dispatching to central server
    public static final int QUEUE_SIZE = 10; 

    private BlockingQueue<Message> eventQueue;

    public void start() 
    throws InterruptedException {
        
        eventQueue = new ArrayBlockingQueue<Message>(QUEUE_SIZE);
        
        Thread t1 = new Thread(new Runnable() {
            @Override public void run() {
                try {
                    producer();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        
        Thread t2 = new Thread(new Runnable() {
            @Override public void run() {
                try {
                    consumer();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        
        t1.start();
        t2.start();
        
        t1.join();
        t2.join();

    }
    
    private void producer() throws InterruptedException {
        
        final WSocket client_conn = new WSocket(8080);
        client_conn.listen();

        while(true) {
            try { 
                Message req = client_conn.receive();
                eventQueue.put(req);
                
                new Thread(new Runnable() {
                    @Override public void run() {
                        try {
                            Message res = new Message(
                                    Message.Method.POST,
                                    "confirmation",
                                    "we received your request");
                            client_conn.sendTo(
                                    res, 
                                    req.getSenderPort());
                        } catch ( IOException e ) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch ( IOException e ) {
                e.printStackTrace();
            } catch ( MessageCorruptException e ) {
                e.printStackTrace();
            }
        }
    }
    
    private void consumer() throws InterruptedException {

        LinkedList<Message> dispatch_queue 
                = new LinkedList<Message>();
        
        final WSocket server_conn = new WSocket(8888);
        server_conn.connect();

        while(true) {

            Message msg = eventQueue.take();
            dispatch_queue.push(msg);

            System.out.println("consumer thread take: "+msg);

            if ( dispatch_queue.size() >= QUEUE_SIZE ) {
                System.out.println("dispatching accumulated objects");
              
                try  {
                    // send list of messages 
                    // requires support of unbound message sizes
                    // this will not work at the moment..
                    Message req = new Message(
                            Message.Method.POST,
                            "votes-list",
                            dispatch_queue);

                    Message res = server_conn.sendReceive(msg);

                    if ( res.getType().equals("stats") ) {
                        updateStats(res);
                    } 
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
                
                while ( dispatch_queue.size() > 0 ) {
                    System.out.println(dispatch_queue.pop());
                    // either send messages individually, 
                    // or more efficiently, 
                    // send a list of votes to central server
                }
            }
        }
    }
    
    public void updateStats ( Message res ) {
        // maybe update some statistics of votes
        System.out.println(res);
    }

    public static void main ( String[] args ) {
        while ( true ) {
            try { 
                new DistrictServer().start();
            } catch ( InterruptedException e ) {
                e.printStackTrace();
                System.out.println("oops. Restarting server");
            }
        }
    }
}
