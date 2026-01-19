package Server;

import Network.Request;
import Network.SerializationUtils;

import java.io.IOException;

public class ServerRequestReader {
    public Request read(byte[] data) throws IOException, ClassNotFoundException {
        Object obj = SerializationUtils.deserialize(data);
        if (obj instanceof Request) {
            return (Request) obj;
        }
        return null;
    }
}
