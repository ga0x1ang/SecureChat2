package client;

import com.google.gson.Gson;
import common.Command;
import common.SCUtils;
import common.SocketIO;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Xiang Gao
 * Date: 2/15/13
 * Time: 10:08 PM
 */
public final class Client {

    private SocketIO socketIO;
    private GUI_Login gui_login;
    private GUI_Register gui_register;
    private GUI_Main gui_main;
    private ConcurrentHashMap<Integer, GUI_Chat> chatRoomList;
    private String username;

    private Client(String addr, int port, String ksPath, char[] ksPasswd) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        Socket socket = createSSLSocket(addr, port, ksPath, ksPasswd);
        this.socketIO = new SocketIO(this, socket);
        this.socketIO.start();
        this.chatRoomList = new ConcurrentHashMap<Integer, GUI_Chat>();

        createLoginWindow();
    }

    public static void main(String[] args) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        String server = args[0];
        int port = Integer.valueOf(args[1]);
        String keystorePath = args[2]; //"ts_6907.jks";
        char[] keystorePassword = args[3].toCharArray(); //"6907.85".toCharArray();

        new Client(server, port, keystorePath, keystorePassword);
    }

    /**
     * @param username
     * @param passwd
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public void login(String username, char[] passwd) throws IOException {

        byte[] salt = ByteUtils.fromHexString("SecureChat2");
        byte[] password = ByteUtils.fromHexString(String.valueOf(passwd));
        byte[] passhash = SCUtils.hash(password, salt);

        String[] values = {username, ByteUtils.toHexString(passhash)};
        Command command = new Command("login", values);
        this.call(command);
    }

    /**
     * params : boolean value in String
     *
     * @param params
     */
    public void loginReturn(String[] params) throws IOException {

        if (Boolean.valueOf(params[0])) {
            this.gui_login.dispose();
            createMainWindow(params[1]);
            this.username = params[1];
        } else {
            this.popupMessageBox(new String[]{"Login failed!", String.valueOf(false)});
        }
    }

    /**
     * @param usrnm
     * @param passwd
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public void register(String usrnm, char[] passwd) throws IOException {

        byte[] salt = ByteUtils.fromHexString("SecureChat2");
        byte[] password = ByteUtils.fromHexString(String.valueOf(passwd));
        byte[] passhash = SCUtils.hash(password, salt);

        String[] values = {usrnm, ByteUtils.toHexString(passhash)};
        Command command = new Command("register", values);
        this.call(command);
    }

    /**
     * params is empty (unused)
     *
     * @param params
     */
    public void registerReturn(String[] params) {

        if (Integer.valueOf(params[0]) > 0) {
            this.gui_register.dispose();
        } else {
            this.popupMessageBox(new String[]{"Register failed!", String.valueOf(false)});
        }
    }

    public void getUserList() throws IOException {

        final String[] params = {};

        Command command = new Command("getUserList", params);
        this.call(command);
    }

    public void getUserListReturn(String[] params) {
        this.gui_main.updateList(params);
    }

    public void createRoom() throws IOException {

        final String[] params = {};
        Command command = new Command("createRoom", params);
        this.call(command);
    }

    public void createRoomReturn(final String[] params) {

        int roomId = Integer.valueOf(params[0]);
        createChatWindow(roomId);
    }

    public void sendMessage(int rmId, String msg) throws IOException {

        final String[] params = {String.valueOf(rmId), msg};

        Command command = new Command("sendMessage", params);
        this.call(command);
    }

    public void sendMessageReturn(String[] params) {

        int roomId = Integer.valueOf(params[0]);
        String message = params[1];
        GUI_Chat targetChatWindow = this.chatRoomList.get(roomId);
        //if (targetChatWindow != null) { // THIS IS DUMB, I have to
        targetChatWindow.setVisible(true);
        targetChatWindow.updateHistory(message);
        //}
    }

    /**
     * used to pack the request info json and write it to the socket
     *
     * @param r
     * @throws IOException
     */
    final void call(Command r) throws IOException {

        Gson gson = new Gson();
        String request = gson.toJson(r);
        this.socketIO.write(request);
    }

    public void inviteUser(int roomId, String usrnm) throws IOException {

        String instruction = "inviteUser";
        String[] params = {String.valueOf(roomId), usrnm};
        Command command = new Command(instruction, params);
        this.call(command);
    }

    final void createLoginWindow() {

        final GUI_Login gui_login = new GUI_Login(this);
        this.gui_login = gui_login;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui_login.pack();
                gui_login.setVisible(true);
            }
        });
    }

    public final void createRegisterWindow() {

        final GUI_Register gui_register = new GUI_Register(this);
        this.gui_register = gui_register;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui_register.pack();
                gui_register.setVisible(true);
            }
        });
    }

    final void createMainWindow(String username) throws IOException {

        final GUI_Main gui_main = new GUI_Main(this, username);
        this.gui_main = gui_main;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui_main.pack();
                gui_main.setVisible(true);
            }
        });

    }

    final void createChatWindow(int roomId) {

        final GUI_Chat gui_chat = new GUI_Chat(this, roomId);
        this.chatRoomList.put(roomId, gui_chat);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui_chat.pack();
                gui_chat.setVisible(true);
            }
        });
    }

    private Socket createSSLSocket(String serverAddr, int port, String ksPath, char[] ksPasswd) throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, KeyManagementException {

        KeyStore ks;
        TrustManagerFactory tmf;
        SSLContext sslContext;
        SecureRandom secureRandom;

        ks = KeyStore.getInstance("JKS"); // should us jceks or pcks12
        FileInputStream fin = new FileInputStream(ksPath);
        tmf = TrustManagerFactory.getInstance("SunX509"); // can only find PKIX
        sslContext = SSLContext.getInstance("TLS");
        secureRandom = new SecureRandom();

        ks.load(fin, ksPasswd);
        tmf.init(ks);
        secureRandom.nextInt();

        sslContext.init(null, tmf.getTrustManagers(), secureRandom);
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket();

        Security.addProvider(new BouncyCastleProvider());
        sslSocket.setEnabledCipherSuites(SCUtils.getCipherSuites());

        fin.close();

        return sslSocketFactory.createSocket(serverAddr, port);
    }

    public void exitRoom(int roomId) throws IOException {

        String[] params = {String.valueOf(roomId)};
        Command command = new Command("exitRoom", params);
        this.call(command);
        GUI_Chat room = this.chatRoomList.get(roomId);
        if (room == null)
            System.out.println("disappear????");
        this.chatRoomList.remove(roomId);
        room.dispose();
    }

    public void updateRoomUserList(String[] params) {

        int roomId = Integer.valueOf(params[0]);
        String[] userlist = ArrayUtils.subarray(params, 1, params.length);
        GUI_Chat chatroom = this.chatRoomList.get(roomId);
        chatroom.updateUserList(userlist);
    }

    public void showAllChatWindows() {
        Iterator iterator = this.chatRoomList.values().iterator();
        while (iterator.hasNext()) {
            GUI_Chat room = (GUI_Chat) iterator.next();
            room.setVisible(true);
        }
    }

    public void logout() throws IOException {
        Command command = new Command("logout", new String[]{});
        this.call(command);
        System.exit(0);
        // send logout command
        // close all connection
        // dispose all windows
        // this.logout();
    }

    // remove user from chat room
    public void remove(int roomId, String username) throws IOException {
        if (this.username.equals(username)) {
            popupMessageBox(new String[]{"you cannot remove yourself!", String.valueOf(false)});
        } else {
            Command command = new Command("removeRoomUser", new String[]{String.valueOf(roomId), username});
            this.call(command);
        }
    }

    public void popupMessageBox(String[] message) {
        boolean isDeathWindow = Boolean.valueOf(message[1]);
        String text = message[0];
        final GUI_MessageBox messageBox = new GUI_MessageBox(text, isDeathWindow);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                messageBox.pack();
                messageBox.setVisible(true);
            }
        });
    }
}