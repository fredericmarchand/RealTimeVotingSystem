import java.io.Serializable;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public class Message implements Serializable 
{
    // HTTP request Method
    public static enum Method {
        GET, POST, PUT, DELETE, CONNECT
    }

    private static final long serialVersionUID 
        = -4507489610617393544L;

    private Object  data; // could be a String, or a Vote object 
    private Method  method;
    private long    checksum;
    private int     senderID;
    private String  type;


    public Message ( Method method, String type, Object data ) 
    throws IOException {
        this.senderID = senderID;
        this.method = method;
        this.type = type;
        this.data = data;
        this.senderID = -1; // for now TODO decide if necessary
        this.checksum = calculateChecksum(data);
    }

    /**
     * Deserialize a Message instance
     */
    public Message ( byte[]  bytes )
    throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Message msg = (Message)ois.readObject();
        bis.close();
        ois.close();
        this.senderID = msg.senderID;
        this.method = msg.method;
        this.type = msg.type;
        this.data = msg.data;
        this.senderID = msg.senderID;
        this.checksum = msg.checksum;
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

    /**
     * @return a byte array representation of this object
     */
    public byte[] getBytes() throws IOException {
        return getBytes(this);
    }

    @Override
    public String toString() {
        return "["
            + senderID+", "
            + method+", "
            + type+", "
            + data+", "
            + checksum+"]";
    }

    public void setData ( Object data ) {
        this.data = data;
    }

    public Object getData() {
        return this.data;
    }

    public long getChecksum() {
        return this.checksum;
    }   

    public int getSenderID() {
        return this.senderID;
    }

    public Method getMethod() {
        return this.method;
    }

    public String getType() { 
        return this.type;
    }
}
