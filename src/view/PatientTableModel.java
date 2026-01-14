package view;

import model.Patient;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PatientTableModel extends AbstractTableModel {
    private final String[] columns = {
            "Patient ID",
            "First Name",
            "Last Name",
            "Date of Birth",
            "NHS Number",
            "Gender",
            "Phone Number",
            "Email",
            "Address",
            "Postcode",
            "Emergency Contact Name",
            "Emergency Contact Phone",
            "Registration Date",
            "GP Surgery ID"
    };

    private List<Patient> data = new ArrayList<>();

    public void setData(List<Patient> patients) {
        this.data = (patients == null) ? new ArrayList<>() : patients;
        fireTableDataChanged();
    }

    public Patient getAt(int row) {
        if (row < 0 || row >= data.size()) return null;
        return data.get(row);
    }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int col) { return columns[col]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Patient p = data.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> nz(p.getPatientId());
            case 1 -> nz(p.getFirstName());
            case 2 -> nz(p.getLastName());
            case 3 -> nz(p.getDateOfBirth());
            case 4 -> nz(p.getNhsNumber());
            case 5 -> nz(p.getGender());
            case 6 -> nz(p.getPhoneNumber());
            case 7 -> nz(p.getEmail());
            case 8 -> nz(p.getAddress());
            case 9 -> nz(p.getPostcode());
            case 10 -> nz(p.getEmergencyContactName());
            case 11 -> nz(p.getEmergencyContactPhone());
            case 12 -> nz(p.getRegistrationDate());
            case 13 -> nz(p.getGpSurgeryId());
            default -> "";
        };
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
