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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;

public class ManagerFrame extends JPanel {

	Connection conn = null;
	PreparedStatement state = null;
	ResultSet resultSet = null;
	int id = -1;

	JPanel topPanel = new JPanel();
	JPanel midPanel = new JPanel();
	JPanel bottomPanel = new JPanel();

	JLabel nameLb = new JLabel("Full name:");
	JLabel regionLb = new JLabel("Region of duty:");
	JLabel uidLb = new JLabel("Unique Identity Number:");
	JLabel residenceLb = new JLabel("City of residence:");

	JTextField nameTf = new JTextField();
	JTextField regionTf = new JTextField();
	JTextField uidTf = new JTextField();
	JTextField residenceTf = new JTextField();

	JTable table = new JTable();
	JScrollPane scroll = new JScrollPane(table);

	JButton addBtn = new JButton("Add");
	JButton deleteBtn = new JButton("Delete");
	JButton updateBtn = new JButton("Update");
	JButton clearBtn = new JButton("Clear");
	JButton searchBtn = new JButton("Search by name");
	
	private StockFrame sf;
	private SupplierFrame supf;
	private ReferenceFrame rf;

	public ManagerFrame(StockFrame sf, SupplierFrame supf, ReferenceFrame rf) {

		this.setLayout(new GridLayout(3, 1));
		this.sf = sf;
		this.supf = supf;
		this.rf = rf;

		// TOP
		topPanel.setLayout(new GridLayout(4, 2));
		topPanel.add(nameLb);
		topPanel.add(nameTf);
		topPanel.add(regionLb);
		topPanel.add(regionTf);
		topPanel.add(uidLb);
		topPanel.add(uidTf);
		topPanel.add(residenceLb);
		topPanel.add(residenceTf);
		uidTf.setToolTipText("SSN/NINO/EGN");
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
	}

	public void refreshTable() {
		conn = DBConnection.getConnection();

		try {
			state = conn.prepareStatement("SELECT * FROM MANAGER");
			resultSet = state.executeQuery();
			table.setModel(new TableModel(resultSet));

		} catch (SQLException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
		sf.populateManager_Combo();
		supf.populateCombo();
		rf.populateCombo();
	}

	public void clearForm() {
		nameTf.setText("");
		regionTf.setText("");
		uidTf.setText("");
		residenceTf.setText("");
	}

	class MouseAction implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

			int row = table.getSelectedRow();
			// We give id value here to later be used in DeleteAction class
			id = Integer.parseInt(table.getValueAt(row, 0).toString());
			regionTf.setText(table.getValueAt(row, 1).toString());
			nameTf.setText(table.getValueAt(row, 2).toString());
			uidTf.setText(table.getValueAt(row, 3).toString());
			residenceTf.setText(table.getValueAt(row, 4).toString());
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
			String sql = "INSERT INTO MANAGER(FULLNAME, REGION, UID, RESIDENCE) VALUES(?,?,?,?)";

			try {
				state = conn.prepareStatement(sql);

				state.setString(1, nameTf.getText());
				state.setString(2, regionTf.getText());
				state.setString(3, uidTf.getText());
				state.setString(4, residenceTf.getText());

				state.execute();
				refreshTable();				
				clearForm();
			} 

			
			catch (SQLException e1) {

				e1.printStackTrace();
			}
		}
	}

	// For deleteBtn
	class DeleteAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnection.getConnection();
			String sql = "DELETE FROM MANAGER WHERE ID=?";

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
			String updateSQL = "UPDATE MANAGER SET FULLNAME=?, REGION=?, UID=?, RESIDENCE=? WHERE ID=?";

			try {
				PreparedStatement state = conn.prepareStatement(updateSQL);

				state.setString(1, nameTf.getText());
				state.setString(2, regionTf.getText());
				state.setString(3, uidTf.getText());
				state.setString(4, residenceTf.getText());
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
			String sql = "SELECT * FROM MANAGER WHERE FULLNAME = ?";
			int tableRowCount = 0;

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, nameTf.getText());
				resultSet = state.executeQuery();

				try {					
					table.setModel(new TableModel(resultSet));
					tableRowCount = table.getRowCount();
				

			
					if (tableRowCount == 0) {
						String sqlQ = "SELECT * FROM MANAGER WHERE FULLNAME LIKE '%" + nameTf.getText() + "%'";
				        state = conn.prepareStatement(sqlQ);
				        resultSet = state.executeQuery();
				        table.setModel(new TableModel(resultSet));
				        tableRowCount = table.getRowCount();
				        
				        if (tableRowCount == 0) {
				        	String sqlQ2 = "SELECT * FROM MANAGER WHERE UPPER(FULLNAME) LIKE '%" + nameTf.getText().toUpperCase() + "%'";
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
