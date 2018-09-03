import java.util.LinkedList;
import java.util.*;

public class DrawPile{
	public List<Integer> cards; 
	public static final int PILEMAX = 4;
	public int frontIndex; 
	private int[] normalCards; 
	private int[] specialCards;
	private boolean hasSpecialCards; 
	
	//Creates a new draw pile using a int representing the number of cards
	//and a boolean for whether or not special cards should be used.
	//The cards in the normal power are powers of 2 
	//from 1 to the numNormal
	//Special cards are wild, bomb, and undo
	public DrawPile(int numNormal, boolean hasSpecialCards) {
		this.normalCards = new int[numNormal];
		for (int i = 0; i < numNormal; i++) {
			normalCards[i] = (int) Math.pow(2, i + 1); 
		}
		this.hasSpecialCards = hasSpecialCards; 
		if (hasSpecialCards) {
			this.specialCards = new int[] {-1, -3};
		}
		cards = new LinkedList<Integer>();
		//Fills cards with random cards
		for (int i = 0; i < PILEMAX; i++) {
			cards.add(generateRandomCard()); 
		}
	}	
	
	//Removes and returns the card from the front of the pile 
	//and adds a new card to the end of the pile
	public int drawCard() {
		int temp = cards.remove(frontIndex);
		cards.add(generateRandomCard()); 
		return temp;
	}
		
	//Returns a random card which is ranging from 2 to 2 ^ 6 
	//Available cards 2,4,8,16,32,64
	//Which is 2 to the power of 1, 2, 3, 4, 5, 6 
	public int generateRandomCard() {
		Random r = new Random(); 
		int randomNum = r.nextInt(100);
		int cardValue; 
		//Special card
		if (randomNum  == 1 && hasSpecialCards) {
			int cardIndex = r.nextInt(specialCards.length);
			cardValue = specialCards[cardIndex];			
		}
		else if (randomNum == 2 && hasSpecialCards) {
		  int cardIndex = r.nextInt(normalCards.length);
		  cardValue = -normalCards[cardIndex];
		}
		else {
			int cardIndex = r.nextInt(normalCards.length);
			cardValue = normalCards[cardIndex];
		}
		return cardValue;
	}
	
	//Returns the top of the draw pile
	public int peek() {
		return cards.get(frontIndex);
	}
	
	//Prints the next card in the draw pile starting from the front
	public void printNextTwo() {
		if (PILEMAX < 2) {
			throw new IllegalArgumentException(); 
		}
		String firstCard = renameIfSpecial(cards.get(0));
		String secondCard = renameIfSpecial(cards.get(1));
		
		System.out.println("Next Cards:" + firstCard + ", "  + secondCard);
	}
	
	//Prints the entire pile
	public void printPile() {
		System.out.print("Next Cards: ");
		for (int i = PILEMAX - 1; i >= 0; i--) {
			System.out.println(cards.get(i));
		}
	}
	
	//Renames special cards to their string names 
	private String renameIfSpecial(int card) {
		if (card == -1) {
			return "Wild"; 
		}
		else if (card == -3) {
			return "Bomb";
		}
		else if (card == -5) {
		  return "Arrow";
		}
		else {
			return "" + card; 
		}
	}
	
	//reverses an action that was played on the pile
	public void undo(int previousCard) {
		cards.add(0, previousCard);
	}
}
