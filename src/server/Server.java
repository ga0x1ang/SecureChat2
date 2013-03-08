package server;

import common.Command;
import common.SCUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Xiang Gao
 * Date: 2/15/13
 * Time: 11:36 PM
 */
public class Server {

    private ConcurrentHashMap<String, ConnectionThread> onlineUsers;
    private ConcurrentHashMap<Integer, Room> roomList;
    private Database database;

    public Server(int port, String keystorePath, char[] keystorePassword, Database db) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        ServerSocket serverSocket = createSSLSocket(port, keystorePath, keystorePassword);
        this.roomList = new ConcurrentHashMap<Integer, Room>();
        this.onlineUsers = new ConcurrentHashMap<String, ConnectionThread>();
        this.database = db;

        while (true) {
            Socket socket = serverSocket.accept();
            new ConnectionThread(this, socket);
        }
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        int port = Integer.valueOf(args[0]);
        String keystorePath = args[1];//"keystore.jks";
        char[] keystorePassword = args[2].toCharArray();//"securechat".toCharArray();
        String dbHost = args[3];
        int dbPort = Integer.valueOf(args[4]);
        String dbUser = args[5];
        //String dbPass = args[6]; // password should not be stored in a string
        String dbTbl = args[7];

        Database db = new Database(dbHost, dbPort, dbUser, args[6], dbTbl); // these information should be retrived from argus[]

        db.connect();
        new Server(port, keystorePath, keystorePassword, db);
    }

    public Database getDB() {

        return this.database;
    }

    public void getUserList(ConnectionThread caller) throws IOException {

        String instruction = "getUserListReturn";
        String[] params = onlineUsers.keySet().toArray(new String[0]);
        Command command = new Command(instruction, params);
        caller.sendCommand(command);
    }

    public void addUser(ConnectionThread ct) throws IOException, InterruptedException {

        ConnectionThread loggedinUser = this.onlineUsers.get(ct.getUsername());
        if (loggedinUser != null) {
            loggedinUser.systemAlert("Your account has been logged in somewhere else!");
            loggedinUser.logout(null);
        }

        this.onlineUsers.put(ct.getUsername(), ct);
    }

    public void addRoom(Room room) {
        System.out.println(room.hashCode());
        this.roomList.put(room.hashCode(), room);
    }

    public final Room getRoom(int rmId) {

        return this.roomList.get(rmId);
    }

    public final ConnectionThread getUser(String username) {

        return this.onlineUsers.get(username);
    }

    private ServerSocket createSSLSocket(int port, String ksPath, char[] ksPasswd) throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, UnrecoverableKeyException, KeyManagementException {

        KeyStore ks;
        KeyManagerFactory kmf;
        SSLContext sslContext;
        SecureRandom secureRandom;

        ks = KeyStore.getInstance("JKS"); // should use jceks or pcks12
        FileInputStream fin = new FileInputStream(ksPath);
        kmf = KeyManagerFactory.getInstance("SunX509"); // can only find PKIX in document
        sslContext = SSLContext.getInstance("TLS");
        secureRandom = new SecureRandom();

        ks.load(fin, ksPasswd);
        kmf.init(ks, ksPasswd);
        secureRandom.nextInt();

        sslContext.init(kmf.getKeyManagers(), null, secureRandom);
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

        Security.addProvider(new BouncyCastleProvider());
        sslServerSocket.setEnabledCipherSuites(SCUtils.getCipherSuites());

        fin.close();
        System.out.println("Server begin to listen on port " + port);

        return sslServerSocket;
    }

    public final void removeUser(ConnectionThread ct) {
        if (this.onlineUsers.get(ct.getUsername()) != null)  // if this connection is logged in
            this.onlineUsers.remove(ct.getUsername());
    }
}
