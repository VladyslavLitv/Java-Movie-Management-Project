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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import MovieFrame.SessionsFrame.MovieIdConvertor;

public class BigSessionsFrame extends JPanel {
	Connection conn = null;
	PreparedStatement state = null;
	ResultSet result = null;
	int id = -1;
	
	//Panel
	JPanel upPanel = new JPanel();
	JPanel midPanel = new JPanel();
	JPanel downPanel = new JPanel();
	
	//Label
	JLabel bsTitleL = new JLabel("Movie Title:");
	JLabel bsMinCapacityL = new JLabel("Minimum hall capacity:");
	
	//TF
	JTextField bsMinCapacityTF = new JTextField();
	
	//ComboBox
	JComboBox<MovieIdConvertor> movieCombo = new JComboBox<MovieIdConvertor>();
	
	//Buttons
	JButton refreshBTN=new JButton("Refresh");
	JButton searchBTN=new JButton("Search");
	
	//Table
	JTable table = new JTable();
	JScrollPane myScroll = new JScrollPane(table);
	
	public BigSessionsFrame() 
	{
		this.setLayout(new GridLayout(3,1));
		
		//upPanel---------------------------------------
		upPanel.setLayout(new GridLayout(2,2));
		
		upPanel.add(bsTitleL);
		upPanel.add(movieCombo);
		
		upPanel.add(bsMinCapacityL);
		upPanel.add(bsMinCapacityTF);
		
		this.add(upPanel);
		
		//midPanel----------------------------------------
		midPanel.add(searchBTN);
		midPanel.add(refreshBTN);
		
		this.add(midPanel);
		
		searchBTN.addActionListener(new SearchAction());
		refreshBTN.addActionListener(new RefreshAction());
		
		//downPanel----------------------------------------
		myScroll.setPreferredSize(new Dimension(650, 150));
		downPanel.add(myScroll);
		
		this.add(downPanel);
		
		showMovies();
		refreshTable();
	}
	
	public class MovieIdConvertor{
		public int movie_id;
		public String movie_title;
		
		public MovieIdConvertor(int movie_id, String movie_title) {
			this.movie_id = movie_id;
			this.movie_title = movie_title;
		}
		
		public int getMovieId() {
			return movie_id;
		}
		
		public String toString() {
			return movie_title;
		}
	}
	
	public void showMovies() {
		conn = DBConnection.getConnection();
		String sql = "select movie_id, title from movie";
		try {
			state=conn.prepareStatement(sql);
			result = state.executeQuery();
			movieCombo.removeAllItems();
			while (result.next()) {
				int movie_id = result.getInt("movie_id");
				String title = result.getString("title");
				movieCombo.addItem(new MovieIdConvertor(movie_id,title));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void refreshTable() {
		conn=DBConnection.getConnection();
		try {
			state=conn.prepareStatement("select s.session_id, m.title, h.name as hall_name, h.capacity, s.session_time, s.price from sessions s join movie m on s.movie_id = m.movie_id join hall h on s.hall_id = h.hall_id");
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
		movieCombo.setSelectedIndex(0);
		bsMinCapacityTF.setText("");
	}
	
	class SearchAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			conn=DBConnection.getConnection();
			String sql = "select s.session_id, m.title, h.name as hall_name, h.capacity, s.session_time, s.price from sessions s join movie m on s.movie_id = m.movie_id join hall h on s.hall_id = h.hall_id where m.title = ? and h.capacity >= ?";
			try {
				state=conn.prepareStatement(sql);
				MovieIdConvertor selectedMovie = (MovieIdConvertor) movieCombo.getSelectedItem();
				if(selectedMovie == null) return;
				String movieTitle = selectedMovie.toString();
				state.setString(1, movieTitle);
				
				int capacity = Integer.parseInt(bsMinCapacityTF.getText().trim());
				state.setInt(2, capacity);
				
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
			showMovies();
			clearForms();
		}
		
	}
}
