package view;

import model.Prescription;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionTableModel extends AbstractTableModel {

    private final String[] columns = {
            "Prescription ID",
            "Patient ID",
            "Clinician ID",
            "Appointment ID",
            "Prescription Date",
            "Medication",
            "Dosage",
            "Frequency",
            "Duration Days",
            "Quantity",
            "Instructions",
            "Pharmacy",
            "Status",
            "Issue Date",
            "Collection Date"
    };

    private List<Prescription> data = new ArrayList<>();

    public void setData(List<Prescription> list) {
        this.data = (list == null) ? new ArrayList<>() : list;
        fireTableDataChanged();
    }

    public Prescription getAt(int row) {
        if (row < 0 || row >= data.size()) return null;
        return data.get(row);
    }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int col) { return columns[col]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Prescription p = data.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> p.getPrescriptionId();
            case 1 -> p.getPatientId();
            case 2 -> p.getClinicianId();
            case 3 -> p.getAppointmentId();
            case 4 -> p.getPrescriptionDate();
            case 5 -> p.getMedicationName();
            case 6 -> p.getDosage();
            case 7 -> p.getFrequency();
            case 8 -> p.getDurationDays();
            case 9 -> p.getQuantity();
            case 10 -> p.getInstructions();
            case 11 -> p.getPharmacyName();
            case 12 -> p.getStatus();
            case 13 -> p.getIssueDate();
            case 14 -> p.getCollectionDate();
            default -> "";
        };
    }
}
