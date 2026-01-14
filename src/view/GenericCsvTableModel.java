package view;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class GenericCsvTableModel extends AbstractTableModel {

    private String[] columns = new String[0];
    private List<String[]> rows = new ArrayList<>();

    public void setData(String[] header, List<String[]> dataRows) {
        this.columns = (header == null) ? new String[0] : header;
        this.rows = (dataRows == null) ? new ArrayList<>() : dataRows;
        fireTableStructureChanged();
    }

    public String[] getHeader() { return columns; }

    public List<String[]> getRowsCopy() {
        return new ArrayList<>(rows);
    }

    public String[] getAt(int row) {
        if (row < 0 || row >= rows.size()) return null;
        return rows.get(row);
    }

    public void addRow(String[] r) {
        rows.add(r);
        fireTableDataChanged();
    }

    public void updateRow(int idx, String[] r) {
        if (idx < 0 || idx >= rows.size()) return;
        rows.set(idx, r);
        fireTableDataChanged();
    }

    public void deleteRow(int idx) {
        if (idx < 0 || idx >= rows.size()) return;
        rows.remove(idx);
        fireTableDataChanged();
    }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int col) { return columns[col]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String[] r = rows.get(rowIndex);
        if (r == null || columnIndex < 0 || columnIndex >= r.length) return "";
        return r[columnIndex];
    }
}
