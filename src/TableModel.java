import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class TableModel extends AbstractTableModel {
    
	private ResultSet resultSet;
    private ArrayList<Object[]> data = new ArrayList<>();

    public TableModel(ResultSet rs) throws Exception {
        resultSet = rs;
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int j = 0; j < columnCount; j++) {
                row[j] = rs.getObject(j + 1);
            }
            data.add(row);
        }
   }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        try {
            return resultSet.getMetaData().getColumnCount();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex)[columnIndex];
    }

    @Override
    public String getColumnName(int columnIndex) {
        try {
            return resultSet.getMetaData().getColumnName(columnIndex+1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}