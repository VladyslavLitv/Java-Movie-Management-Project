package MovieFrame;

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

public class GenresFrame extends JPanel{
	Connection conn = null;
	PreparedStatement state = null;
	ResultSet result = null;
	int id = -1;
	
	//Panel
	JPanel upPanel = new JPanel();
	JPanel midPanel = new JPanel();
	JPanel downPanel = new JPanel();
	
	//Label
	JLabel gNameL = new JLabel("Genre:");
	JLabel gDescriptionL = new JLabel("Description: ");
	
	//TF
	JTextField gNameTF = new JTextField();
	JTextField gDescriptionTF = new JTextField();
	
	//Buttons
	JButton addBTN=new JButton("Add");
	JButton deleteBTN=new JButton("Delete");
	JButton editBTN=new JButton("Edit");
	JButton searchBTN=new JButton("Search by description");
	JButton refreshBTN=new JButton("Refresh");
	
	//Table
	JTable table = new JTable();
	JScrollPane myScroll = new JScrollPane(table);
	
	
	public GenresFrame() 
	{
		this.setLayout(new GridLayout(3,1));
		
		//upPanel----------------------------------
		upPanel.setLayout(new GridLayout(2,2));
		
		upPanel.add(gNameL);
		upPanel.add(gNameTF);
		
		upPanel.add(gDescriptionL);
		upPanel.add(gDescriptionTF);
		
		this.add(upPanel);
		
		//midPanel----------------------------------
		midPanel.add(addBTN);
		midPanel.add(deleteBTN);
		midPanel.add(editBTN);
		midPanel.add(searchBTN);
		midPanel.add(refreshBTN);
				
		this.add(midPanel);
		
		addBTN.addActionListener(new AddAction());
		deleteBTN.addActionListener(new DeleteAction());
		editBTN.addActionListener(new EditAction());
		searchBTN.addActionListener(new SearchAction());
		refreshBTN.addActionListener(new RefreshAction());
		
		//downPanel-----------------------------------
		myScroll.setPreferredSize(new Dimension(650, 150));
		downPanel.add(myScroll);
		
		this.add(downPanel);
		table.addMouseListener(new MouseAction());
		
		refreshTable();
	}
	
	public void refreshTable() {
		conn = DBConnection.getConnection();
		try {
			state=conn.prepareStatement("select * from genre");
			result = state.executeQuery();
			table.setModel(new MovieModel(result));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void clearForms() {
		gNameTF.setText("");
		gDescriptionTF.setText("");
	}
	
	class AddAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			conn=DBConnection.getConnection();
			String sql="insert into genre (name, description) values (?,?)";
			try {
				state=conn.prepareStatement(sql);
				state.setString(1, gNameTF.getText());
				state.setString(2, gDescriptionTF.getText());
				state.execute();
				refreshTable();
				clearForms();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	class MouseAction implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			int row=table.getSelectedRow();
			id=Integer.parseInt(table.getValueAt(row, 0).toString());
			gNameTF.setText(table.getValueAt(row, 1).toString());
			gDescriptionTF.setText(table.getValueAt(row, 2).toString());
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
	
	class DeleteAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			conn=DBConnection.getConnection();
			String sql = "delete from genre where genre_id=?";
			try {
				state=conn.prepareStatement(sql);
				state.setInt(1, id);
				state.execute();
				refreshTable();
				clearForms();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	class EditAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			conn=DBConnection.getConnection();
			String sql = "update genre set name=?, description=? where genre_id=?";
			try {
				state=conn.prepareStatement(sql);
				state.setString(1, gNameTF.getText());
				state.setString(2, gDescriptionTF.getText());
				state.setInt(3, id);
				state.execute();
				
				refreshTable();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}
	
	class SearchAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			conn=DBConnection.getConnection();
			String sql = "select * from genre where lower(description) like ?";
			try {
				state=conn.prepareStatement(sql);
				String searchText = "%" + gDescriptionTF.getText().trim().toLowerCase() + "%";
				state.setString(1, searchText);
				result=state.executeQuery();
				table.setModel(new MovieModel(result));
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	class RefreshAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			refreshTable();
			clearForms();
		}
		
	}
}
