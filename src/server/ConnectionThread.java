package server;

import com.google.gson.Gson;
import common.Command;
import common.SCUtils;
import common.SocketIO;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: Xiang Gao
 * Date: 2/17/13
 * Time: 2:56 PM
 */
public final class ConnectionThread extends Thread {

    private Server server;
    private SocketIO socketIO;
    private String username;

    public ConnectionThread(Server srv, Socket sck) throws IOException {

        this.server = srv;
        this.socketIO = new SocketIO(this, sck);
        this.socketIO.start();
        this.username = "NO USERNAME";
    }

    /**
     * TODO: maybe should seperate func and funcReturn?
     *
     * @param values
     * @throws java.security.NoSuchAlgorithmException
     *
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public final void register(String[] values) throws IOException, SQLException {

        // retrival variables
        String username = values[0];
        byte[] passhash = ByteUtils.fromHexString(values[1]);

        // salt the original passhash
        byte[] salt = SCUtils.genSalt();
        String saltedPasshash = ByteUtils.toHexString(SCUtils.hash(passhash, salt));

        // try to insert to the database
        /* TODO: should add some detailed return information of the insert query */
        String[] insertParams = {username, saltedPasshash, ByteUtils.toHexString(salt)};
        System.out.println(insertParams[1]);
        Database db = this.server.getDB();
        int ret;

        ret = db.get(username).next() ? 0 : db.insert(insertParams); // check if the username has been taken

        // set the command values
        String instruction = "registerReturn";
        String[] params = {String.valueOf(ret)};

        // return the result to the client
        Command command = new Command(instruction, params);
        this.sendCommand(command);
    }

    public final void login(String[] values) throws SQLException, IOException, InterruptedException {

        boolean ret = false;
        String username = values[0];
        byte[] passhash = ByteUtils.fromHexString(values[1]);

        // select by username
        Database db = this.server.getDB();
        ResultSet rs = db.get(username);

        // get salt
        if (rs.next()) {

            byte[] dbPasshash = ByteUtils.fromHexString(rs.getString(3));
            byte[] salt = ByteUtils.fromHexString(rs.getString(4));
            byte[] clientPasshash = SCUtils.hash(passhash, salt);
            System.out.println("dbpasshash:" + ByteUtils.toHexString(dbPasshash));
            System.out.println("clientpasshash" + ByteUtils.toHexString(clientPasshash));

            // compare with database pass
            if (ret = ByteUtils.equals(clientPasshash, dbPasshash)) {
                this.username = rs.getString(2);
                this.server.addUser(this);
            }
        }

        // return the result to the client
        String[] params = {String.valueOf(ret), this.username};
        Command command = new Command("loginReturn", params);
        this.sendCommand(command);
    }

    /**
     * sendMessageReturn message is sent in Room.broadcast()
     *
     * @param params
     * @throws java.io.IOException
     */
    public final void sendMessage(String[] params) throws IOException {

        int roomId = Integer.valueOf(params[0]);
        String message = this.getUsername() + ": " + params[1] + "\n";
        Room room = this.server.getRoom(roomId);
        room.broadtcast(message);
    }

    public final String getUsername() {

        return this.username;
    }

    public final void createRoom(String[] unused) throws IOException {

        Room room = new Room(this);
        this.server.addRoom(room);
        String instruction = "createRoomReturn";
        String[] params = {String.valueOf(room.hashCode())};
        Command command = new Command(instruction, params);
        this.sendCommand(command);

        room.updateRoomUserList();
    }

    public final void getUserList(String[] params) throws IOException {

        this.server.getUserList(this);
    }

    public final void sendCommand(Command r) throws IOException {

        Gson gson = new Gson();
        String request = gson.toJson(r);

        this.socketIO.write(request);
    }

    public final void inviteUser(String[] params) throws IOException {

        int roomId = Integer.valueOf(params[0]);
        String username = String.valueOf(params[1]);
        ConnectionThread user;
        if ((user = this.server.getUser(username)) != null) {

            Room room = this.server.getRoom(roomId);
            if (room.checkOwnerPermission(this.getId())) {
                room.addUser(user);

                // open a new window on the invited user's client
                String instruction = "createRoomReturn";
                String[] p = {String.valueOf(room.hashCode())};
                Command command = new Command(instruction, p);
                user.sendCommand(command);

                room.updateRoomUserList();
            } else {
                System.out.println("you have no permission to invite user in this room");
            }
        } else {
            System.out.println("no user found");
        }

    }

    public final void exitRoom(String[] params) throws IOException {

        int roomId = Integer.valueOf(params[0]);
        Room room = server.getRoom(roomId);
        room.removeUser(this.getId());

        room.updateRoomUserList();
    }

    public final void logout(String[] unused) {
        // issue: room created by this user will not be destroyed
        this.server.removeUser(this);

    }

    public final void systemAlert(String alertMessage) throws IOException {
        Command command = new Command("popupMessageBox", new String[]{alertMessage, String.valueOf(true)});
        this.sendCommand(command);
    }

    public final void removeRoomUser(String[] params) {
        int roomId = Integer.valueOf(params[0]);
        String username = params[1];

        Room room = this.server.getRoom(roomId);
        Long userId = this.server.getUser(username).getId();
        room.removeUser(userId);
    }

}
