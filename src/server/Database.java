package server;

import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: Xiang Gao
 * Date: 2/15/13
 * Time: 9:55 PM
 */
// TODO: not flexible enough, can only operate SecureChat2.user
public class Database {

    private String address;
    private int port;
    private String username;
    private char[] password;
    private String dbname;
    private Connection connection;

    public Database(String addr, int prt, String usrnm, String passwd, String dbname) {
        this.address = addr;
        this.port = prt;
        this.username = usrnm;
        this.password = passwd.toCharArray();
        this.dbname = dbname;
    }

    public int insert(String[] values) throws SQLException {

        PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO SecureChat2.user VALUES(NULL, ?, ?, ?)"); //SecureChat2.user in hard coded?
        for (int i = 0; i < values.length; i++) { // fill the parameters into the sql statement(username, passhash, salt)
            preparedStatement.setString(i + 1, values[i]);
        }
        int ret = preparedStatement.executeUpdate();

        return ret;
    }

    public ResultSet get(String value) throws SQLException {

        PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM SecureChat2.user WHERE username = ?");
        preparedStatement.setString(1, value);
        ResultSet ret = preparedStatement.executeQuery();

        return ret;
    }

    public void connect() throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        this.connection = DriverManager.getConnection("jdbc:mysql://" + this.address + ":" + this.port + "/" + this.dbname, this.username, String.valueOf(this.password)); // will the password in string leak information?
        System.out.println("Connected to the database");
    }

    public void disconnect() throws SQLException {

        this.connection.close();
        System.out.println("Disconnected from database");
    }
}
