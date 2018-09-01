 import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class GameTester {

	public Pile pileFromArray(int[] base) {
		Pile temp = new Pile(); 
		for (int i = 0; i < base.length; i++) {
			temp.cards.add(base[i]);
		}
		return temp;
	}
	
	@Test
	//All piles in the board should start as empty
	void gameStartsEmpty() {
		GameStatus testGame = new GameStatus(); 
		for (int i = 0; i < 4; i++) {
			assertTrue(testGame.board.get(i).isEmpty());
		}
		assertEquals(testGame.score, 0);
	}
	
	@Test
	//Multipliers are applied for the second level of stacking 
	void basicMultiplier() {
		int[] base = new int[] {128, 64};
		Pile testPile = pileFromArray(base);
		int score = testPile.playCard(64);
		assertEquals(score, 640);
		assertEquals(testPile.peek(), 256);
	}
	
	@Test
	//Multipliers are applied for multiple levels of stacking
	void multiLayeredCompression() {
		int[] base = new int[] {128, 64, 32, 16, 8}; 
		Pile testPile = pileFromArray(base);
		int score = testPile.playCard(8);
		assertEquals(score, 2064);
		assertEquals(testPile.peek(), 256);
	}
	 
	@Test
	//Draw pile works and regenerates
	void testDraw() {
		DrawPile testDeck = new DrawPile(6, true); 
		for (int i = 0; i < 10; i++) {
			assertTrue(testDeck.drawCard() <= 64);
		}
	}
	
	@Test
	void noSpecialDraw() {
		DrawPile testDeck = new DrawPile(6, false); 
		for (int i = 0; i < 1000; i++) { 
			int currentCard = testDeck.drawCard(); 
			assertTrue(currentCard <= 64 && currentCard > 0);
		}
	}
	
	@Test 
	void specialDraw() {
	  DrawPile testDeck = new DrawPile(6, true);
	  Set<Integer> usedCards = new HashSet<Integer>();
	  for (int i = 0; i < 1000; i++) {
	    usedCards.add(testDeck.drawCard()); 
	  }
	  assertTrue(usedCards.contains(-1) || usedCards.contains(-3));
	}
	@Test
	void expandedDraw() {
		DrawPile testDeck = new DrawPile(10, true);
		for (int i = 0; i < 10; i++) {
			assertTrue(testDeck.drawCard() <= 1028);
		}
	}
	
	@Test
	//Stack 2 on top of 2 
	void basicStack() {
		Pile testPile = new Pile();
		assertEquals(0, testPile.playCard(2));
		assertEquals(4, testPile.playCard(2));
	}
	
	@Test 
	//Mimics card at the end of the pile
	void playWild() {
		GameStatus testGame = new GameStatus(); 
		testGame.playCard(64, 0);
		testGame.playCard(-1, 0);
		assertEquals(testGame.score, 128);
		assertEquals(testGame.getPile(0).peek(), 128);
	}
	
	@Test 
	//destroys the pile it's placed on
	void playBomb(){
		GameStatus testGame = new GameStatus(); 
		testGame.playCard(2, 0);
		testGame.playCard(4, 0);
		testGame.playCard(8, 0);
		testGame.playCard(64, 0);
		testGame.playCard(-3, 0);
		assertTrue(testGame.getPile(0).isEmpty());
		assertEquals(testGame.score, 0);
	}
	
	@Test 
	void playArrow() {
	  GameStatus testGame = new GameStatus();
	  testGame.playCard(4, 0);
	  testGame.playCard(2, 0);
	  testGame.playCard(8, 0);
	  testGame.playCard(-5, 0);
	  assertEquals(testGame.getPile(0).peek(), 2);
	  testGame.playCard(2, 0);
	  assertEquals(testGame.score, 20);
	  assertEquals(testGame.getPile(0).peek(), 8);
	}
	
	@Test 
	void wildOnEmpty() {
	  GameStatus testGame = new GameStatus(); 
	  testGame.playCard(-1, 0);	
	  testGame.playCard(64, 0);
	  assertEquals(testGame.score, 128);
	  assertEquals(testGame.getPile(0).peek(), 128);
	} 
	
	@Test
	void bombOnEmpty() {
	  GameStatus testGame = new GameStatus(); 
	  testGame.playCard(-3, 0);
	  assertTrue(testGame.getPile(0).isEmpty());
	  assertEquals(testGame.score, 0);
	}
	
	@Test 
	void ArrowOnEmpty() {
	  GameStatus testGame = new GameStatus(); 
	   testGame.playCard(-5, 0);
	   testGame.playCard(2, 0);
	}
	
	@Test 
	void WildOnFull() {  
	  GameStatus testGame = new GameStatus();
    for (int i = 0; i < testGame.getPile(0).pileMax; i++) {
      testGame.playCard((int) Math.pow(2, i + 1), 0);
    }
    assertEquals(testGame.getPile(0).peek(), 256);
    testGame.getPile(0).print();
    testGame.playCard(-1, 0);
    testGame.getPile(0).print();
    assertEquals(testGame.getPile(0).peek(), 2 * (int) Math.pow(2, testGame.getPile(0).pileMax));
    assertEquals(testGame.score, (int) 2 * Math.pow(2, testGame.getPile(0).pileMax));
	}
	
	@Test
	void BombOnFull() {
    GameStatus testGame = new GameStatus();
    for (int i = 0; i < testGame.getPile(0).pileMax; i++) {
      testGame.playCard((int) Math.pow(2, i + 1), 0);
    }	  
    testGame.playCard(-3, 0);
    assertTrue(testGame.getPile(0).isEmpty());
    assertEquals(testGame.score, 0);
	}
	
	@Test 
	void ArrowOnFull() {
    GameStatus testGame = new GameStatus();
    for (int i = 0; i < testGame.getPile(0).pileMax; i++) {
      testGame.playCard((int) Math.pow(2, i + 1), 0);
    }
    testGame.playCard(-5, 0);
    assertEquals(testGame.getPile(0).peek(), (int) Math.pow(2, testGame.getPile(0).pileMax - 1));
    assertEquals(testGame.score, 0);
	}
	
	@Test
	//Game status is updated when goal score is reached
	//Pile is cleared
	//Discards is set to standard
	//Multiplier is increased
	void achieveGoalScore() {
		GameStatus newGame = new GameStatus();
		newGame.discards = 0; 
		newGame.playCard(2, 0);
		newGame.playCard(1024, 0);
		newGame.playCard(1024, 0);
		
		assertTrue(newGame.getPile(0).isEmpty());
		assertEquals(newGame.discards, 2);
		assertEquals(newGame.multiplier, 2);
	}
	
	@Test 
	//Gameover occurs
	//Game should come to a halt no errors should occur
	void achieveGameOver() {
		GameStatus testGame = new GameStatus(); 
		assertFalse(testGame.gameOver());
		for (int i = 0; i < testGame.boardSize; i++) {
			for (int j = 0; j < testGame.getPile(i).pileMax; j++) {
				testGame.playCard((int) Math.pow(2, j + 1), i);
			}
		}
		assertTrue(testGame.gameOver());
	}
	
	@Test
	//base multiplier affects score
	void baseMultiplierIncreases() {
		GameStatus newGame = new GameStatus();
		newGame.discards = 0; 
		newGame.playCard(2, 0);
		newGame.playCard(1024, 0);
		newGame.playCard(1024, 0);
		assertEquals(newGame.score, 2048);
		
		assertTrue(newGame.getPile(0).isEmpty());
		assertEquals(newGame.discards, 2);
		assertEquals(newGame.multiplier, 2);
		
		newGame.playCard(64, 0);
		newGame.playCard(64, 0);
		assertEquals(newGame.score, 2304);
	}
	
	@Test
	//Play a card on an invalid pile
	//Should throw an IllegalArgumentException.
	void playOnFullPile() {
		GameStatus testGame = new GameStatus(); 
		for (int i = 0; i < testGame.getPile(0).pileMax; i++) {
			testGame.playCard((int) Math.pow(2, i + 1), 0);
		}
        try {
            testGame.playCard(2, 0);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // All ok -- expected result
        }
	}
	
	@Test
	void matchingFullPile() {
		GameStatus testGame = new GameStatus(); 
		for (int i = 0; i < testGame.getPile(0).pileMax - 1; i++) {
			testGame.playCard((int) Math.pow(2, i + 1), 0);
		}
    testGame.playCard(2, 0);
    testGame.playCard(2, 0);
	}
	
	@Test 
	//Reverts game to previous state
	void basicUndo() {
		GameStatus test = new GameStatus(); 
		test.playCard(32, 1);
		test.playCard(32, 1);
		test.playCard(64, 1);
		assertEquals(test.score, 192);
		test.undos = 1; 
		test.undo(); 
		assertEquals(test.getPile(1).peek(), 64);
		assertEquals(test.score, 64);
	}

  @Test
  void gainUndo() {
    GameStatus testGame = new GameStatus();
    assertEquals(testGame.undos, 0);
    testGame.playCard(-2, 0);
    assertEquals(testGame.getPile(0).undoCount, 1);
    testGame.playCard(2, 0);
    assertEquals(testGame.getPile(0).undoCount, 0);
    assertEquals(testGame.getPile(0).peek(), 4);
    assertEquals(testGame.undos, 1);
  }
  
  @Test 
  void immediatelyGainUndo() {
    GameStatus testGame = new GameStatus();
    assertEquals(testGame.undos, 0);
    testGame.playCard(2, 0);
    assertEquals(testGame.getPile(0).undoCount, 0);
    testGame.playCard(-2, 0);
    assertEquals(testGame.getPile(0).undoCount, 0);
    assertEquals(testGame.getPile(0).peek(), 4);
    assertEquals(testGame.undos, 1);    
  }
  
  @Test
  void stackTwoUndos() {
    GameStatus testGame = new GameStatus(); 
    testGame.playCard(-2, 0); 
    assertEquals(testGame.getPile(0).undoCount, 1);
    testGame.playCard(-2, 0);
    assertEquals(testGame.getPile(0).undoCount, 0);
    assertEquals(testGame.undos, 2);    
  }
	
	@Test 
	//Attempt to undo when there are none remaining
	//Should alert the user to make a different action
	void noUndos() {
		GameStatus testGame = new GameStatus(); 
		testGame.playCard(2, 0);
		try {
            testGame.undo();
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // All ok -- expected result
        }
	}
	
	@Test
	void undoStartOfGame() {
		GameStatus testGame = new GameStatus();
        testGame.undos = 1;
		try {
            testGame.undo();
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // All ok -- expected result
        }
	}
	
	@Test 
	void undoFirstTurn() {
		GameStatus test = new GameStatus(); 
		test.playCard(32, 1); 
		test.undos = 1; 
		test.undo(); 
		for (int i = 0; i < test.boardSize; i++) {
			assertTrue(test.board.get(i).isEmpty());
		}
	}
	
	@Test 
	void undoDiscard() {
		GameStatus testGame = new GameStatus();
		for (int i = 0; i < 1000; i++) {
			testGame.undos = 1; 
			int firstDraw = testGame.discard();
			testGame.undo();
			assertEquals(firstDraw, testGame.drawPile.peek());
		}
	}
	
	@Test
	//Discarding a card correctly updates discards
	//And also updates the draw pile
	void basicDiscard() {
		GameStatus testGame = new GameStatus();
		int nextCard = testGame.drawPile.cards.get(1);
		testGame.discard(); 
		assertEquals(testGame.discards, testGame.standardDiscards - 1);
		assertEquals(testGame.drawPile.peek(), nextCard);
		testGame.discard(); 
	}
	
	@Test 
	//Attempt a discard when there are none remaining
	//Should alert the user to make a different action
	void noDiscards() {
		GameStatus testGame = new GameStatus(); 
		while (testGame.discards > 0) {
			testGame.discard(); 
		}
        try {
            testGame.discard();
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // All ok -- expected result
        }
	}
}
