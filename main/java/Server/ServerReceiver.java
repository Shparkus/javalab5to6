package Server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class ServerReceiver {
    private final DatagramChannel channel;
    private final Selector selector;

    public ServerReceiver(DatagramChannel channel, Selector selector) {
        this.channel = channel;
        this.selector = selector;
    }

    public ReceivedDatagram receive(int timeoutMs) throws IOException {
        if (selector.select(timeoutMs) == 0) {
            return null;
        }
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();
            if (key.isReadable()) {
                ByteBuffer buffer = ByteBuffer.allocate(65507);
                SocketAddress address = channel.receive(buffer);
                if (address == null) {
                    return null;
                }
                buffer.flip();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                return new ReceivedDatagram(bytes, address);
            }
        }
        return null;
    }
}
