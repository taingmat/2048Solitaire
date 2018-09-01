import java.util.*;


public class Pile {
	LinkedList<Integer> cards; 
	int pileMax = 8; 
	boolean isFull; 
	int goalScore; 
	public int undoCount; 
	
	//Creates a new empty pile
	public Pile() {
		cards = new LinkedList<Integer>();
		goalScore = 2048; 
		isFull = false; 
		undoCount = 0; 
	}
	
	//Accepts an int and adds it to the end of the stack.
	//Throws an IllegalArgumentException if players try adding
	//a card to a full stack which it doesn't match the end of.
	public int playCard(int newCard) {
		//If the stack is full, or the card does not matches the end card
	  if (!isValidPile(newCard)) {
			throw new IllegalArgumentException(); 
		}
		int pointsGained = 0; 
		//Wild
		if (newCard == -1 && !isEmpty()) {
			cards.add(peek());
			pointsGained = compress();
		}
		//Bomb
		else if (newCard == -3) {
      clear(); 
    }
		//Arrow
		else if (newCard == -5 && !isEmpty()) {
		  remove();
		}
		else {
		  //is an undo card
		  if (newCard < 0) {
		    undoCount++;
		  }
			cards.add(newCard); 
			pointsGained = compress();
		}
		if (cards.size() >= pileMax) {
			isFull = true; 
		}
		return pointsGained; 
	}
	
	//Removes the card from the end of the pile and returns it's value 
	private int remove() {
		return cards.removeLast();
	}
	
	//Combines the last two cards of the stack if they are identical
	//Throws an IllegalArgumentException if they're are not two cards or the cards do not much 
	private int stack() {
		//Pile is too short to stack 
		if (cards.size() < 2) {
			throw new IllegalArgumentException(); 
		}
		int temp = remove(); 
		int temp2 = remove();
		
		if(temp2 == -1) {
		  temp2 = temp;
		}
		
    //Cards are not identical 
    if (Math.abs(temp) != Math.abs(temp2)) {
      throw new IllegalArgumentException(); 
    }
		if (temp < 0) {
		  undoCount--;
		}
		if (temp2 < 0) {
		  undoCount--;
		}
		//Combines cards at end of stack 
		int newCard = (Math.abs(temp) + Math.abs(temp2));
		cards.add(newCard);
		return newCard;
	}
	
	//Compresses the stack with the card beneath it 
	//returns the score to be added
	private int compress() {
		return compress(0, 1);
	}
	
	//Additional compressions 
	private int compress(int score, int bonusMultiplier) {
		if (cards.size() > 1) {
			int lastCard = cards.get(cards.size() - 1);
			int secondLastCard = cards.get(cards.size() - 2);
			
			//Effects of placed cards
			//Wild
			if(secondLastCard == -1) {
			  secondLastCard = lastCard;
			}
			if(lastCard == -1) {
			  lastCard = secondLastCard; 
			}
			
			//Arrow
			if(secondLastCard == -5) {
			  cards.remove();
			  cards.remove();
			}
			//If the stack is compressible keep going till it's not
			else if (Math.abs(lastCard) == Math.abs(secondLastCard)) {
				score = score + stack() * bonusMultiplier;
				return compress(score, bonusMultiplier + 1);
			}
		}
		return score;
	}
	
	//Prints the pile from beginning to end
	public void print() {
		for (int i = 0; i < cards.size(); i++) {
			System.out.printf("%-6s", "|" + cards.get(i) + "|");
		}
		System.out.println();
	}
	
	//Returns the value of the top of the pile
	//Throws an exception if the stack is empty
	public int peek() {
		if (isEmpty()) {
			throw new NoSuchElementException(); 
		}
		return cards.getLast(); 
	}
	
	//Returns a boolean of whether or not the pile is empty 
	public boolean isEmpty() {
			return cards.size() == 0; 
	}
	
	//Accepts a card
	//Returns whether or not that card can be played on this pile
	//It cannot be played if the pile is full and it does not match the end of the pile
	public boolean isValidPile(int newCard) {
		return !isFull|| 
		    Math.abs(newCard) == Math.abs(cards.getLast()) || 
		    newCard == -1 ||
		    newCard == -3 ||
		    newCard == -5;
	}
	
	//clears the pile
	public void clear() {
		cards = new LinkedList<Integer>();
	}
}
