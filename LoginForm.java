import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm {
    private JPanel Login;
    private JPanel Form;
    private JTextField userId;
    private JPasswordField pwd;
    private JButton signInBtn;
    private JButton cancelBtn;

    public User user;

    public LoginForm() {
        signInBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userId.getText();
                String password = new String(pwd.getPassword());

                user = getAuthenticatedUser(username, password);

                if(user != null) {
                    JOptionPane.showMessageDialog(null, "Login Successful! Welcome " + user.getFirstName());
                    /*String role = user.getRoleFromUserId();

                    switch (role){
                        case "AD":
                            new AdminDashboard(user);
                            break;

                        case "LEC":
                            new LecturerDashboard(user);
                            break;

                        case "TO":
                            new TechnicalOfficerDashboard(user);
                            break;

                        case "ST":
                            new StudentDashboard(user);
                            break;

                        default:
                            JOptionPane.showMessageDialog(null, "Unknown role, access denied");
                            break;
                    }*/
                }else{
                    JOptionPane.showMessageDialog(null, "Invalid username or password");
                }
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
    
    private User getAuthenticatedUser(String username, String password){
        DbConnection db = new DbConnection();
        Connection con = db.getMyConnection();
        User user = null;

        if(con == null){
           JOptionPane.showMessageDialog(null, "Failed to connect to database.");
           return null;
        }

        try {
            String loginQuery = "SELECT * FROM user WHERE user_id = ? AND password = ?";
            PreparedStatement stmt = con.prepareStatement(loginQuery);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                user = new User(
                        rs.getString("user_id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("gender"),
                        rs.getString("telephone"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        } finally{
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error in closing connection: " + e.getMessage());
            }
        }

        return user;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("loginForm");
        frame.setContentPane(new LoginForm().Login);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
