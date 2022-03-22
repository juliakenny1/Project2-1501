//TO-DO Add necessary imports
import java.util.*;
import java.io.*;


public class AutoComplete{
	//initializing variables 
	private DLBNode root;
	private int size; 	
	
  public AutoComplete(String dictFile) throws java.io.IOException {
  //reading in dictionary from command line 
    Scanner fileScan = new Scanner(new FileInputStream(dictFile));
    while(fileScan.hasNextLine()){
      StringBuilder word = new StringBuilder(fileScan.nextLine());
      add(word);
    }
    fileScan.close();
  }

  /**
   * Part 1: add, increment score, and get score
   */

  //add word to the tree
  public void add(StringBuilder word){
  	root = addHelper(root, word, 0);  	
  }

	//method adapted from lab 5
  private DLBNode addHelper(DLBNode x, StringBuilder word, int pos){
        DLBNode result = x;
        if (x == null){ // if x is null
            result = new DLBNode(null, 0); // create a new node
            result.data = word.charAt(pos); 
            if(pos < word.length()-1)
             result.child = addHelper(result.child, word, ++pos);/* Recurse on the child DLBNode*/
            else result.isWord = true; //if it is a word then saying such. 
        } 
        
        else if(x.data == word.charAt(pos)) {
            if(pos < word.length()-1){
              result.child = addHelper(result.child, word, ++pos);/*Recurse on the child DLBNode*/
            } 
            else result.isWord = true;//if it is a word then saying such. 
        } 
        else {
          result.sibling = addHelper(result.sibling, word, pos);/*Recurse on the sibling DLBNode*/
        }
        return result;
  }



  //increment the score of word
  public void notifyWordSelected(StringBuilder word){
	DLBNode curr = root;
	curr = get(curr, word, 0); //get last node of the word
    curr.score++; // increase the score of that word at the last node 
  }
  
  //get the score of word
  public int getScore(StringBuilder word){
	DLBNode curr = root; 
	curr = get(curr, word, 0); // get last node of the word
	return curr.score; //returning the score associated 
  }
  
	//method adapted from lab 5
    private DLBNode get(DLBNode x, StringBuilder word, int pos) {
        DLBNode result = null;
        if(x != null){
          if(x.data == word.charAt(pos)){
            if(pos == word.length()-1){
              result = x;
            } 
            else {
              result = get(x.child, word, ++pos);/*TODO: Recurse on the child node*/
            }
          } 
          else {
            result = get(x.sibling, word, pos);/*TODO: Recurse on the next sibling node*/
         }
        }
        return result; //returning the last node of the word if it is there 
    }

 
  /**
   * Part 2: retrieve word suggestions in sorted order.
   */
 //TO-DO Implement this method
  //retrieve a sorted list of autocomplete words for word. The list should be sorted in descending order based on score.
  public ArrayList<Suggestion> retrieveWords(StringBuilder word){
    ArrayList<Suggestion> array = new ArrayList<Suggestion>(); //creating new arraylist of type suggestion
    StringBuilder s = new StringBuilder(); // keeps track of the prefix
    DLBNode curr = root;
    DLBNode end; //node for end of prefix 
    
    end = get(curr, word, 0); // gets node at end of prefix
    s.append(word); //append the original word to the stringbuilder
    
    if(end == null) return array;
    if(end.isWord){ //if prefix is also a word then add as a suggestion!
    	Suggestion suggestion = new Suggestion(s, end.score); // create new suggestion
    	array.add(suggestion); // add to array
    }
    collect(end.child, array, s); //add all the suggestions into an arraylist 
    Collections.sort(array); // sort the array in descending order 
    return array;
   } 
   //adapted from lab 5 from queue to arraylist 
	private void collect(DLBNode x, ArrayList<Suggestion> suggest, StringBuilder current) {
        if (x == null) return;
        DLBNode curr = x;
        while(curr != null){
          current.append(curr.data);
          if(curr.isWord){
          	StringBuilder newCurr = new StringBuilder(current); //create the new stringbuilder object to get passed into new suggestion 
            Suggestion y = new Suggestion(newCurr, curr.score); //creating new suggestion to get added to array
            suggest.add(y); //add to the arraylist
          }
          collect(curr.child, suggest, current); //recurse on child
          current.deleteCharAt(current.length()-1); //delete the last node that was added 
          curr = curr.sibling; //go down to the next sibling 
        }
    }
    
    // collections.sort();

  /**
   * Helper methods for debugging.
   */

  //Print the subtree after the start string
  public void printTree(String start){
    System.out.println("==================== START: DLB Tree Starting from "+ start + " ====================");
    DLBNode startNode = getNode(root, start, 0);
    if(startNode != null){
      printTree(startNode.child, 0);
    }
    System.out.println("==================== END: DLB Tree Starting from "+ start + " ====================");
  }

  //A helper method for printing the tree
  private void printTree(DLBNode DLBNode, int depth){
    if(DLBNode != null){
      for(int i=0; i<depth; i++){
        System.out.print(" ");
      }
      System.out.print(DLBNode.data);
      if(DLBNode.isWord){
        System.out.print(" *");
      }
        System.out.println(" (" + DLBNode.score + ")");
      printTree(DLBNode.child, depth+1);
      printTree(DLBNode.sibling, depth);
    }
  }

  //return a pointer to the DLBNode at the end of the start string. Called from printTree.
  private DLBNode getNode(DLBNode DLBNode, String start, int index){
    DLBNode result = DLBNode;
    if(DLBNode != null){
      if((index < start.length()-1) && (DLBNode.data.equals(start.charAt(index)))) {
          result = getNode(DLBNode.child, start, index+1);
      } else if((index == start.length()-1) && (DLBNode.data.equals(start.charAt(index)))) {
          result = DLBNode;
      } else {
          result = getNode(DLBNode.sibling, start, index);
      }
    }
    return result;
  }


  //A helper class to hold suggestions. Each suggestion is a (word, score) pair. 
  //This class should be Comparable to itself.
  public class Suggestion implements Comparable<Suggestion>{
  	// need a method which places the array in score order
  	public StringBuilder word; //word-score pair 
  	public int score;
  	
  	public Suggestion(StringBuilder word, int score){
  		this.word = word;
  		this.score = score;
  		
  	}
  	public int compareTo(Suggestion s){ //makes the class implement comparable 
  		Suggestion next = s; // making s = next 
  		if(this.score == next.score) return 0; // if equal return 0
  		if(this.score < next.score) return 1; // if < return 1
  		else return -1; // if > return -1
  		
  	}
  }

  //The DLBNode class.
  private class DLBNode{
    private Character data;
    private int score;
    private boolean isWord;
    private DLBNode sibling;
    private DLBNode child;

    private DLBNode(Character data, int score){
        this.data = data;
        this.score = score;
        isWord = false;
        sibling = child = null;
    }
  }
}
