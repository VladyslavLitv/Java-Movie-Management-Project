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
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

import MovieFrame.MoviesFrame.AddAction;
import MovieFrame.MoviesFrame.DeleteAction;
import MovieFrame.MoviesFrame.EditAction;
import MovieFrame.MoviesFrame.GenreIdConvertor;
import MovieFrame.MoviesFrame.MouseAction;
import MovieFrame.MoviesFrame.RefreshAction;
import MovieFrame.MoviesFrame.SearchAction;

public class SessionsFrame extends JPanel {
	Connection conn = null;
	PreparedStatement state = null;
	ResultSet result = null;
	int id = -1;
	
	//Panel
	JPanel upPanel = new JPanel();
	JPanel midPanel = new JPanel();
	JPanel downPanel = new JPanel();
	
	//Label
	JLabel sMovieL = new JLabel("Movie title:");
	JLabel sHallL = new JLabel("Hall name:");
	JLabel sTimeL = new JLabel("Session start at:");
	JLabel sPriceL = new JLabel("Price:");
	
	//JSpinner
	JSpinner sTimeSpinner;
	JSpinner.DateEditor dateEditor;
	
	//TF
	JTextField sPriceTF = new JTextField();
	
	//ComboBox
	JComboBox<MovieIdConvertor> movieCombo = new JComboBox<MovieIdConvertor>();
	JComboBox<HallIdConvertor> hallCombo = new JComboBox<HallIdConvertor>();
	
	//Buttons
	JButton addBTN=new JButton("Add");
	JButton deleteBTN=new JButton("Delete");
	JButton editBTN=new JButton("Edit");
	JButton searchBTN=new JButton("Search by hall");
	JButton refreshBTN=new JButton("Refresh");
		
	//Table
	JTable table = new JTable();
	JScrollPane myScroll = new JScrollPane(table);
	
	public SessionsFrame() 
	{
		this.setLayout(new GridLayout(3,1));
		
		//upPanel
		upPanel.setLayout(new GridLayout(4,2));
		
		upPanel.add(sMovieL);
		upPanel.add(movieCombo);
		
		upPanel.add(sHallL);
		upPanel.add(hallCombo);
		
		upPanel.add(sTimeL);
		SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
		sTimeSpinner = new JSpinner(dateModel);
		dateEditor = new JSpinner.DateEditor(sTimeSpinner, "yyyy-MM-dd HH:mm");
		sTimeSpinner.setEditor(dateEditor);
		upPanel.add(sTimeSpinner);
		
		upPanel.add(sPriceL);
		upPanel.add(sPriceTF);
		
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
		
		showMovies();
		showHalls();
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
	
	public class HallIdConvertor{
		public int hall_id;
		public String hall_name;
		
		public HallIdConvertor(int hall_id, String hall_name) {
			this.hall_id = hall_id;
			this.hall_name = hall_name;
		}
		
		public int getHallId() {
			return hall_id;
		}
		
		public String toString() {
			return hall_name;
		}
	}
	
	public void showHalls() {
		conn = DBConnection.getConnection();
		String sql = "select hall_id, name from hall";
		try {
			state=conn.prepareStatement(sql);
			result = state.executeQuery();
			hallCombo.removeAllItems();
			while(result.next()) {
				int hall_id = result.getInt("hall_id");
				String name = result.getString("name");
				hallCombo.addItem(new HallIdConvertor(hall_id, name));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void refreshTable() 
	{
		conn = DBConnection.getConnection();
		try {
			state = conn.prepareStatement("select * from sessions");
			result  = state.executeQuery();
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
		sPriceTF.setText("");
		sTimeSpinner.setValue(new Date());
		movieCombo.setSelectedIndex(0);
		hallCombo.setSelectedIndex(0);
	}
	
	class AddAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			conn = DBConnection.getConnection();
			String sql = "insert into sessions (movie_id, hall_id, session_time, price) values (?,?,?,?)";
			try {
				state = conn.prepareStatement(sql);
				MovieIdConvertor selectedMovie = (MovieIdConvertor) movieCombo.getSelectedItem();
				if (selectedMovie == null) return;
				int movieId = selectedMovie.getMovieId();
				state.setInt(1, movieId);
				HallIdConvertor selectedHall = (HallIdConvertor) hallCombo.getSelectedItem();
				if (selectedHall == null) return;
				int hallId = selectedHall.getHallId();
				state.setInt(2, hallId);
				Date sessionDate = (Date) sTimeSpinner.getValue();
				java.sql.Timestamp timestamp = new java.sql.Timestamp(sessionDate.getTime());
				state.setTimestamp(3, timestamp);
				state.setFloat(4, Float.parseFloat(sPriceTF.getText()));
				state.execute();
				
				refreshTable();
				clearForms();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}		
	}
	
	class DeleteAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String sql = "delete from sessions where session_id = ?";
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
			String sql = "update sessions set movie_id = ?, hall_id=?, session_time=?, price=? where session_id = ?";
			try {
				state=conn.prepareStatement(sql);
				MovieIdConvertor selectedMovie = (MovieIdConvertor) movieCombo.getSelectedItem();
				if (selectedMovie == null) return;
				int movieId = selectedMovie.getMovieId();
				state.setInt(1, movieId);
				HallIdConvertor selectedHall = (HallIdConvertor) hallCombo.getSelectedItem();
				if (selectedHall == null) return;
				int hallId = selectedHall.getHallId();
				state.setInt(2, hallId);
				Date sessionDate = (Date) sTimeSpinner.getValue();
				java.sql.Timestamp timestamp = new java.sql.Timestamp(sessionDate.getTime());
				state.setTimestamp(3, timestamp);
				state.setFloat(4, Float.parseFloat(sPriceTF.getText()));
				state.setInt(5, id);
				state.execute();
				
				refreshTable();
				clearForms();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}	
	}
	
	class SearchAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			conn=DBConnection.getConnection();
			String sql = "select * from sessions where hall_id = ?";
			try {
				state=conn.prepareStatement(sql);
				HallIdConvertor selectedHall = (HallIdConvertor) hallCombo.getSelectedItem();
				if (selectedHall == null) return;
				int hallId = selectedHall.getHallId();
				state.setInt(1, hallId);
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
			showHalls();
			clearForms();
		}
		
	}
	
	class MouseAction implements MouseListener
	{

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			int row = table.getSelectedRow();
			
			id=Integer.parseInt(table.getValueAt(row, 0).toString());
			
			MovieIdConvertor item;
			for(int i = 0; i < movieCombo.getItemCount(); i++) {
				item = movieCombo.getItemAt(i);
				if(item.getMovieId() == Integer.parseInt(table.getValueAt(row, 1).toString())) {
					movieCombo.setSelectedIndex(i);
					break;
				}
			}
			
			HallIdConvertor item1;
			for(int i = 0; i < hallCombo.getItemCount(); i++) {
				item1 = hallCombo.getItemAt(i);
				if(item1.getHallId() == Integer.parseInt(table.getValueAt(row, 2).toString())) {
					hallCombo.setSelectedIndex(i);
					break;
				}
			}
			
			try {
	            String sessionTimeStr = table.getValueAt(row, 3).toString();
	            java.util.Date parsedDate = java.sql.Timestamp.valueOf(sessionTimeStr);
	            sTimeSpinner.setValue(parsedDate);
	        } catch (IllegalArgumentException ex) {
	            ex.printStackTrace();
	        }
			
			sPriceTF.setText(table.getValueAt(row, 4).toString());
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
