//Daven Giftian Tejalaksana
//Sunday, 25 April 2021
//CSE 143
//Instructor: Stuart Reges
//TA: Andrew Cheng
//Assignment #4
//This program keeps track of the state of a cheating game of hangman,
//including the word pattern, user's guesses, remaining wrong guesses, and available words
//as the user guesses a letter.

import java.util.*;

public class HangmanManager {
   private Set<String> setOfWords; //Set of available words for the game
   private int maxGuesses; //Max number of wrong guesses player is allowed to make
   private Set<Character> userGuesses; //Current set of letters guessed by the user
   private String patternDisplay; //Displays the pattern of the word user needs to guess
   
   //pre: length should not be less than 1 and max should not be less than 0. 
      //throws IllegalArgumentException if not.
   //post: Constructor then uses parameters (dictionary, word length, max wrong guesses)
      //to initialize the state of the game.
      //Assume that dictionary is legal (collection of entirely lowercase nonempty strings).
      //Set of words should initially contain all words of given length from the dictionary.
      //Set of words should eliminate any duplicates.
      //Creates an initial pattern display with pattern "- -"(pattern based on word length).
   public HangmanManager(Collection<String> dictionary, int length, int max) {
      if (length < 1 || max < 0) {
         throw new IllegalArgumentException("length: " + length + ", max: " + max); 
      }
      
      setOfWords = new TreeSet<>(); //initializes words considered by the manager.
      Iterator<String> itr = dictionary.iterator();
      while (itr.hasNext()) {
         String next = itr.next();
         if (next.length() == length) {
            setOfWords.add(next);
         }
      }
      userGuesses = new TreeSet<>(); //initializes userGuesses
      maxGuesses = max; //initializes maxGuesses to max
      
      patternDisplay = "-"; //creates initial pattern of the game
      for (int i = 1; i < length; i++) {
         patternDisplay += " -";
      }
   }
   
   //post: gets access to the current set of words being considered by hangman manager.
   public Set<String> words() {
      return setOfWords;
   }
   
   //post: returns the amount of guesses the player has left (wrong guesses allowed).
   public int guessesLeft() {
      return maxGuesses;
   }
   
   //post: returns the current set of letters that have been guessed by the user.
   public Set<Character> guesses() {
      return userGuesses;
   }
   
   //pre: set of words should not be empty (throws IllegalStateException if not).
   //post: Returns the current pattern to be displayed for the hangman game.
      //Takes into account guesses that have been made.
      //Letters that have not been guessed are displayed as a dash.
      //There are spaces separating the letters with no leading or trailing spaces.
   public String pattern() {
      if (setOfWords.isEmpty()) {
         throw new IllegalStateException("Set of words is empty.");
      }
      return patternDisplay;
   }
   
   //pre: number of guesses should be at least 1 and set of words should not be empty.
      //throws IllegalStateException if not.
   //pre: If previous exception was not thrown & character being guessed was guessed previously,
      //throws IllegalArgumentException.
   //post: records the next guess made by the user. (assume guess passed are lowercase letters).
      //Using the guess parameter, it should decide what set of words to use going forward.
      //Then, it returns a new display pattern based on guess (returns pattern with most words).
      //Returns the number of occurences of the guessed letter in the new pattern.
      //Appropriately updates the number of guesses left + adds guess letter to userGuesses.
   public int record(char guess) {
      if (maxGuesses < 1 || setOfWords.isEmpty()) {
         throw new IllegalStateException("No available words left OR no more guesses left.");
      } else if (userGuesses.contains(guess)) {
         throw new IllegalArgumentException("The character has been guessed previously.");
      }
      userGuesses.add(guess);
      Map<String, Set<String>> wordList = new TreeMap<>(); //Map recording patterns and words.
      String newPattern = "";
      for (String word: setOfWords) {
         newPattern = createPattern(word, guess); //Returns the created pattern from given word
         if (!wordList.containsKey(newPattern)) {
            wordList.put(newPattern, new TreeSet<>());
         }
         wordList.get(newPattern).add(word);
         newPattern = "";
      }
      patternDisplay = iteratePatterns(wordList); //Returns pattern with most words
      return occurences(guess); //Return occurences of user's guess.
   }
   
   //A sub-method of the record method.
   //Post: It returns the number of occurences of guessed letter in the new pattern.
      //If the occurence of the guess is 0, it decreases number of guesses user can make.
   private int occurences(char guess) {
      int count = 0;
      for (int i = 0; i < patternDisplay.length(); i += 2) {
         if (patternDisplay.charAt(i) == guess) {
            count++;
         }
      }
      if (count == 0) {
         maxGuesses--;
      }
      return count;
   }
   
   //A sub-method of the record method.
   //Post: It takes in the word and the letter user guessed.
      //Then, it returns a new hangman pattern created between the word and letter.
   private String createPattern(String word, char guess) {
      String createdPattern = "";
      if (patternDisplay.charAt(0) == '-' && word.charAt(0) == guess) {
         createdPattern += guess;
      } else {
         createdPattern += patternDisplay.charAt(0);
      }
      for (int i = 1; i < word.length(); i++) {
         if (patternDisplay.charAt(i * 2) == '-' && word.charAt(i) == guess) {
            createdPattern += " " + guess;
         } else {
            createdPattern += " " + patternDisplay.substring(i * 2, i * 2 + 1);
         }
      }
      return createdPattern;  
   }
   
   //A sub-method of the record method.
   //Post: Iterates over the patterns and returns the pattern with the most amount of words.
   private String iteratePatterns(Map<String, Set<String>> wordList) {
      String iteratedPattern = "";
      Set<String> allPatterns = wordList.keySet();
      Iterator<String> itr = allPatterns.iterator();
      int max = 0;
      while (itr.hasNext()) {
         String next = itr.next();
         int sizeNum = wordList.get(next).size();
         if (sizeNum > max) {
            iteratedPattern = next;
            max = sizeNum;
            setOfWords = wordList.get(iteratedPattern);
         }
      }
      return iteratedPattern;
   }
}