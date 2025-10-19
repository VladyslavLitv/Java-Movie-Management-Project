package MovieFrame;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MainClass {
	public static void main(String[] args) 
	{
		JFrame frame = new JFrame("Movie Management");
		frame.setSize(1000, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		JTabbedPane tabs = new JTabbedPane();

        GenresFrame genrePanel = new GenresFrame();
        MoviesFrame moviePanel = new MoviesFrame();
        HallsFrame hallPanel = new HallsFrame();
        SessionsFrame sessionPanel = new SessionsFrame(); 
        BigSessionsFrame bigSessionsPanel = new BigSessionsFrame();

        tabs.addTab("Genres", genrePanel);
        tabs.addTab("Movies", moviePanel);
        tabs.addTab("Halls", hallPanel);
        tabs.addTab("Sessions", sessionPanel);
        tabs.addTab("Search big sessions", bigSessionsPanel);

        frame.add(tabs);
        frame.setVisible(true);
	} 
}
