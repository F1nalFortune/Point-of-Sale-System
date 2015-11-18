import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Transaction_Interface extends JFrame implements ActionListener
{
	private PointOfSale transaction;
	private Management management = new Management();
	
	private JButton addItem;
	private JButton removeItem;
	private JButton endTransaction;
	private JButton cancelTransaction;
	private String phone;	
	private long phoneNum;
	private JTextArea transactionDialog;
	
	private String databaseFile;
	
	String operation = "";
	
	JScrollPane scroll;
	
	public Transaction_Interface(String operation)
	{
		super ("SG Technologies - Transaction View");
		setLayout(null);
		
		
		this.operation = operation;
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		int xSize = ((int) tk.getScreenSize().getWidth());
		int ySize = ((int) tk.getScreenSize().getHeight());
		
		setSize(xSize,ySize);
		//setLocation(500,280);
		
		addItem = new JButton("Add Item");
		addItem.setBounds(xSize*4/5,ySize/6,150,80);
		add(addItem);
		
		removeItem = new JButton("Remove Item");
		removeItem.setBounds(xSize*4/5,ySize*2/6,150,80);
		add(removeItem);
		
		endTransaction = new JButton("End");
		endTransaction.setBounds(xSize*4/5,ySize*3/6,150,80);
		add(endTransaction);
		
		cancelTransaction = new JButton("Cancel");
		cancelTransaction.setBounds(xSize*4/5,ySize*4/6,150,80);
		add(cancelTransaction);
		
		addItem.addActionListener(this);
		removeItem.addActionListener(this);
		endTransaction.addActionListener(this);
		cancelTransaction.addActionListener(this);
		
		
		transactionDialog=new JTextArea();  
		transactionDialog.setBackground(Color.white);  
		transactionDialog.setForeground(Color.black);  
		transactionDialog.setEditable(false);
		Font font = transactionDialog.getFont();
		float size = font.getSize() + 5.0f;
		transactionDialog.setFont( font.deriveFont(size) );
		
		scroll = new JScrollPane (transactionDialog, 
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBounds(xSize/16,ySize/16,3*xSize/5,4*ySize/5);  
		add(scroll);
		
		if (operation.equals("Sale"))
		{
			transaction = new POS();
			databaseFile = "Database/itemDatabase.txt";
		}
		
		if (operation.equals("Rental"))
		{
			getCustomerPhone();
			databaseFile = "Database/rentalDatabase.txt";
			transaction = new POR(phoneNum);
		}
		
		if (operation.equals("Return"))
		{
			
			getCustomerPhone();
			databaseFile = "Database/itemDatabase.txt";
			transaction = new POH(phoneNum);
		}
		
		transaction.startNew(databaseFile);
		
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource() == addItem)
		{
			EnterItem_Interface enterItem = new EnterItem_Interface(transaction,true,transactionDialog);
			enterItem.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			enterItem.setVisible(true);
				
		}
		if (event.getSource() == removeItem)
		{
			EnterItem_Interface removeItem = new EnterItem_Interface(transaction,false,transactionDialog);
			removeItem.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			removeItem.setVisible(true);
			
		}
		
		if (event.getSource() == endTransaction) //goes to payment screen
		{
			String coupon ="";
			if (operation.equals("Sale"))
			{
				coupon = JOptionPane.showInputDialog("Enter coupon code if user has one");
				if (!coupon.equals(""))
					if (!transaction.coupon(coupon))
						JOptionPane.showMessageDialog(null, "Invalid coupon");
			}
			
			Payment_Interface payment = new Payment_Interface(transaction,databaseFile,operation);
			payment.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			payment.setVisible(true);
			
			this.setVisible(false);
			this.dispose();
			
		}
		
		
		if (event.getSource() == cancelTransaction) //cancels transaction for customer
		{
			JOptionPane.showMessageDialog(null,"Transaction Has Been Cancelled");
			Cashier_Interface cashier = new Cashier_Interface();
			cashier.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			cashier.setVisible(true);
			
			this.setVisible(false);
			dispose();
		}
	}
	
	private void getCustomerPhone()
	{
		phone = JOptionPane.showInputDialog("Please enter customer's phone number");
		while ((phoneNum = Long.parseLong(phone)) > 9999999999l || (phoneNum < 1000000000l))
		{
			JOptionPane.showMessageDialog(null, "Invalid phone number. Please enter again");
			phone = JOptionPane.showInputDialog("Please enter customer's phone number");
		}
		 if (!management.checkUser(phoneNum))
		 {
			 if(management.createUser(phoneNum))
				 JOptionPane.showMessageDialog(null, "New customer was registered");
			 else
				 JOptionPane.showMessageDialog(null, "New customer couldn't be registered");
		 }
	}
}
