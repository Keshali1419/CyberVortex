import db.DbConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MedicalForm {
    private JPanel Medical;
    private JPanel FormMedical;
    private JTextField MedId;
    private JButton UpdateBtn;
    private JPanel ViewMedicals;
    private JTextField medIdforView;
    private JButton viewBtn;
    private JButton deleteBtn;
    private JTable MedicalTable;
    private JRadioButton approvedRadioButton;
    private JRadioButton rejectedRadioButton;
    private ButtonGroup statusGroup;

    public MedicalForm() {
        statusGroup = new ButtonGroup();
        statusGroup.add(approvedRadioButton);
        statusGroup.add(rejectedRadioButton);

        loadAllMedicals();

        UpdateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMedicalStatus();
            }
        });
        viewBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewMedicalById();
            }
        });
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMedical();
            }
        });
    }

    private void loadAllMedicals() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Med_Id", "Student_Id", "Course_Code", "Date", "Description", "Status"});
        MedicalTable.setModel(model);

        try(Connection con = DbConnection.getMyConnection()) {
            String medQuery = "SELECT * FROM medical";
            ResultSet rs = con.prepareStatement(medQuery).executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("med_id"),
                        rs.getString("stuId"),
                        rs.getString("courseCode"),
                        rs.getString("submissionDate"),
                        rs.getString("description"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(Medical, "DB load error" + e.getMessage());
        }
    }

    private void updateMedicalStatus(){
        String medId = MedId.getText().trim();
        if(medId.isEmpty() || (!approvedRadioButton.isSelected() && !rejectedRadioButton.isSelected())){
            JOptionPane.showMessageDialog(Medical, "Please select approved or rejected");
            return;
        }

        String status = approvedRadioButton.isSelected() ? "Approved" : "Rejected";

        try(Connection con = DbConnection.getMyConnection()) {
            String updateMedQuery = "UPDATE medical SET status = ? WHERE med_id = ?";
            PreparedStatement ps = con.prepareStatement(updateMedQuery);
            ps.setString(1, status);
            ps.setString(2, medId);

            int updated = ps.executeUpdate();
            if(updated > 0){
                JOptionPane.showMessageDialog(Medical, "Updated successfully");
                loadAllMedicals();
            } else {
                JOptionPane.showMessageDialog(Medical, "Update failed");
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(Medical, "DB update error" + e.getMessage());
        }
    }

    private void viewMedicalById() {
        String id = medIdforView.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(Medical, "Please enter a valid id");
            return;
        }

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Med_Id", "Student_Id", "Course_Code", "Date", "Description", "Status"});
        MedicalTable.setModel(model);

        try(Connection con = DbConnection.getMyConnection()){
            String medViewQuery = "SELECT * FROM medical WHERE med_id = ?";
            PreparedStatement ps = con.prepareStatement(medViewQuery);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                model.addRow(new Object[]{
                        rs.getString("med_id"),
                        rs.getString("stuId"),
                        rs.getString("courseCode"),
                        rs.getString("submissionDate"),
                        rs.getString("description"),
                        rs.getString("status")
                });
            } else {
                JOptionPane.showMessageDialog(Medical, "No record found");
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(Medical, "DB view error" + e.getMessage());
        }
    }

    private void deleteMedical() {
        String id = medIdforView.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(Medical, "Please enter a valid id");
            return;
        }

        try (Connection con = DbConnection.getMyConnection()){
            String deleteQuery = "DELETE FROM medical WHERE med_id = ?";
            PreparedStatement ps = con.prepareStatement(deleteQuery);
            ps.setString(1, id);

            int deleted = ps.executeUpdate();
            if(deleted > 0){
                JOptionPane.showMessageDialog(Medical, "Deleted successfully");
                loadAllMedicals();
            } else {
                JOptionPane.showMessageDialog(Medical, "Delete failed");
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(Medical, "DB delete error" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MedicalForm");
        frame.setContentPane(new MedicalForm().Medical);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
