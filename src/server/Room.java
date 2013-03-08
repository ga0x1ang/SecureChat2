package server;

import common.Command;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Xiang Gao
 * Date: 2/19/13
 * Time: 2:23 PM
 */
public class Room {

    private Long creatorId;
    private HashMap<Long, ConnectionThread> userList;
    private ArrayList<ConnectionThread> adminList;
    private ArrayList<ConnectionThread> readList;
    private ArrayList<ConnectionThread> writeList;

    /**
     * create a room, add the creator to the userlist
     *
     * @param creator
     */
    public Room(ConnectionThread creator) throws IOException {
        this.userList = new HashMap<Long, ConnectionThread>();
        this.creatorId = creator.getId();
        addUser(creator);
        //this.userList.put(this.creatorId, creator);

    }

    /**
     * forward the message from user to all the user in userlist
     *
     * @param message
     * @throws java.io.IOException
     */
    public void broadtcast(String message) throws IOException {

        ConnectionThread ct;
        Iterator iterator;

        iterator = userList.values().iterator();
        while (iterator.hasNext()) {
            ct = (ConnectionThread) iterator.next();
            String instruction = "sendMessageReturn";
            String[] params = {String.valueOf(this.hashCode()), message};
            Command request = new Command(instruction, params);
            ct.sendCommand(request);
        }

    }

    /**
     * added a participant
     *
     * @param participant
     */
    public void addUser(ConnectionThread participant) {

        // add a connection thread into this room
        this.userList.put(participant.getId(), participant);
    }

    /**
     * remove a user from userlist
     *
     * @param userId
     */
    public void removeUser(Long userId) {

        this.userList.remove(userId);
    }

    public boolean checkOwnerPermission(Long id) {

        return id.equals(creatorId);
    }

    public void updateRoomUserList() throws IOException {
        // prepare nedded variables for udate the list
        // this is just a scratch, need optimaized
        ConnectionThread ct; // temp variable
        Iterator iterator;
        ArrayList<String> nameList = new ArrayList<String>();

        iterator = userList.values().iterator();
        while (iterator.hasNext()) { // get all the username from current list and store them in nameList
            ct = (ConnectionThread) iterator.next();
            nameList.add(ct.getUsername());
        }
        String[] params1 = {String.valueOf(this.hashCode())}; // first parameter is the room id
        String[] params2 = new String[nameList.size()]; // then is the username list
        nameList.toArray(params2);
        String[] params = ArrayUtils.addAll(params1, params2); // combine the two parts

        String instruction = "updateRoomUserList";
        Command request = new Command(instruction, params);
        iterator = userList.values().iterator();
        while (iterator.hasNext()) {
            ct = (ConnectionThread) iterator.next();
            ct.sendCommand(request);
        }
    }

    /*

    public void setAdminPermission(ConnectionThread ct) {
        this.adminList.add(ct);
    }

    public void setReadPermission(ConnectionThread ct) {
        this.readList.add(ct);
    }

    public void setWritePermission(ConnectionThread ct) {
        this.writeList.add(ct);
    }

    public boolean checkAdminPermission(ConnectionThread ct) {

        return this.adminList.contains(ct);
    }

    public boolean checkReadPermission(ConnectionThread ct) {

        return this.readList.contains(ct);
    }

    public boolean checkWritePermission(ConnectionThread ct) {

        return this.writeList.contains(ct);
    }

    public String[] getUserPermission(Long id) {
        ConnectionThread ct = this.userList.get(id);
        return null;
    }
    */
}
