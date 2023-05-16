import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class ReferenceFrame extends JPanel {

	Connection conn = null;
	PreparedStatement state = null;
	ResultSet resultSet = null;

	JPanel topPanel = new JPanel();
	JPanel midPanel = new JPanel();
	JPanel bottomPanel = new JPanel();

	JLabel selectMngrLb = new JLabel("Select manager:");
	ArrayList<String> comboItems = new ArrayList<String>();
	JComboBox<String> salesRepCB = new JComboBox<String>();
	
	JLabel selectSuppLb = new JLabel("Select supplier:");
	ArrayList<String> comboItems2 = new ArrayList<String>();
	JComboBox<String> suppCB = new JComboBox<String>();

	JTable table = new JTable();
	JScrollPane scroll = new JScrollPane(table);

	JButton searchBtn = new JButton("Search");
	JButton clearBtn = new JButton("Clear Search");

	public ReferenceFrame() {

		this.setLayout(new GridLayout(3, 1));

		// TOP
		topPanel.setLayout(new GridLayout(3, 2, 50, 50));
		topPanel.add(selectMngrLb);
		selectMngrLb.setHorizontalAlignment(JLabel.CENTER);
		selectMngrLb.setFont(new Font("Century Gothic", Font.BOLD, 22));
		selectMngrLb.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
		topPanel.add(salesRepCB);
		topPanel.add(selectSuppLb);
		selectSuppLb.setHorizontalAlignment(JLabel.CENTER);
		selectSuppLb.setFont(new Font("Century Gothic", Font.BOLD, 22));
		selectSuppLb.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
		topPanel.add(suppCB);
		this.add(topPanel);

		// MIDDLE
		midPanel.add(searchBtn);
		midPanel.add(clearBtn);
		searchBtn.addActionListener(new SearchAction());
		clearBtn.addActionListener(new ClearAction());
		this.add(midPanel);

		// BOTTOM
		scroll.setPreferredSize(new Dimension(500, 200));
		bottomPanel.add(scroll);
		refreshTable();
		populateCombo();
		populateCombo2();
		this.add(bottomPanel);
		this.setVisible(true);

	}

	
	public void refreshTable() {
	    conn = DBConnection.getConnection();
	    String sql = "SELECT M.FULLNAME AS Manager, SU.NAME AS Supplier, ST.NAME AS Stock, ST.QUANTITY AS Quantity "
	            + "FROM STOCK ST " + "JOIN SUPPLIER SU ON ST.SUPP_ID = SU.SUPP_ID "
	            + "JOIN MANAGER M ON ST.MANAGER_ID = M.ID";

	    try {
	        state = conn.prepareStatement(sql);
	        resultSet = state.executeQuery();
	        ResultSetMetaData metaData = resultSet.getMetaData();
	        int columnCount = metaData.getColumnCount();
	        String[] columnNames = new String[columnCount];

	        for (int i = 0; i < columnCount; i++) {
	            columnNames[i] = metaData.getColumnLabel(i + 1);
	        }
	        
	        columnNames[0] = "MANAGER";
	        columnNames[1] = "SUPPLIER";
	        columnNames[2] = "STOCK";
	        columnNames[3] = "QUANTITY";

	        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
	        while (resultSet.next()) {
	            Object[] rowData = new Object[columnCount];
	            for (int i = 0; i < columnCount; i++) {
	                rowData[i] = resultSet.getObject(i + 1);
	            }
	            model.addRow(rowData);
	        }
	        table.setModel(model);

	    } catch (SQLException e) {

	        e.printStackTrace();
	    } catch (Exception e) {

	        e.printStackTrace();
	    }

	}


	public void populateCombo() {

		conn = DBConnection.getConnection();

		try {
			state = conn.prepareStatement("SELECT FULLNAME FROM MANAGER");
			resultSet = state.executeQuery();
			TableModel tb = new TableModel(resultSet);
			HashSet<String> unItems = new HashSet<String>();

			int rowCount = tb.getRowCount();
			String item;

			for (int i = 0; i < rowCount; i++) {

				// Row count is 0 cuz resultSet only retrieves 1 column => 1st column = 0 index
				item = tb.getValueAt(i, 0).toString();
				comboItems.add(item);
			}

			comboItems.forEach(str -> unItems.add(str));
			salesRepCB.removeAllItems();
			unItems.forEach(it -> salesRepCB.addItem(it));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public void populateCombo2() {

		conn = DBConnection.getConnection();

		try {
			state = conn.prepareStatement("SELECT NAME FROM SUPPLIER");
			resultSet = state.executeQuery();
			TableModel tb = new TableModel(resultSet);
			HashSet<String> unItems = new HashSet<String>();

			int rowCount = tb.getRowCount();
			String item;

			for (int i = 0; i < rowCount; i++) {

				// Row count is 0 cuz resultSet only retrieves 1 column => 1st column = 0 index
				item = tb.getValueAt(i, 0).toString();
				comboItems2.add(item);
			}

			comboItems2.forEach(str -> unItems.add(str));
			suppCB.removeAllItems();
			unItems.forEach(it -> suppCB.addItem(it));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	
	class SearchAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnection.getConnection();
			String sql = "SELECT M.FULLNAME AS MANAGER, SU.NAME AS SUPPLIER, ST.NAME AS STOCK, ST.QUANTITY AS QUANTITY "
					+ "FROM STOCK ST " + "JOIN SUPPLIER SU ON ST.SUPP_ID = SU.SUPP_ID "
					+ "JOIN MANAGER M ON ST.MANAGER_ID = M.ID WHERE M.FULLNAME = ? AND SU.NAME=?";

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, salesRepCB.getSelectedItem().toString());
				state.setString(2, suppCB.getSelectedItem().toString());
				resultSet = state.executeQuery();
				try {

					table.setModel(new TableModel(resultSet));

				} catch (Exception e1) {

					e1.printStackTrace();
				}

			} catch (SQLException e1) {

				e1.printStackTrace();
			}

		}
	}
	
	class SearchAction2 implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnection.getConnection();
			//String sql = "SELECT * FROM SUPPLIER WHERE NAME=?";
			
			String sql = "SELECT SU.NAME AS NAME, SU.LOCATION, SU.VAT, M.FULLNAME AS NAME, ST.NAME "
					+ "FROM STOCK ST " + "JOIN SUPPLIER SU ON ST.SUPP_ID = SU.SUPP_ID "
					+ "JOIN MANAGER M ON ST.MANAGER_ID = M.ID WHERE SU.NAME = ?";

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, suppCB.getSelectedItem().toString());
				resultSet = state.executeQuery();
				try {

					table.setModel(new TableModel(resultSet));

				} catch (Exception e1) {

					e1.printStackTrace();
				}

			} catch (SQLException e1) {

				e1.printStackTrace();
			}

		}
	}
	
	
	
	// For clearBtn
			class ClearAction implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					refreshTable();
				}
			}

}
