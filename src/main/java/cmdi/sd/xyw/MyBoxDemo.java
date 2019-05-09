package cmdi.sd.xyw;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

import cmdi.sd.db.DBHelper;
import cmdi.sd.util.SysParam;
import com.csvreader.CsvReader;


public class MyBoxDemo {
	// 总窗体
	public JPanel panelContainer;
	// 头部窗体
	public JPanel headPanel;
	// 中间窗体
	public JPanel bottomPanel;
	// 底部窗体
	public JPanel middlePanel;

	public JScrollPane columnandFileIndex;
	public JTable columnandFileIndexTable;
	// 日志
	public JTextArea rizhiArea = new JTextArea("系统日志打印窗口");
	JScrollPane rizhiJScrollPane;
	// 数据
	public HashMap<Integer, HashMap<String, String>> data;


	public JButton upButton;
	public JButton downButton;
	public int stepCount = 1;
	public JButton valButton;

	public Object messageFlag;


	public DBHelper dbHelper;

	public StringBuilder rizhiSb = new StringBuilder();


	public void init(){

		panelContainer = new JPanel();
		createHeadPanel();
        createBottomPanel();
		createMiddlePanel();

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
		JFrame frame = new JFrame("Boxlayout布局演示");
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
				dbHelper.closeConn();
				if(messageFlag != null) {
//					messageFlag.release();
				}
				System.exit(-1);
			}
		});
		frame.setVisible(true);

	}
	public static void main(String[] args) {
		new MyBoxDemo().init();
	}

	private void createHeadPanel() {
		headPanel = new JPanel();
		//headPanel.setBackground(Color.blue);
		JTextField field = new JTextField("DATA INPUT SYSTEM");
		field.setFont(new Font("Dialog", 0, 40));
		field.setBorder(BorderFactory.createEmptyBorder());
		field.setEditable(false);
		headPanel.add(field);
		headPanel.add(new JLabel("1111"));
	}


	private void createMiddlePanel() {
		middlePanel = new JPanel();
		dbHelper = new DBHelper(SysParam.driverClass, SysParam.url, SysParam.username, SysParam.password);
		Connection connection = dbHelper.initDB();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int rowCount = -1;
		Object[][] rowData = null;
		String splits[] = SysParam.url.split("/", -1);
		String sql = "SELECT table_name FROM information_schema.TABLES WHERE table_schema = '" + splits[splits.length - 1] + "' AND table_type = 'base table'";
		System.out.println(sql);
		SysParam.selectdbName = splits[splits.length - 1];
		try {
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			resultSet.last();
			rowCount = resultSet.getRow();
			resultSet.first();
			System.out.println(rowCount);
			rowData = new Object[rowCount][1];
			for (int i = 0; i < rowCount; i++) {
				rowData[i][0] = resultSet.getString(1);
				resultSet.next();
				System.out.println(rowData[i][0]);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			info(e.getMessage());
		} finally {
			dbHelper.closeConn();
		}
		final String[] coN = {"数据表"};

		final JTable datatableTable = new JTable(new DefaultTableModel(rowData, coN){

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});

		datatableTable.setRowSelectionInterval(0,0);
		datatableTable.putClientProperty("terminateEditOnFocusLost", true);

		datatableTable.getTableHeader().setFont(new Font("Dialog", 0, 15));
		datatableTable.setRowHeight(20);
		datatableTable.setFont(new Font("Dialog", 0, 20));

		final JScrollPane scrollPane = new JScrollPane(datatableTable);
		scrollPane.setPreferredSize(new Dimension(100, 100));

		final JPanel fileChoosePane = new JPanel();

		JTextField ass = new JTextField("选择文件");
		final JFileChooser fileChoose = createFileChoose();

		fileChoosePane.add(ass);
		fileChoosePane.add(fileChoose);

		fileChoosePane.setVisible(false);

		datatableTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 1) {
					int columnIndex = datatableTable.columnAtPoint(e.getPoint()); //获取点击的列
					int rowIndex = datatableTable.rowAtPoint(e.getPoint()); //获取点击的行
					System.out.println(datatableTable.getValueAt(rowIndex, columnIndex));
					System.out.println(rowIndex + "," + columnIndex);
				}
			}
		});
		System.out.println(scrollPane);
		middlePanel.add(scrollPane);
		middlePanel.add(fileChoosePane);
		final JPanel radioButton = createRadioButton();
		radioButton.setVisible(false);
		middlePanel.add(radioButton);


		upButton = new JButton("上一步");
		downButton = new JButton("下一步");
		// 创建短信按钮
		valButton = new JButton("确认导入");
		// 创建 bottomPanel

		// 设置 bottomPanel 为垂直布局
		middlePanel .setLayout(new BoxLayout(middlePanel,BoxLayout.Y_AXIS ));
		// 创建包含两个按钮的 buttonPanel
		final JPanel buttonPanel = new JPanel();
		//buttonPanel.setBackground(Color.RED);
		// 设置 bottomPanel 为水平布局
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS ));


		//加入一个 glue, glue 会挤占两个按钮之间的空间
		buttonPanel.add(Box.createHorizontalStrut(15));
		buttonPanel.add(upButton);
		buttonPanel.add(Box.createHorizontalGlue());
		// 将查询按钮加入到 buttonPanel
		buttonPanel.add(downButton);
		buttonPanel.add(Box.createHorizontalGlue ());
		// 将退出按钮加入到 buttonPanel
		buttonPanel.add(valButton);
		// 加入一个 Strut，从而使 bottomPanel 和 middlePanel 上下之间留出距离
		middlePanel .add(Box.createVerticalStrut (1));
		buttonPanel.add(Box.createHorizontalGlue ());

		buttonPanel.add(Box.createHorizontalStrut(15));

		valButton.setEnabled(false);
		upButton.setEnabled(false);
		downButton.setEnabled(true);

		// 加入 buttonPanel
		middlePanel .add(buttonPanel);
		// 加入一个 Strut，从而使 bottomPanel 和底部之间留出距离

		upButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("stepCount " + stepCount);
				// TODO Auto-generated method stub
				if(stepCount == 1) {
					valButton.setEnabled(false);
				}
				if(stepCount == 2) {
					scrollPane.setVisible(true);
					fileChoosePane.setVisible(false);
					radioButton.setVisible(false);
					valButton.setEnabled(false);
					stepCount--;
					System.out.println("当前页码： " + stepCount);
				}
				if(stepCount == 3) {
					radioButton.setVisible(true);
					columnandFileIndex.setVisible(false);
					valButton.setEnabled(false);
					stepCount--;
					System.out.println("当前页码： " + stepCount);
				}

			}

		});
		downButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				System.out.println("stepCount " + stepCount);
				upButton.setEnabled(true);
				int ro = datatableTable.getSelectedRow() < 0 ? 0 : datatableTable.getSelectedRow();
				int co = datatableTable.getSelectedColumn() < 0 ? 0 : datatableTable.getSelectedColumn();
				if(stepCount == 1) {
					SysParam.selectTableName = datatableTable.getValueAt(ro, co).toString();
					info("数据库:" + SysParam.selectdbName + ", 数据表:" + SysParam.selectTableName);
					scrollPane.setVisible(false);
					fileChoosePane.setVisible(true);
					int i = fileChoose.showOpenDialog(null);
					valButton.setEnabled(false);
					if(i == JFileChooser.APPROVE_OPTION) {
						String path = fileChoose.getSelectedFile().getPath();
						info("已选择文件:" + path);
						SysParam.selectFilePath = path;
						stepCount++;
						scrollPane.setVisible(false);
						fileChoosePane.setVisible(false);
						radioButton.setVisible(true);
						System.out.println("当前页码： " + stepCount);
					} else if(i == JFileChooser.CANCEL_OPTION) {
						info("未选择文件");
						scrollPane.setVisible(true);
						fileChoosePane.setVisible(false);
					}
				} else if(stepCount == 2) {
					radioButton.setVisible(false);
					columnandFileIndex = createColumnandFileIndex();
					middlePanel.add(columnandFileIndex);
					valButton.setEnabled(true);
					columnandFileIndex.setVisible(true);
					stepCount++;
					System.out.println("当前页码： " + stepCount);
				}


			}

		});
		valButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int rowCount = columnandFileIndexTable.getRowCount();
				String[] newCol = new String[rowCount];
				boolean flag = false;
				if(SysParam.hasHead) {
					for (int i = 0; i < rowCount; i++) {
						if(columnandFileIndexTable.getValueAt(i,1) == null) {
							info("对应列不能为空");
							flag = true;
							break;
						}else {
							newCol[i] = columnandFileIndexTable.getValueAt(i,1).toString();
						}
					}
				}
				if(!flag) {
					info("开始导入");
					long start = System.currentTimeMillis();
					dbHelper = new DBHelper(SysParam.driverClass, SysParam.url, SysParam.username, SysParam.password);
					PreparedStatement preparedStatement;
					String encoder = "utf8";
					Connection connection = dbHelper.initDB();
					int executeUpdate = 0;
					int currentCount = 0;
					int batchCount = 30000;
					String sql = "load data local infile '' " + " ignore into table "+ SysParam.selectTableName + " character set "+encoder+ " fields terminated by ',' enclosed by ''";
					if(SysParam.hasHead) {
						CsvReader reader = null;
						try {
							connection.setAutoCommit(false);
							preparedStatement = connection.prepareStatement(sql);
							System.out.println(sql);
							reader = new CsvReader(new FileInputStream(new File(SysParam.selectFilePath)), ',',Charset.forName("UTF8"));
							reader.readHeaders();
							StringBuilder sb = new StringBuilder();
							InputStream is = null;
							while (reader.readRecord()) {
								currentCount++;
								for (int i = 0; i < newCol.length; i++) {
									sb.append(reader.get(newCol[i]) + ",");
								}
								sb.append("\n");
//								System.out.println(sb.toString());
								if(currentCount % batchCount == 0) {
									is = null;
									try {
										is = new ByteArrayInputStream(sb.toString().getBytes());
										((com.mysql.jdbc.Statement) preparedStatement).setLocalInfileInputStream(is);
										executeUpdate = executeUpdate + preparedStatement.executeUpdate();
										connection.commit();
										info("load count:" + executeUpdate);
										System.out.println("load count:" + executeUpdate);

										sb.setLength(0);
									} catch (Exception eee) {
										System.out.println(eee);
									} finally {
										if(is != null)
											is.close();
									}
								}
							}
							try {
								is = new ByteArrayInputStream(sb.toString().getBytes());
								((com.mysql.jdbc.Statement) preparedStatement).setLocalInfileInputStream(is);
								executeUpdate = executeUpdate + preparedStatement.executeUpdate();
								connection.commit();
								info("load count:" + executeUpdate);
								System.out.println("load count:" + executeUpdate);
								sb.setLength(0);
							} catch (Exception eee) {
								System.out.println(eee);
							} finally {
								if(is != null)
									is.close();
							}
						} catch (SQLException ea) {
							info(ea.getMessage());
						} catch (FileNotFoundException ea) {
							info(ea.getMessage());
						} catch (IOException ea){
							info(ea.getMessage());
						}finally {
							dbHelper.closeConn();
							reader.close();
						}
					} else {
						try {
							connection.setAutoCommit(false);
							preparedStatement = connection.prepareStatement(sql);
							InputStream is = new FileInputStream(new File(SysParam.selectFilePath));
							((com.mysql.jdbc.Statement) preparedStatement).setLocalInfileInputStream(is);
							executeUpdate = preparedStatement.executeUpdate();
//							preparedStatement.execute();
							connection.commit();
						} catch (SQLException ea) {
							info(ea.getMessage());
						} catch (FileNotFoundException ea) {
							info(ea.getMessage());
						} finally {
							dbHelper.closeConn();
						}
					}
					long end = System.currentTimeMillis();
					info("success(rows):" + executeUpdate);
					info("spend time(s):" + ((end - start) / 1000));
					rizhiArea.setText(rizhiSb.toString());
				}
			}
		});




	}

	public JPanel createRadioButton() {
		JPanel contentPane=new JPanel();
		JRadioButton randioButton1=new JRadioButton("有表头",true);
		JRadioButton randioButton2=new JRadioButton("无表头");
		contentPane.add(randioButton1);
		contentPane.add(randioButton2);
		ButtonGroup group=new ButtonGroup();
		group.add(randioButton1);
		group.add(randioButton2);
		randioButton1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				info("有表头");
				SysParam.hasHead = true;
			}
		});
		randioButton2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				info("无表头");
				SysParam.hasHead = false;
			}
		});
		return contentPane;
	}

	public JScrollPane createColumnandFileIndex() {
		String[] coN = {"表字段","文件头"};
		dbHelper = new DBHelper(SysParam.driverClass, SysParam.url, SysParam.username, SysParam.password);
		Connection connection = dbHelper.initDB();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int rowCount = -1;
		Object[][] rowData = null;
		String sql = "select COLUMN_NAME from information_schema.COLUMNS where table_name = '"+ SysParam.selectTableName +"' and table_schema = '"+SysParam.selectdbName+"'";
		System.out.println(sql);
		CsvReader reader = null;
		String[] headers = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			resultSet.last();
			rowCount = resultSet.getRow();
			resultSet.first();
			System.out.println(rowCount);
			rowData = new Object[rowCount][2];
			for (int i = 0; i < rowCount; i++) {
				rowData[i][0] = resultSet.getString(1);
				resultSet.next();
				System.out.println(rowData[i][0]);
			}
			if(SysParam.hasHead) {
				reader = new CsvReader(new FileInputStream(new File(SysParam.selectFilePath)), ',',Charset.forName("UTF8"));
				reader.readHeaders();
				headers = reader.getHeaders();
				reader.close();
				for (int i = 0; i < rowCount; i++) {
					if(i < headers.length) {
						rowData[i][1] = headers[i];
					}
					System.out.println(rowData[i][1]);
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			info(e.getMessage());
		} catch (FileNotFoundException e) {
			info(e.getMessage());
		} catch (IOException e) {
			info(e.getMessage());
		}
		finally {
			dbHelper.closeConn();
			if(reader != null) {
				reader.close();
			}
		}
		final JTable datatableTable = new JTable(new DefaultTableModel(rowData, coN){

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				if(column == 0 || !SysParam.hasHead)
					return false;
				return true;
			}
		});

		if(SysParam.hasHead) {
			JComboBox<String> stringJComboBox = new JComboBox<>(headers);
			DefaultCellEditor editor = new DefaultCellEditor(stringJComboBox);
			datatableTable.getColumnModel().getColumn(1).setCellEditor(editor);
		}

		datatableTable.putClientProperty("terminateEditOnFocusLost", true);

		datatableTable.getTableHeader().setFont(new Font("Dialog", 0, 15));
		datatableTable.setRowHeight(20);
		datatableTable.setFont(new Font("Dialog", 0, 20));

		JScrollPane scrollPane = new JScrollPane(datatableTable);
		scrollPane.setPreferredSize(new Dimension(20, 200));
		columnandFileIndexTable = datatableTable;
		return scrollPane;
	}

	public JFileChooser createFileChoose() {
		JFileChooser jFileChooser = new JFileChooser();
		FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		info(fileSystemView.getHomeDirectory());
		jFileChooser.setCurrentDirectory(fileSystemView.getHomeDirectory());
		jFileChooser.setDialogTitle("请选择文件...");
		jFileChooser.setApproveButtonText("确定");
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		FileFilter fileFilter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".csv") || f.getName().toLowerCase().endsWith(".txt");
			}

			@Override
			public String getDescription() {
				return "*.csv;*.txt";
			}
		};
		jFileChooser.addChoosableFileFilter(fileFilter);
		jFileChooser.setFileFilter(fileFilter);
		return jFileChooser;
	}


	public void info(Object obj) {
	    String i = "\n" + obj.toString();
		rizhiArea.append(i);
		rizhiSb.append(i);
		if(rizhiArea.getLineCount() > 10)
		    rizhiArea.setText("");
		rizhiArea.setCaretPosition(rizhiArea.getText().length());
		rizhiArea.paintImmediately(rizhiArea.getBounds());
		rizhiArea.setSelectionStart(rizhiArea.getText().length());
        JScrollBar bar = rizhiJScrollPane.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
    }


	private void createBottomPanel() {
		// 创建 middlePanel
		bottomPanel = new JPanel();
		rizhiArea = new JTextArea("日志打印窗口");
        rizhiArea.setLineWrap(true);
        rizhiArea.setWrapStyleWord(true);
		DefaultCaret caret = (DefaultCaret)rizhiArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		rizhiJScrollPane = new JScrollPane(rizhiArea);
		rizhiJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//rizhiJScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		bottomPanel.add(rizhiJScrollPane);
	}

}
