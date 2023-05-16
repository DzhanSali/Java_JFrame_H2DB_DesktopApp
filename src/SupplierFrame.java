import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;

public class SupplierFrame extends JPanel {

	Connection conn = null;
	PreparedStatement state = null;
	ResultSet resultSet = null;
	int id = -1;

	JPanel topPanel = new JPanel();
	JPanel midPanel = new JPanel();
	JPanel bottomPanel = new JPanel();

	JLabel vatLb = new JLabel("VAT/BULSTAT:");
	JLabel nameLb = new JLabel("Name:");
	JLabel addressLb = new JLabel("Address:");
	JLabel salesRepLb = new JLabel("Sales Representative:");

	JTextField vatTf = new JTextField();
	JTextField nameTf = new JTextField();
	JTextField addressTf = new JTextField();

	ArrayList<String> comboItems = new ArrayList<String>();
	JComboBox<String> salesRepCB = new JComboBox<String>();

	JTable table = new JTable();
	JScrollPane scroll = new JScrollPane(table);

	JButton addBtn = new JButton("Add");
	JButton deleteBtn = new JButton("Delete");
	JButton updateBtn = new JButton("Update");
	JButton clearBtn = new JButton("Clear");
	JButton searchBtn = new JButton("Search by name");
	
	private StockFrame sf;

	public SupplierFrame(StockFrame sf) {

		this.setLayout(new GridLayout(3, 1));
		this.sf= sf;

		// TOP
		topPanel.setLayout(new GridLayout(4, 2));
		topPanel.add(vatLb);
		topPanel.add(vatTf);
		topPanel.add(nameLb);
		topPanel.add(nameTf);
		topPanel.add(addressLb);
		topPanel.add(addressTf);
		topPanel.add(salesRepLb);
		topPanel.add(salesRepCB);
		vatTf.setToolTipText("Unique Identification Number");
		ToolTipManager.sharedInstance().setInitialDelay(200);
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
		refreshTable();
		table.addMouseListener(new MouseAction());
		this.add(bottomPanel);
		this.setVisible(true);
		populateCombo();
		
	}

	 public void populateCombo() {

		conn = DBConnection.getConnection();

		try {
			state = conn.prepareStatement("SELECT FULLNAME FROM MANAGER");
			resultSet = state.executeQuery();
			TableModel tb = new TableModel(resultSet);

			int rowCount = tb.getRowCount();
			String item;
			comboItems.clear();
			comboItems.add("");

			for (int i = 0; i < rowCount; i++) {

				// Row count is 0 cuz resultSet only retrieves 1 column => 1st column = 0 index
				item = tb.getValueAt(i, 0).toString();
				comboItems.add(item);
			}

			salesRepCB.removeAllItems();
			comboItems.forEach(comboItems -> salesRepCB.addItem(comboItems));
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	


	public void refreshTable() {
		conn = DBConnection.getConnection();

		try {
			state = conn.prepareStatement("SELECT SUPP_ID, NAME, LOCATION, (SELECT FULLNAME FROM MANAGER M WHERE M.ID = SUPPLIER.MANAGER_ID) AS MANAGER, VAT FROM SUPPLIER");
			resultSet = state.executeQuery();
			table.setModel(new TableModel(resultSet));

		} catch (SQLException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
		sf.supplierCB.removeAllItems();
		sf.populateSupplier_Combo();
	}

	public void clearForm() {
		nameTf.setText("");
		vatTf.setText("");
		addressTf.setText("");
		salesRepCB.setSelectedItem("");
	}

	class MouseAction implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

			int row = table.getSelectedRow();
			// We give id value here to later be used in DeleteAction class
			id = Integer.parseInt(table.getValueAt(row, 0).toString());
			nameTf.setText(table.getValueAt(row, 1).toString());
			addressTf.setText(table.getValueAt(row, 2).toString());
			salesRepCB.setSelectedItem(table.getValueAt(row, 3).toString());
			vatTf.setText(table.getValueAt(row, 4).toString());
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
			String sql = "INSERT INTO SUPPLIER(NAME, LOCATION, MANAGER_ID, VAT) "
					+ "VALUES(?,?, (SELECT ID FROM MANAGER WHERE FULLNAME = ?),?)";

			try {
				state = conn.prepareStatement(sql);

				state.setString(1, nameTf.getText());
				state.setString(2, addressTf.getText());
				state.setString(3, salesRepCB.getSelectedItem().toString());
				state.setString(4, vatTf.getText());

				state.execute();
				refreshTable();
				populateCombo();
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
			String sql = "DELETE FROM SUPPLIER WHERE SUPP_ID=?";

			try {
				state = conn.prepareStatement(sql);
				// id already has a value from AddAction class
				state.setInt(1, id);
				state.execute();
				refreshTable();
				clearForm();
				id = -1;
			} catch (SQLException e1) {

				e1.printStackTrace();
			}

		}

	}

	// For clearBtn
	class ClearAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			clearForm();
		}
	}

	class UpdateAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			conn = DBConnection.getConnection();
			String updateSQL = "UPDATE SUPPLIER " + "SET(NAME, LOCATION, MANAGER_ID, VAT) "
					+ "= VALUES(?, ?, (SELECT ID FROM MANAGER WHERE FULLNAME = ?) , ?) WHERE SUPP_ID = ?";

			try {
				PreparedStatement state = conn.prepareStatement(updateSQL);

				state.setString(1, nameTf.getText());
				state.setString(2, addressTf.getText());
				state.setString(3, salesRepCB.getSelectedItem().toString());
				state.setString(4, vatTf.getText());
				state.setInt(5, id);

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
			String sql = "SELECT * FROM SUPPLIER WHERE NAME = ?";
			int tableRowCount = 0;

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, nameTf.getText());
				resultSet = state.executeQuery();

				try {
					table.setModel(new TableModel(resultSet));
					tableRowCount = table.getRowCount();

					if (tableRowCount == 0) {
						String sqlQ = "SELECT * FROM SUPPLIER WHERE NAME LIKE '%" + nameTf.getText() + "%'";
						state = conn.prepareStatement(sqlQ);
						resultSet = state.executeQuery();
						table.setModel(new TableModel(resultSet));
						tableRowCount = table.getRowCount();

						if (tableRowCount == 0) {
							String sqlQ2 = "SELECT * FROM SUPPLIER WHERE UPPER(NAME) LIKE '%"
									+ nameTf.getText().toUpperCase() + "%'";
							state = conn.prepareStatement(sqlQ2);
							resultSet = state.executeQuery();
							table.setModel(new TableModel(resultSet));
							tableRowCount = table.getRowCount();
						}

					}

				} catch (Exception e1) {
					e1.printStackTrace();
				}

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

}
