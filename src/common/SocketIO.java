package common;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Xiang Gao
 * Date: 2/18/13
 * Time: 8:13 PM
 */
public class SocketIO extends Thread {

    private Socket socket;
    private SocketReader socketReader;
    private SocketWriter socketWriter;
    private Object caller;
    private Dispatcher dispatcher;

    public SocketIO(Object caller, Socket sck) throws IOException {
        this.caller = caller;
        this.socket = sck;
        this.socketReader = new SocketReader(socket);
        this.socketWriter = new SocketWriter(socket);
        this.dispatcher = new Dispatcher();
    }

    public void write(String message) throws IOException {
        this.socketWriter.write(message);
    }

    public void run() {
        String message;
        try {
            while ((message = socketReader.read()) != null) {
                System.out.println(message);
                this.dispatcher.dispatch(caller, message);
            }
            System.out.println("socket reader error! close the socket");
            socket.close();
            //this.socket.close(); // when read error, close the socket
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchMethodException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
