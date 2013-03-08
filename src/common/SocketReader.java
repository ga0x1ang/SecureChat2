package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Xiang Gao
 * Date: 2/15/13
 * Time: 7:57 PM
 */
public class SocketReader {

    private BufferedReader bufferedReader;

    public SocketReader(Socket sck) throws IOException {

        Socket socket = sck;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String read() throws IOException {

        return this.bufferedReader.readLine();
    }
}
