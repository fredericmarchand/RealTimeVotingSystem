//
// @author Brandon Schurman
//
package networking;
import java.io.Serializable;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.net.InetAddress;

public class Message implements Serializable 
{
    // HTTP request Method
    public static enum Method {
        GET, POST, PUT, DELETE, CONNECT
    }

    public static enum Type {
    	REGISTER, LOGIN, HAS_VOTED, VOTE, CANDIDATES, RESULTS
    }
    
    private static final long serialVersionUID 
        = -4507489610617393544L;

    private Object  data; // could be a String, or a Vote object 
    private Method  method;
    private long    checksum;
    private int     length;
    private int     senderPort;
//    private Type  	type;
    private String type;
    private InetAddress senderAddr; 


    public Message (Method method, String type, Object data) 
    throws IOException {
        this.senderPort = -1;
        this.method = method;
        this.type = type;
        this.data = data;
        this.length = -1;
        this.checksum = calculateChecksum(data);
        calculateLength();
    }

    /**
     * Deserialize a Message instance
     */
    public Message ( byte[]  bytes ) 
    throws IOException, MessageCorruptException {
        try { 
            ByteArrayInputStream bis 
                = new ByteArrayInputStream(bytes);
            ObjectInputStream ois 
                = new ObjectInputStream(bis);
            Message msg = (Message)ois.readObject();
            bis.close();
            ois.close();
            this.senderPort = msg.senderPort;
            this.senderAddr = msg.senderAddr;
            this.length = msg.length;
            this.method = msg.method;
            this.type = msg.type;
            this.data = msg.data;
            this.checksum = msg.checksum;
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        }
    }

    /**
     * calculate a CRC checksum for Object data
     */ 
    public static long calculateChecksum ( Object data ) 
        throws IOException {
        CRC32 crc = new CRC32();
        crc.update(getBytes(data));
        return crc.getValue();
    }

    /**
     * convert an Object to a byte array
     * @param msg
     * @return
     * @throws IOException
     */
    public static byte[] getBytes ( Object obj ) 
    throws IOException {
        ByteArrayOutputStream bos 
            = new ByteArrayOutputStream();
        ObjectOutputStream oos 
            = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.close();
        bos.close();
        return bos.toByteArray();
    }

    private void calculateLength() {
        try { 
            byte[] bytes = this.getBytes();
            this.length = bytes.length;
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * @return a byte array representation of this object
     */
    public byte[] getBytes() throws IOException {
        return getBytes(this);
    }

    @Override
    public String toString() {
        return "["
            + senderPort+", "
            + method+", "
            + type+", "
            + data+", "
            + checksum+"]";
    }

    public void setSender( int port, InetAddress host ) {
        this.senderPort = port;
        this.senderAddr = host;
        calculateLength();
    }

    public void setSenderPort ( int senderPort ) { 
        this.senderPort = senderPort;
        calculateLength();
    }
    
    public void setSenderHost ( InetAddress host ) { 
        this.senderAddr = host;
        calculateLength();
    }

    public void setData ( Object data ) {
        this.data = data;
        calculateLength();
    }
    
    public int getSenderPort() { 
        return this.senderPort;
    }
    
    public InetAddress getSenderHost() { 
        return this.senderAddr; 
    }

    public Object getData() {
        return this.data;
    }

    public long getChecksum() {
        return this.checksum;
    }   
    
    public Method getMethod() {
        return this.method;
    }

    public String getType() { 
        return this.type;
    }
}
