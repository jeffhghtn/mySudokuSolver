package ca.jeffhoughton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Main extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;

	// This 9x9 grid holds each of the text boxes used for input and output
	JTextField[][] gridOfFields = new JTextField[9][9];

	// A boolean to track if shift is down, for tabbing between text boxes
	boolean shiftDown = false;

	public static void main(String[] args) {
		new Main();
	}

	public Main(){
		// Set a vertical layout, so we are adding content in rows
		this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		this.setSize(320,380); // Set the size

		////////////////////////////////////////////////////
		//// Setup the text fields for input and output ////
		////////////////////////////////////////////////////
		for (int i = 0; i < 9; i++){
			JPanel panel = new JPanel(); // create row "i" of text fields
			panel.setPreferredSize(new Dimension(400,30)); // Set the size of the row
			for (int j = 0; j < 9; j++){
				JTextField numPicker = new JTextField(); // create a text field
				gridOfFields[i][j] = numPicker; // save it to our grid
				numPicker.setPreferredSize(new Dimension(25,25));
				panel.add(numPicker); // add the text field to our current row
				if (j==2 || j==5) { // add a space after the third and sixth column
					JLabel label = new JLabel(" ");
					label.setPreferredSize(new Dimension(5,10));
					panel.add(label);
				}
			}
			this.add(panel); // add the row of text fields to the window
			if (i==2 || i==5) { // add a space after the third and sixth row
				JPanel spacer = new JPanel();
				spacer.setPreferredSize(new Dimension(200,5));
				this.add(spacer);
			}
		}

		/////////////////////////////////////////////////
		//// Setup the buttons below the text fields ////
		/////////////////////////////////////////////////
		JPanel buttons = new JPanel();

		// Solve button
		JButton solve = new JButton("Solve");
		solve.addActionListener(this);
		solve.setActionCommand("solve");
		buttons.add(solve);
		// Clear button
		JButton clear = new JButton("Clear");
		clear.addActionListener(this);
		clear.setActionCommand("clear");
		buttons.add(clear);
		// Create button, makes a sudoku board
		JButton create = new JButton("Create");
		create.addActionListener(this);
		create.setActionCommand("create");
		buttons.add(create);
		//add the buttons
		this.add(buttons);

		this.setVisible(true); // Mark the window visible
	}

	/////////////////////////////////////
	//// Listen for a pressed button ////
	/////////////////////////////////////
	@Override public void actionPerformed(ActionEvent e) {
		// Whenever a button is pressed, clear the text field background colour
		// The background colour is marked red when there is an error
		for (int i = 0; i < 9; i++){
			for (int j = 0; j < 9; j++){
				gridOfFields[i][j].setBackground(Color.white);
			}
		}
		// if "Solve" button is pressed
		if (e.getActionCommand().equals("solve")){
			actionSolve(); // Call the solve button, which solves the board
		}
		// If "Clear" button is pressed
		else if (e.getActionCommand().equals("clear")){
			for (int i = 0; i < 9; i++){
				for (int j = 0; j < 9; j++){
					// clear all textfields on the board
					gridOfFields[i][j].setText("");
				}
			}
		}
		// Create a sudoku board
		else if (e.getActionCommand().equals("create")){
			// Clear it
			for (int i = 0; i < 9; i++){
				for (int j = 0; j < 9; j++){
					// clear all textfields on the board
					gridOfFields[i][j].setText("");
				}
			}
			// Solve it
			actionSolve(); // fill in a blank bored with finished moves
			// 55% change of clearing each square, creating an unfinished board
			Random r = new Random();
			for (int i = 0; i < 9; i++){
				for (int j = 0; j < 9; j++){
					int remove = r.nextInt(100);
					if (remove > 45) gridOfFields[i][j].setText("");
				}
			}
		}
	}

	//////////////////////////////
	//// Get inputs and solve ////
	//////////////////////////////
	public void actionSolve(){
		int[][] numGrid = new int[9][9]; // This grid will hold the input numbers
		boolean valid = false; // A variable to make sure all inputs are valid
		// loop through each input
		for (int i = 0; i < 9; i++){
			for (int j = 0; j < 9; j++){
				valid = false; // input must be marked true to go on
				String c = gridOfFields[i][j].getText(); // get text from field
				// blank spaces represented as zeros
				if (c.equals("") || c.equals(" ")) {	valid = true; c = "0"; }
				else { // 1-9 only other valid inputs
					for (int k = 1; k < 10; k++) {
						if (c.equals(k+"")) {
							valid = true; break;
						}
					}
				}

				if (!valid) { // input was not valid, mark that field red
					gridOfFields[i][j].setBackground(Color.red);
					break;
				} // otherwise, parse that input
				else numGrid[i][j] = Integer.parseInt(c);
			}
			if (!valid) break;
		}

		if (valid) { // If all inputs are valid...
			Solver mySolver = new Solver(numGrid); // solve the board
			// retrieve the board and any errors, and print it
			fillOutBoard(mySolver.getGrid(), mySolver.getError());
		}
	}

	///////////////////////////
	//// "print" the board ////
	///////////////////////////
	private void fillOutBoard(int[][] grid, ArrayList<Pair> error){
		if (grid[0][0] == 0) { // This checks if an unfinished board was returned
			// if an unfinished board was returned, mark the incorrect fields red
			for (int k = 0; k < error.size(); k++){
				gridOfFields[error.get(k).i][error.get(k).j].setBackground(Color.red);
			}
		} else { // Otherwise, fill out the text fields with their proper results
			for (int i = 0; i < 9; i++){
				for (int j = 0; j < 9; j++){
					gridOfFields[i][j].setText(grid[i][j]+"");
				}
			}
		}
	}
}
