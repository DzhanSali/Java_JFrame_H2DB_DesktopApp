import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class StockFrame extends JPanel {

	Connection conn = null;
	PreparedStatement state = null;
	PreparedStatement selectSQL = null;
	ResultSet result = null;
	int id = -1;

	JPanel topPanel = new JPanel();
	JPanel midPanel = new JPanel();
	JPanel bottomPanel = new JPanel();

	JLabel stockLb = new JLabel("Stock name:");
	JLabel quantityLb = new JLabel("Quantity:");
	JLabel priceLb = new JLabel("Price:");
	JLabel supplierLb = new JLabel("Supplier:");
	JLabel managerLb = new JLabel("Inventory manager:");

	JTextField stockTf = new JTextField();
	JTextField quantityTf = new JTextField();
	JTextField priceTf = new JTextField();

	ArrayList<String> comboItems = new ArrayList<String>();
	ArrayList<String> comboItems2 = new ArrayList<String>();
	JComboBox<String> supplierCB = new JComboBox<String>();
	JComboBox<String> managerCB = new JComboBox<String>();

	JTable table = new JTable();
	JScrollPane scroll = new JScrollPane(table);

	JButton addBtn = new JButton("Add");
	JButton deleteBtn = new JButton("Delete");
	JButton updateBtn = new JButton("Update");
	JButton clearBtn = new JButton("Clear");
	JButton searchBtn = new JButton("Search by name");


	public StockFrame() {

		this.setLayout(new GridLayout(3, 1));


		// TOP
		topPanel.setLayout(new GridLayout(5, 2));
		topPanel.add(stockLb);
		topPanel.add(stockTf);
		topPanel.add(quantityLb);
		topPanel.add(quantityTf);
		topPanel.add(priceLb);
		topPanel.add(priceTf);
		topPanel.add(supplierLb);
		topPanel.add(supplierCB);
		topPanel.add(managerLb);
		topPanel.add(managerCB);
		this.add(topPanel);

		// MIDDLE
		midPanel.add(addBtn);
		midPanel.add(deleteBtn);
		midPanel.add(updateBtn);
		midPanel.add(clearBtn);
		midPanel.add(searchBtn);
		this.add(midPanel);

		addBtn.addActionListener(new AddAction());
		deleteBtn.addActionListener(new DeleteAction());
		clearBtn.addActionListener(new ClearAction());
		updateBtn.addActionListener(new UpdateAction());
		searchBtn.addActionListener(new SearchAction());

		// BOTTOM
		scroll.setPreferredSize(new Dimension(500, 200));
		bottomPanel.add(scroll);
		this.add(bottomPanel);
		refreshTable();
		table.addMouseListener(new MouseAction());
		this.setVisible(true);
		populateManager_Combo();
		populateSupplier_Combo();

	}

	public void populateManager_Combo() {

		conn = DBConnection.getConnection();

		try {
			state = conn.prepareStatement("SELECT FULLNAME FROM MANAGER");
			result = state.executeQuery();
			TableModel tb = new TableModel(result);
			HashSet<String> unItems = new HashSet<String>();

			int rowCount = tb.getRowCount();
			String item;
			comboItems.add("");

			for (int i = 0; i < rowCount; i++) {

				// Row count is 0 cuz resultSet only retrieves 1 column => 1st column = 0 index
				item = tb.getValueAt(i, 0).toString();
				comboItems.add(item);
			}
			
			comboItems.forEach(str -> unItems.add(str));
			managerCB.removeAllItems();
			// Using HashSet to avoid duplicating data since the ComboBox is populated once on startup of project and later every time there is a change in Manager table
			unItems.forEach(it -> managerCB.addItem(it));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void populateSupplier_Combo() {

		conn = DBConnection.getConnection();

		try {
			state = conn.prepareStatement("SELECT NAME FROM SUPPLIER");
			result = state.executeQuery();
			TableModel tb = new TableModel(result);
			HashSet<String> unItems = new HashSet<String>();

			int rowCount = tb.getRowCount();
			String item;
			comboItems2.add("");

			for (int i = 0; i < rowCount; i++) {

				// Row count is 0 cuz resultSet only retrieves 1 column => 1st column = 0 index
				item = tb.getValueAt(i, 0).toString();
				comboItems2.add(item);
			}
			
			comboItems2.forEach(str -> unItems.add(str));
			supplierCB.removeAllItems();			
			// Using HashSet to avoid duplicating data since the ComboBox is populated once on startup of project and later every time there is a change in Supplier table
			unItems.forEach(it -> supplierCB.addItem(it));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void refreshTable() {
		conn = DBConnection.getConnection();

		try {
			state = conn.prepareStatement("SELECT STOCK_ID, NAME, QUANTITY, PRICE,"
					+ "       (SELECT FULLNAME FROM MANAGER M WHERE M.ID = STOCK.MANAGER_ID) AS MANAGER, "
					+ "       (SELECT NAME FROM SUPPLIER S WHERE S.SUPP_ID = STOCK.SUPP_ID) AS SUPPLIER "
								+ "FROM STOCK");
			result = state.executeQuery();
			table.setModel(new TableModel(result));

		} catch (SQLException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void clearForm() {
		stockTf.setText("");
		quantityTf.setText("");
		priceTf.setText("");
		supplierCB.setSelectedItem("");
		managerCB.setSelectedItem("");
	}

	class MouseAction implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

			int row = table.getSelectedRow();
			// We give id value here to later be used in DeleteAction class
			id = Integer.parseInt(table.getValueAt(row, 0).toString());
			stockTf.setText(table.getValueAt(row, 1).toString());
			quantityTf.setText(table.getValueAt(row, 2).toString());
			priceTf.setText(table.getValueAt(row, 3).toString());
			managerCB.setSelectedItem(table.getValueAt(row, 4).toString());
			supplierCB.setSelectedItem(table.getValueAt(row, 5).toString());

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	// For addBtn
	class AddAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnection.getConnection();
			String sql = "INSERT INTO STOCK(NAME, QUANTITY, PRICE, SUPP_ID, MANAGER_ID) "
					+ "VALUES(?,?,?,(SELECT SUPP_ID FROM SUPPLIER WHERE NAME = ?), "
					+ "(SELECT ID FROM MANAGER WHERE FULLNAME = ?))";

			try {
				state = conn.prepareStatement(sql);

				state.setString(1, stockTf.getText());
				state.setInt(2, Integer.parseInt(quantityTf.getText()));
				state.setBigDecimal(3, new BigDecimal(priceTf.getText()));
				state.setString(4, supplierCB.getSelectedItem().toString());
				state.setString(5, managerCB.getSelectedItem().toString());

				state.execute();
				refreshTable();
				clearForm();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		}
	}

	// For deleteBtn
	class DeleteAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnection.getConnection();
			String sql = "DELETE FROM STOCK WHERE STOCK_ID=?";

			try {
				state = conn.prepareStatement(sql);
				// id already has a value from AddAction class
				state.setInt(1, id);
				state.execute();
				refreshTable();
				clearForm();
				id = -1;
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	// For clearBtn
	class ClearAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			clearForm();
			refreshTable();

		}
	}

	class UpdateAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			conn = DBConnection.getConnection();
			String updateSQL = "UPDATE STOCK " + "SET(NAME, QUANTITY, PRICE, SUPP_ID, MANAGER_ID) "
					+ "= VALUES(?,?,?,(SELECT SUPP_ID FROM SUPPLIER WHERE NAME = ?), "
					+ "(SELECT ID FROM MANAGER WHERE FULLNAME = ?)) " + "WHERE STOCK_ID = ?";

			try {
				PreparedStatement state = conn.prepareStatement(updateSQL);

				state.setString(1, stockTf.getText());
				state.setInt(2, Integer.parseInt(quantityTf.getText()));
				state.setBigDecimal(3, new BigDecimal(priceTf.getText()));
				state.setString(4, supplierCB.getSelectedItem().toString());
				state.setString(5, managerCB.getSelectedItem().toString());
				state.setInt(6, id);
				state.executeUpdate();
				refreshTable();
				clearForm();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	// For searchBtn
	class SearchAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			conn = DBConnection.getConnection();
			String sql = "SELECT * FROM STOCK WHERE UPPER(NAME) = ?";

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, stockTf.getText().toUpperCase());
				result = state.executeQuery();

				try {					
					table.setModel(new TableModel(result));
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

}
