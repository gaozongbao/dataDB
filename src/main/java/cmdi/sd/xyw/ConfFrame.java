package cmdi.sd.xyw;

import cmdi.sd.db.DBHelper;
import cmdi.sd.util.SysParam;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.ConnectException;
import java.sql.Connection;
import java.util.HashMap;


public class ConfFrame {
	public JFrame frame;
	// 总窗体
	public JPanel panelContainer;
	// 头部窗体
	public JPanel headPanel;
	// 中间窗体
	public JPanel bottomPanel;
	// 底部窗体
	public JPanel middlePanel;
	// 日志
	public JTextArea rizhiArea = new JTextArea("系统日志打印窗口");
	JScrollPane rizhiJScrollPane;
	// 数据
	public HashMap<Integer, HashMap<String, String>> data;
	// 表格
	public JTable table;

    public DBHelper dbHelper;

	public JTable confTable;
	public String[] rowName = {"驱动信息","连接信息","用户名","密码"};
	public String[] columnName = {"配置名称", "配置信息"};

	public JButton initButton;
	public JButton cleanButton;


	public void init(){
		panelContainer = new JPanel();
		createHeadPanel();
		createMiddlePanel();
		createBottomPanel();
		// 为总窗体设定布局
		panelContainer.setLayout(new GridBagLayout());
		// 设置头部
		GridBagConstraints ch = new GridBagConstraints();
		ch.gridx = 0;
		ch.gridy = 0;
		ch.gridwidth = 5;
		ch.gridheight = 1;
		ch.anchor = GridBagConstraints.CENTER;
		ch.weightx = 0;
		ch.weighty = 0;
		ch.fill = GridBagConstraints.BOTH;

		panelContainer.add(headPanel, ch);
		// 设置top
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 0;
		c1.gridy = 1;
		c1.gridwidth = 5;
		c1.gridheight = 2;
		c1.weightx = 1;
		c1.weighty = 1;
		c1.fill = GridBagConstraints.BOTH;
//		panelContainer.add(topPanel, c1);
		// 设置middle
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 0;
		c2.gridy = 3;
		c2.gridwidth = 5;
		c2.gridheight = 1;
		c2.weightx = 1;
		c2.weighty = 1;
		c2.fill = GridBagConstraints.BOTH;
		panelContainer.add(middlePanel, c2);
		// 设置bottom
		GridBagConstraints c3 = new GridBagConstraints();
		c3.gridx = 0;
		c3.gridy = 4;
		c3.gridwidth = 5;
		c3.gridheight = 2;
		c3.weightx = 1;
		c3.weighty = 1;
		c3.fill = GridBagConstraints.BOTH;
		panelContainer.add(rizhiJScrollPane, c3);

		// 创建窗体
		frame = new JFrame("Boxlayout布局演示");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		panelContainer.setOpaque(true);
		frame.setSize(900, 700);                          // 设置窗口大小
		Dimension displaySize = Toolkit.getDefaultToolkit().getScreenSize(); // 获得显示器大小对象
		Dimension frameSize = frame.getSize();
		if (frameSize.width > displaySize.width)
			frameSize.width = displaySize.width/2;           // 窗口的宽度不能大于显示器的宽度
		if (frameSize.height > displaySize.height)
			frameSize.height = displaySize.height/2;          // 窗口的高度不能大于显示器的高度
		frame.setLocation((displaySize.width - frameSize.width) / 2,
				(displaySize.height - frameSize.height) / 2); // 设置窗口居中显示器显示

		frame.setContentPane(panelContainer);



		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				System.out.println("强制关闭系统");
				info("强制关闭系统");
				System.exit(-1);
			}
		});
		frame.setVisible(true);

	}
	public static void main(String[] args) {
		new ConfFrame().init();
	}

	private void createHeadPanel() {
		headPanel = new JPanel();
		//headPanel.setBackground(Color.blue);
		JTextField field = new JTextField("数据库配置");
		field.setFont(new Font("Dialog", 0, 40));
		field.setBorder(BorderFactory.createEmptyBorder());
		field.setEditable(false);
		headPanel.add(field);
	}


	private void createMiddlePanel() {
		// 创建 bottomPanel
		middlePanel = new JPanel();

		Object[][] rowData = new Object[rowName.length][columnName.length];
		for (int i = 0; i < rowName.length; i++) {
			rowData[i][0] = rowName[i];
		}

		rowData[0][1] = "com.mysql.jdbc.Driver";
		rowData[1][1] = "jdbc:mysql://localhost:3306/test";
		rowData[2][1] = "root";
		rowData[3][1] = "cmdi1234";

		confTable = new JTable(new DefaultTableModel(rowData, columnName){

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				if (column == 0)
					return false;
				return true;
			}

		});
		confTable.putClientProperty("terminateEditOnFocusLost", true);
		confTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 1) {
					int columnIndex = confTable.columnAtPoint(e.getPoint()); //获取点击的列
					int rowIndex = confTable.rowAtPoint(e.getPoint()); //获取点击的行
					System.out.println(rowIndex + "," + columnIndex);
				}
			}
		});
		confTable.getTableHeader().setFont(new Font("Dialog", 0, 15));
		confTable.setRowHeight(20);
		confTable.setFont(new Font("Dialog", 0, 20));

		JScrollPane scrollPane = new JScrollPane(confTable);
		scrollPane.setPreferredSize(new Dimension(100, 100));
//		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// 先加入一个不可见的 Strut，从而使 topPanel 对顶部留出一定的空间
		//middlePanel.add(Box.createVerticalStrut(1));
		// 加入包含表格的滚动窗格
		//scrollPane.setBackground(Color.YELLOW);


		initButton = new JButton("系统初始化连接");
		cleanButton = new JButton("清空");


		middlePanel.add(scrollPane);
		// 设置 bottomPanel 为垂直布局
		middlePanel .setLayout(new BoxLayout(middlePanel,BoxLayout.Y_AXIS ));
		// 创建包含两个按钮的 buttonPanel
		JPanel buttonPanel = new JPanel();
		// 设置 bottomPanel 为水平布局
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS ));
		//加入一个 glue, glue 会挤占两个按钮之间的空间
		buttonPanel.add(Box.createHorizontalStrut(15));
		buttonPanel.add(initButton);
		buttonPanel.add(Box.createHorizontalGlue());
		// 将查询按钮加入到 buttonPanel
		buttonPanel.add(cleanButton);
		buttonPanel.add(Box.createHorizontalGlue ());


		cleanButton.setEnabled(true);

		// 加入 buttonPanel
		middlePanel .add(buttonPanel);
		// 加入一个 Strut，从而使 bottomPanel 和底部之间留出距离

		initButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				info("系统正在建立连接......");
				String driver = confTable.getValueAt(0,1).toString();
				String url = confTable.getValueAt(1,1).toString();
				String username = confTable.getValueAt(2,1).toString();
				String password = confTable.getValueAt(3,1).toString();
				System.out.println(password + "aaa");
                dbHelper =  new DBHelper(driver, url, username, password);
				System.out.println(dbHelper);
				Connection connection = dbHelper.initDB();
				info(connection);
				if(connection != null){
					info("初始化成功");
					frame.setVisible(false);
					SysParam.driverClass = driver;
					SysParam.url = url;
					SysParam.username = username;
					SysParam.password = password;
					dbHelper.closeConn();
					new MyBoxDemo().init();
				}else {
					info("初始化失败");
					confTable.setEnabled(true);
				}

			}

		});


		rizhiArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				rizhiArea.setCaretPosition(rizhiArea.getText().length());
				rizhiArea.paintImmediately(rizhiArea.getBounds());
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {


			}
		});

		cleanButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("aaaa");
				if(cleanButton.isEnabled()) {
					for (int i = 0; i < rowName.length; i++) {
						confTable.setValueAt(null, i,1);
					}

				}
			}
		});

	}

	private void createBottomPanel() {
		// 创建 middlePanel
		rizhiArea = new JTextArea("日志打印窗口");
		rizhiJScrollPane = new JScrollPane(rizhiArea);
		rizhiJScrollPane.setBackground(Color.YELLOW);
	}
    public void info(Object obj) {
        if(obj != null) {
            rizhiArea.append("\n" + obj.toString());
        }
        rizhiArea.setCaretPosition(rizhiArea.getText().length());
        rizhiArea.paintImmediately(rizhiArea.getBounds());

    }

}
