package edu.wofford.wordoff;

import java.util.*;
import java.util.List;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.Timer;

import java.net.*;


/**
* Anagrams GUI implements a GUI for the Anagrams game. Players are given a target
* word with a specified number of anagrams and prompted to enter
* anagrams of that word until all anagrams have been found.
* The timer will continue to decrease until it reaches 0, or until all anagrams
* have been entered. At that point, the game will end in either a
* loss, if not all anagrams were guessed, or a win, if all anagrams
* were discovered in time.
*
* <pre>{@code
* //Default Constructor
* List<String> listOfAnagrams = new ArrayList<String>();
* int difficulty = 5;
* AnagramsGUI window = new AnagramsGUI(listOfAnagrams, difficulty);
*
* //Constructor with Random instance
* Random random = new Random();
* List<String> listOfAnagrams = new ArrayList<String>();
* int difficulty = 5;
* AnagramsGUI window = new AnagramsGUI(random, listOfAnagrams, difficulty);
*
* //startTimer method used to start the countdown timer
* AnagramsGUI window = new AnagramsGUI(listOfAnagrams, difficulty);
* window.startTimer();
*
* //getStartTime method used to get the start time of the countdown timer, which is the difficulty number times ten.
* List<String> listOfAnagrams = new ArrayList<String>();
* int difficulty = 5;
* AnagramsGUI window = new AnagramsGUI(listOfAnagrams, difficulty);
* int startTime = window.getStartTime();
* //startTime contains 50, which is ten times the given difficulty of 5.
*
* //getCurrentTime method used to get the current time of the countdown timer.
* List<String> listOfAnagrams = new ArrayList<String>();
* int difficulty = 5;
* AnagramsGUI window = new AnagramsGUI(listOfAnagrams, difficulty);
* int currentTime = window.getCurrentTime();
* //currentTime contains 50
* window.startTimer();
* //...waiting ten seconds...
* currentTime = window.getCurrentTime();
* //currentTime contains 40
*
* //getButton method used to get the instance of JButton used inside the GUI
* AnagramsGUI window = new AnagramsGUI(listOfAnagrams, difficulty);
* JButton button = window.getButton();
*
* //getTextField method used to get the instance of JTextField used inside the GUI
* AnagramsGUI window = new AnagramsGUI(listOfAnagrams, difficulty);
* JTextField field = window.getTextField();
*
* //disableButtonAndTextField method used to disable the JButton and JTextField in the GUI
* AnagramsGUI window = new AnagramsGUI(listOfAnagrams, difficulty);
* window.disableButtonAndTextField();
*
* //isButtonAndTextFieldEnabled method used to check if the given JButton and JTextField are both enabled.
* AnagramsGUI window = new AnagramsGUI(listOfAnagrams, difficulty);
* JButton button = window.getButton();
* JTextField field = window.getTextField();
* boolean state = window.isButtonAndTextFieldEnabled(button, field);
* //state contains true
* window.disableButtonAndTextField();
* state = window.isButtonAndTextFieldEnabled(button, field);
* //state contains false
*
* //showLeaderboadDialog method used to display a leaderboard of the top five scores
* AnagramsGUI window = new AnagramsGUI(listOfAnagrams, difficulty);
* window.showLeaderboadDialog();
*
* //AnagramsGUI main used to start the game
* //Running with difficulty 4 and random seed 25.
* String[] args = new String[]{"4", "25"};
* AnagramsGUI.main(args);
*
* }</pre>
*/
public class AnagramsGUI extends JFrame implements ActionListener, TimerListener{
	private JTextField guess;
	// dictionary:
	//  Contains the anagram as the key and the JLabel as the value.
	private Map<String, JLabel> dictionary;
	private JLabel target;
	private JButton button;
	private TimerPanel timerPanel;
	private LeaderboardDialog leaderboardDialog;
	private int difficulty;
	private String selectedWord;

	/**
	* Action Performed on Button Click.
	*/
	public void actionPerformed(ActionEvent event) {
			String input = guess.getText().toLowerCase();
			if (dictionary.containsKey(input)) {
				// If guess was correct, set label to input and remove word from dictionary
				dictionary.get(input).setText(input);
				dictionary.remove(input);
			}
			//when the dictionary is empty, all anagrams have been guessed
			if(dictionary.size() == 0){
				//disable JButton if count is higher than the
				target.setBorder(new LineBorder(Color.GREEN));
				button.setEnabled(false);
				guess.setEnabled(false);
				timerPanel.stopTimer();
				showLeaderboadDialog();
			}
			//if guess is incorrect rest the textfield to empty
			guess.setText("");
	}

	/**
	* Implementation of the timerExpired method from the TimerListener interface.
	* When called the method disables the button and text field using
	* {@code disableButtonAndTextField}, then calls the
	* {@code showLeaderboadDialog()} method.
	*/
	public void timerExpired(){
		disableButtonAndTextField();
		showLeaderboadDialog();
	}

	/**
	* Default Constructor
	* Runs the {@code AnagramsGUI(Random random, List<String> listOfAnagrams)}
	* constructor with a new instance of Random with no seed.
	*
	* @param listOfAnagrams A list of anagrams to be used in the generation of the
	* TimerPanel and JLabel creation.
	* @param difficulty The number of anagrams to be found during the game
	*/
	public AnagramsGUI(List<String> listOfAnagrams, int difficulty) {
		this(new Random(), listOfAnagrams, difficulty);
	}


	/**
	* Constructor with Random Instance
	* Builds the GUI for the anagrams game.
	*
	* @param random An instance of Random to use for word generation.
	* @param listOfAnagrams A list of anagrams to be used in the generation of the
	* TimerPanel and JLabel creation.
	* @param difficulty The number of anagrams to be found during the game
	*/
	public AnagramsGUI(Random random, List<String> listOfAnagrams, int difficulty) {
		//set title and name of frame
		setTitle("WordOff");
		setName("WordOff");
		setSize(new Dimension(415, 550));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0, 1, 0, 20));
		mainPanel.setBorder(new EmptyBorder(20, 30, 10, 30));
		//set target word to find anagrams of
		//set constraints and make the gui look purty

		int randomIndex = random.nextInt(listOfAnagrams.size());
		selectedWord = listOfAnagrams.get(randomIndex);
		this.difficulty = difficulty;
		listOfAnagrams.remove(randomIndex);

		target = new JLabel(selectedWord);
		target.setName("target");
		target.setHorizontalAlignment(JLabel.CENTER);
		target.setBorder(new LineBorder(Color.RED));
		Dimension preferredSize = new Dimension(350, 40);
		mainPanel.add(target);
		dictionary = new HashMap<>();
		//dynamically create labels for the appropriate number of anagrams needed
		for(int i = 0; i < listOfAnagrams.size(); i++) {
			JLabel label = new JLabel("");
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setBorder(new LineBorder(Color.BLACK));
			label.setName("anagram" + Integer.toString(i));
			label.setPreferredSize(preferredSize);
			mainPanel.add(label);
			dictionary.put(listOfAnagrams.get(i),label);
		}

		JPanel guessPanel = new JPanel();
		guessPanel.setLayout(new GridLayout (1, 2, 30, 20));
		guessPanel.setBorder(new EmptyBorder(40, 30, 30, 30));

		//set the guess textfield
		guess = new JTextField();
		guess.setName("guess");
		guess.setPreferredSize(new Dimension(20, 30));


		guessPanel.add(guess);
		guess.requestFocus(true);

		//set the guess button and create actionlistener for button press
		button = new JButton("Guess");
		button.setName("button");
		button.setPreferredSize(new Dimension(10, 40));
		button.addActionListener(this);
		guess.addActionListener(this);
		guessPanel.add(button);

		int totalTime = 10 * listOfAnagrams.size();
		timerPanel = new TimerPanel(totalTime);
		timerPanel.addTimerListener(this);
		add(timerPanel, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);
		add(guessPanel, BorderLayout.SOUTH);
		pack();
	}

	/**
	* Starts the timerPanel countdown timer
	*/
	public void startTimer() {
		timerPanel.startTimer();
	}

	/**
	* Gets the starting time of the timer panel
	* @return An int value representing the start time of the timer panel in seconds.
	*/
	public int getStartTime() {
		return timerPanel.getStartTime();
	}

	/**
	* Gets the current time of the timer panel
	* @return An int value representing the current time of the timer panel in seconds.
	*/
	public int getCurrentTime() {
		return timerPanel.getCurrentTime();
	}

	/**
	* Returns the {@code JButton} object of the AnagramsGUI.
	* @return The {@code JButton} object intialized by the AnagramsGUI constructor
	*/
	public JButton getButton(){
		return this.button;
	}

	/**
	* Returns the {@code JTextField} object of the AnagramsGUI.
	* @return The {@code JTextField} object intialized by the AnagramsGUI constructor
	*/
	public JTextField getTextField(){
		return this.guess;
	}

	/**
	* Disables the JButton and JTextField in the GUI
	*/
	public void disableButtonAndTextField() {
		button.setEnabled(false);
		guess.setEnabled(false);
	}

	/**
	* Returns the current state of the given {@code JButton} and {@code JTextField}
	* objects.
	* @param jbutton JButton object to check to see if it is enabled
	* @param jtextfield JTextField object to check to see if it is enabled
	* @return returns true if both are enabled, returns false otherwise
	*/
	public boolean isButtonAndTextFieldEnabled(JButton jbutton, JTextField jtextfield) {
		return jbutton.isEnabled() && jtextfield.isEnabled();
	}

	/**
	* Creates a new instance of the {@code LeaderboardDialog} to display the leaderboard.
	*/
	public void showLeaderboadDialog() {
		leaderboardDialog = new LeaderboardDialog(this, selectedWord, difficulty, timerPanel.getCurrentTime());
	}

	public static void main(String[] args) {
	//check argument was passed
		if (args.length > 0) {
			//try to parse argument as an integer
			try {
				int difficulty = Integer.parseInt(args[0]);

				String targetWord;
				List<String> listOfAnagrams = new ArrayList<>();

				//if the argument is atleast 1, find a random word with difficulty number of anagrams
				//create a hashmap and make a new window for the word
				if (difficulty >= 1) {
					try {
						URL targetUrl = new URL("http://localhost:8080/wordoff/common/wordwithanagrams/" + (difficulty + 1));
						HttpURLConnection targetConn = (HttpURLConnection) targetUrl.openConnection();
						targetConn.setRequestMethod("GET");

						int status = targetConn.getResponseCode();
						if (status == 200) {
							StringBuffer targetContent = new StringBuffer();
							try (BufferedReader in = new BufferedReader(new InputStreamReader(targetConn.getInputStream()))) {
								String inputLine;
								while ((inputLine = in.readLine()) != null) {
									targetContent.append(inputLine);
								}
								in.close();
							} catch (Exception e) {
								System.err.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
           						e.printStackTrace();
							}

							targetWord = targetContent.toString();
							System.out.println("Target word: " + targetWord);

							URL anagramsUrl = new URL("http://localhost:8080/wordoff/common/anagrams/" + targetWord);
							HttpURLConnection anagramsConn = (HttpURLConnection) anagramsUrl.openConnection();

							status = anagramsConn.getResponseCode();
							if (status == 200) {
								StringBuffer anagramsContent = new StringBuffer();
								try (BufferedReader in = new BufferedReader(new InputStreamReader(anagramsConn.getInputStream()))) {
									String inputLine;
									while ((inputLine = in.readLine()) != null) {
										anagramsContent.append(inputLine);
									}
									in.close();
								} catch (Exception e) {
									System.err.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
           							e.printStackTrace();
								}

								String anagramsString = anagramsContent.toString();
								anagramsString = anagramsString.replace("[", "");
								anagramsString = anagramsString.replace("]", "");
								anagramsString = anagramsString.replace("\"", "");

								listOfAnagrams = new ArrayList<String>(Arrays.asList(anagramsString.split(",")));

								System.out.println("Anagrams list: " + listOfAnagrams);
							}

							anagramsConn.disconnect();
							targetConn.disconnect();
						}


						if(listOfAnagrams != null && listOfAnagrams.size() != 0) {
							AnagramsGUI window = new AnagramsGUI(listOfAnagrams, difficulty);
							window.setVisible(true);
							window.startTimer();
						}

					} catch (Exception e) {
						System.err.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
           				e.printStackTrace();
					}

			//catch exceptions if too large, too small, or invalid input
				}
			} catch (NumberFormatException | IndexOutOfBoundsException e){}
		}
	}
}
