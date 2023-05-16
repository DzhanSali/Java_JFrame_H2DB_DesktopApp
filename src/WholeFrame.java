import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class WholeFrame extends JFrame {

	JPanel stockPanel = new JPanel();
	JPanel managerPanel = new JPanel();
	JPanel supplierPanel = new JPanel();
	JPanel referencePanel = new JPanel();

	JTabbedPane tab = new JTabbedPane();
	
	StockFrame sf = new StockFrame();
	SupplierFrame supf = new SupplierFrame(sf);
	ReferenceFrame rf = new ReferenceFrame();
	ManagerFrame mf = new ManagerFrame(sf, supf, rf);
	

	public WholeFrame() {
		this.setBounds(450, 20, 530, 720);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		stockPanel.add(sf);

		managerPanel.add(mf);

		supplierPanel.add(supf);

		referencePanel.add(rf);

		tab.add(stockPanel, "Stock");
		tab.add(managerPanel, "Managers");
		tab.add(supplierPanel, "Suppliers");
		tab.add(referencePanel, "Reference");

		this.add(tab);
		this.setVisible(true);

	}

}