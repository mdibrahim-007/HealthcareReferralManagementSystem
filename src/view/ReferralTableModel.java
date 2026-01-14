package view;

import model.Referral;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ReferralTableModel extends AbstractTableModel {

    private final String[] columns = {
            "Referral ID",
            "Patient ID",
            "Urgency",
            "Reason",
            "Status",
            "Referral Date",
            "Referring Clinician",
            "Referred To Clinician",
            "From Facility",
            "To Facility",
            "Appointment ID"
    };

    private List<Referral> data = new ArrayList<>();

    public void setData(List<Referral> list) {
        this.data = (list == null) ? new ArrayList<>() : list;
        fireTableDataChanged();
    }

    public Referral getAt(int row) {
        if (row < 0 || row >= data.size()) return null;
        return data.get(row);
    }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int col) { return columns[col]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Referral r = data.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> r.getReferralId();
            case 1 -> r.getPatientId();
            case 2 -> r.getUrgencyLevel();
            case 3 -> r.getReferralReason();
            case 4 -> r.getStatus();
            case 5 -> r.getReferralDate();
            case 6 -> r.getReferringClinicianId();
            case 7 -> r.getReferredToClinicianId();
            case 8 -> r.getReferringFacilityId();
            case 9 -> r.getReferredToFacilityId();
            case 10 -> r.getAppointmentId();
            default -> "";
        };
    }
}
