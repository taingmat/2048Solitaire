import java.util.*;

public class GameStatus {
	public int score;
	public int multiplier;
	public int undos;
	public int discards; 
	public static final int boardSize = 4; 
	public LinkedList<Pile> board = new LinkedList<Pile>();
	public DrawPile drawPile = new DrawPile(6, true); 
	
	public LinkedList<Integer> cardHistory;
	public LinkedList<Integer> pileIndexHistory; 
	
	//References to the initial state of the game
	public final int standardDiscards = 2; 
	public final int goalScore = 2048; 

	//Initializes the game based on standard settings
	public GameStatus() {
		score = 0; 
		multiplier = 1;
		undos = 0;
		discards = standardDiscards; 
		cardHistory = new LinkedList<Integer>(); 
		pileIndexHistory = new LinkedList<Integer>();
		for (int i = 0; i < boardSize; i++) {
			board.add(new Pile());
		}
	}
	
	//Plays one turn of the game 
	//this can be an undo, discard, or playing a card
	public void playTurn(Scanner input) {
		//Keeps track of current draw pile
		int currentCard = drawPile.peek();
		boolean receivedValidInput = false; 
		printGame(); 
		while (!receivedValidInput) {
			System.out.println("Which pile would you like to place " + currentCard + " on");
			System.out.println("please enter \"d\" to discard or \"u\" to undo");
			//is a pile index
			if (input.hasNextInt()) {
				int selectedPile = selectPileIndex(input, currentCard);
				receivedValidInput = true;
				drawPile.drawCard(); 
				playCard(currentCard, selectedPile);	
			}
			//Is a String
			else {
				String answer = input.next(); 
				//Discard
				if (answer.equals("d")) {
					if (discards >= 1) { 
					discard();
					receivedValidInput = true;		
					}
					else {
						System.out.println("Sorry you're out of discards");
					}		
				}
				//Undo
				else if (answer.equals("u")) {
					undo();
					receivedValidInput = true;
				}
			}
		}
	}
	
	//Prints the Game into the console
	//Including: Score, Multiplier, Discards, Undos, DrawPile, and board
	private void printGame() {
		System.out.println("Score: " + score + ", Multiplier: " + multiplier);
		System.out.println("Discards: " + discards + ", Undos: " + undos);
		drawPile.printNextTwo();
		printPiles(); 	
	}
	
	//Prints the piles from the board
	private void printPiles() {
		for (int i = 0; i < board.size(); i++) {
			System.out.print("Pile" + (i + 1) + ": ");
			board.get(i).print(); 
		}
	}
	
	//Accepts a scanner and the current card
	//Prompts the user for a pile to place the current card on
	//Until they select a valid pile index
	private int selectPileIndex(Scanner input, int currentCard) {
		int selectedPile = input.nextInt() - 1; 
		while (selectedPile > boardSize || 
				selectedPile < 0 || 
				!board.get(selectedPile).isValidPile(currentCard)) {
			System.out.println("Please select a valid pile index");
			selectedPile = input.nextInt() - 1; 
		}
		return selectedPile; 
	}
	
	//Accepts a card to play and the index of a pile to play it on
	//Returns the score gained by playing the card
	public int playCard(int card, int pileIndex) {
			int initialUndoCount = board.get(pileIndex).undoCount;
			int scoreEarned = board.get(pileIndex).playCard(card) * multiplier;
			score = score + scoreEarned; 
			cardHistory.add(card); 
			pileIndexHistory.add(pileIndex);			
			//Undo played on a previous turn 
			if (board.get(pileIndex).undoCount < initialUndoCount) {
			  undos += (initialUndoCount - board.get(pileIndex).undoCount);
			}
			//undo played from hand
			if (card < 0 && !board.get(pileIndex).isEmpty() && board.get(pileIndex).peek() != card) {
			  undos++;
			}
			checkFor2048(pileIndex);
			return scoreEarned;
	}
	
	//Updates the gamestatus if the goalscore has been achieved
	//Multiplier is increased by 1
	//Discards are reset to the default
	//And the pile that the goalscore was achieved on is cleared
	private void checkFor2048(int pileIndex) {
		if (!getPile(pileIndex).isEmpty() && board.get(pileIndex).peek() == goalScore) {
		  discards = standardDiscards;
			board.get(pileIndex).cards.clear();
			multiplier = multiplier + 1; 
		}
	}
	
	//Returns a boolean of whether or not the game is completed
	//The game is over when all piles are full
	//And the current card from the top of the draw pile Cannot be played
	public boolean gameOver() {
		for (int i = 0; i < boardSize; i++) {
			Pile current = board.get(i);
			if (!current.isFull || current.peek() == drawPile.peek()) {
				return false; 
			}
		}
		System.out.println("GAME OVER");
		System.out.println("Final Score: " + score);
		return true;
	}
	
	//Undoes the previous action
	public void undo() {
		if (undos <= 0 || cardHistory.size() <= 0) {
			throw new IllegalArgumentException(); 
		}
		undos = undos -1; 
		drawPile.undo(cardHistory.removeLast()); 
		pileIndexHistory.removeLast(); 
		GameStatus newGame = playFromArray(new GameStatus(), cardHistory, pileIndexHistory); 
		changeGame(newGame);
	}
	
	//Accepts a Game Status
	//updates the current Game Status to match the new one
	private void changeGame(GameStatus newGame) {
		score = newGame.score; 
		multiplier = newGame.multiplier; 
		undos = newGame.undos; 
		board = newGame.board; 
		discards = newGame.discards;
		cardHistory = newGame.cardHistory;
		pileIndexHistory = newGame.pileIndexHistory; 
	}
	
	//Accepts an initialGame state, cardHistory, and pileIndexHIstory
	//Plays the game starting from the initial state using the card history
	//Returns the new GameStatus
	private GameStatus playFromArray(GameStatus game, LinkedList<Integer> cardHistory, LinkedList<Integer> pileIndexHistory) {
		for (int i = 0; i < cardHistory.size(); i++) {
			int card = cardHistory.get(i);
			int pileIndex = pileIndexHistory.get(i);			
			//0 is used for isDiscard 
			if (pileIndex == 0) {
				discards = discards + 1;
			}
			else {
				game.playCard(card, pileIndex);
			}
		}
		return game; 
	}
	
	//Removes and returns the top card of the DrawPile
	//and then Reduces the players discards by 1
	//Throws an IllegalArugmentException if the player has no discards remaining
	public int discard() {
		if (discards <= 0) {
			throw new IllegalArgumentException(); 
		}
		discards = discards - 1;
		int removedCard = drawPile.drawCard();
		cardHistory.add(removedCard);
		pileIndexHistory.add(-1);
		return removedCard; 
	}
	
	//Accepts an index and returns the pile from that index
	public Pile getPile(int index) {
		return board.get(index);
	}
}
