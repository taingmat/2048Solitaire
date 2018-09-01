import java.util.Scanner;

public class Console2048Solitaire {

	public static void main(String[] args) {
		GameStatus game = new GameStatus(); 
		
		System.out.println("Welcome to 2048 Solitaire");
		System.out.println("How to play:");
		System.out.println("Each turn you can do one of three things");
		System.out.println("1: enter a pile number, to place the top of the draw stack onto that pile"); 
		System.out.println("2: enter \"d\" to discard the card from the top of the draw stack");
		System.out.println("3: enter \"u\" to undo the last card you played");
		System.out.println();
		
		Scanner input = new Scanner(System.in);
		while (!game.gameOver()) {
			game.playTurn(input);
		}
		input.close(); 
		
		System.out.println("GAME OVER");
		System.out.println("Final Score:" + game.score);
	}

}
