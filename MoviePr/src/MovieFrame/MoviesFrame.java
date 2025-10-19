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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class MoviesFrame extends JPanel{
	Connection conn = null;
	PreparedStatement state = null;
	ResultSet result = null;
	int id = -1;
	
	//Panel
	JPanel upPanel = new JPanel();
	JPanel midPanel = new JPanel();
	JPanel downPanel = new JPanel();
	
	//Label
	JLabel mTitleL = new JLabel("Movie Title:");
	JLabel mDescriptionL = new JLabel("Description:");
	JLabel mDurationL = new JLabel("Duration (minutes):");
	JLabel mGenreL = new JLabel("Genre:");
	JLabel mRatingL = new JLabel("Rating 0-100:");
	
	//TF
	JTextField mTitleTF = new JTextField();
	JTextField mDescriptionTF = new JTextField();
	JTextField mDurationTF = new JTextField();
	JTextField mRatingTF = new JTextField();
	
	//ComboBox
	JComboBox<GenreIdConvertor> genreCombo = new JComboBox<GenreIdConvertor>();
	
	//Buttons
	JButton addBTN=new JButton("Add");
	JButton deleteBTN=new JButton("Delete");
	JButton editBTN=new JButton("Edit");
	JButton searchBTN=new JButton("Search by rating");
	JButton refreshBTN=new JButton("Refresh");
	
	//Table
	JTable table = new JTable();
	JScrollPane myScroll = new JScrollPane(table);
	
	public MoviesFrame() 
	{
		this.setLayout(new GridLayout(3,1));
		
		//upPanel----------------------------------
		upPanel.setLayout(new GridLayout(5,2));
		
		upPanel.add(mTitleL);
		upPanel.add(mTitleTF);
		
		upPanel.add(mDescriptionL);
		upPanel.add(mDescriptionTF);
		
		upPanel.add(mDurationL);
		upPanel.add(mDurationTF);
		
		upPanel.add(mGenreL);
		upPanel.add(genreCombo);
		
		upPanel.add(mRatingL);
		upPanel.add(mRatingTF);
		
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
		
		showGenres();
		refreshTable();
	}
	
	public class GenreIdConvertor 
	{
		public int id;
		public String name;
		
		public GenreIdConvertor(int id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public int getId() {
			return id;
		}
		
		public String toString() {
			return name;
		}
	}
	
	public void showGenres() {
		conn = DBConnection.getConnection();
		try {
			String sql = "select genre_id, name from genre";
			state = conn.prepareStatement(sql);
			result = state.executeQuery();
			genreCombo.removeAllItems();
			while (result.next()) {
				int id = result.getInt("genre_id");
				String name = result.getString("name");
				genreCombo.addItem(new GenreIdConvertor(id,name));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void refreshTable() {
		conn=DBConnection.getConnection();
		try {
			state=conn.prepareStatement("select * from movie");
			result=state.executeQuery();
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
		mTitleTF.setText("");
		mDescriptionTF.setText("");
		mDurationTF.setText("");
		mRatingTF.setText("");
	}
	
	class AddAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			conn=DBConnection.getConnection();
			String sql = "insert into movie (title, description, duration, genre_id, rating) values (?,?,?,?,?)";
			try {
				state=conn.prepareStatement(sql);
				state.setString(1, mTitleTF.getText());
				state.setString(2, mDescriptionTF.getText());
				state.setInt(3, Integer.parseInt(mDurationTF.getText()));
				GenreIdConvertor selectedGenre = (GenreIdConvertor) genreCombo.getSelectedItem();
				if (selectedGenre == null) return;
				int genreId = selectedGenre.getId();
				state.setInt(4, genreId);
				state.setInt(5, Integer.parseInt(mRatingTF.getText()));
				state.execute();
				refreshTable();
				clearForms();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	class DeleteAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String sql = "delete from movie where movie_id = ?";
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
			String sql = "update movie set title=?, description=?, duration=?, genre_id=?,rating=? where movie_id = ?";
			try {
				state=conn.prepareStatement(sql);
				state.setString(1, mTitleTF.getText());
				state.setString(2, mDescriptionTF.getText());
				state.setInt(3, Integer.parseInt(mDurationTF.getText()));
				GenreIdConvertor selectedGenre = (GenreIdConvertor) genreCombo.getSelectedItem();
				if (selectedGenre == null) return;
				int genreId = selectedGenre.getId();
				state.setInt(4, genreId);
				state.setInt(5, Integer.parseInt(mRatingTF.getText()));
				state.setInt(6, id);
				state.execute();
				refreshTable();
				clearForms();
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
			String sql = "select * from movie where rating = ?";
			try {
				state=conn.prepareStatement(sql);
				state.setInt(1, Integer.parseInt(mRatingTF.getText().trim()));
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
			showGenres();
			clearForms();
		}
		
	}
	
	class MouseAction implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			int row = table.getSelectedRow();
			id=Integer.parseInt(table.getValueAt(row, 0).toString());
			mTitleTF.setText(table.getValueAt(row, 1).toString());
			mDescriptionTF.setText(table.getValueAt(row, 2).toString());
			mDurationTF.setText(table.getValueAt(row, 3).toString());
			GenreIdConvertor item;
			for(int i = 0; i < genreCombo.getItemCount(); i++) {
				item = genreCombo.getItemAt(i);
				if(item.getId() == Integer.parseInt(table.getValueAt(row, 4).toString())) {
					genreCombo.setSelectedIndex(i);
					break;
				}
			}
			mRatingTF.setText(table.getValueAt(row, 5).toString());
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
}
