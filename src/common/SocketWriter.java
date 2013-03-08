package common;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Xiang Gao
 * Date: 2/15/13
 * Time: 7:57 PM
 */
public class SocketWriter {
    private PrintWriter writer;

    public SocketWriter(Socket sck) throws IOException {
        Socket socket = sck;
        this.writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public void write(String content) {

        writer.println(content);
        writer.flush();  // need this?
    }
}

/**
 * Socket has an outputStream, get it by socket.getOutputStream()
 * PrintWriter write to an outputStream
 * PrintWriter.write -> OutputStream -> Socket
 */
