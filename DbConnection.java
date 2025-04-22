import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private String url = "jdbc:mysql://localhost:3306/teclms";
    private String user = "root";
    private String password = "1234";

    private Connection con = null;

    private void registerMyConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Error in registering the drive class" + e.getMessage());
        }
    }

    public Connection getMyConnection(){
        registerMyConnection();
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Error in getting connection" + e.getMessage());
        }
        return con;
    }
}
