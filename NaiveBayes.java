import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;


public class NaiveBayes {

	private HashMap<String, Integer> hamWordCounts = new HashMap<String, Integer>();
	private HashMap<String, Integer> spamWordCounts = new HashMap<String, Integer>();
	private HashSet<String> allWords = new HashSet<String>();
	private HashMap<String, Double> hamWordProbabilities = new HashMap<String, Double>();
	private HashMap<String, Double> spamWordProbabilities = new HashMap<String, Double>();
	private double probabilityHam; 
	private double probabilitySpam; 


    /*
     * Train your Naive Bayes Classifier based on the given training
     * ham and spam emails.
     *
     * Params:
     *      hams - email files labeled as 'ham'
     *      spams - email files labeled as 'spam'
     */
  	//Iterate over the labelled spam emails and, for each word w in 
	//the entire training set, count how many of the spam emails contain w.
    public void train(File[] hams, File[] spams) throws IOException {
  
    	//goes through every email file in hams 
    	for (File email: hams) { 
    		//gets all distinct words from one email
    		HashSet<String> wordsFromEmail =  tokenSet(email);
    		//Then goes through every distinct word in that set
    		for (String word: wordsFromEmail) {
    			//adds the word to the all words set. 
    			allWords.add(word); 
    			if (!hamWordCounts.containsKey(word)) {
    				//adds the word to the hamWordCounts and sets the count as 1 if not already in it. 
    				hamWordCounts.put(word, 1 );
    			}else {
    				//if word is already in hamWordCounts, then it increments the counter of that word by 1.
    				hamWordCounts.put(word, hamWordCounts.get(word) + 1) ;  
    			}
    		}
    	}
    	
    	//goes through every email file in spams
    	for (File email: spams) { 
    		//gets all distinct words from one email
    		HashSet<String> wordsFromEmail =  tokenSet(email);
    		//Then goes through every distinct word in that set
    		for (String word: wordsFromEmail) {
    			//adds the word to the all words set. 
    			allWords.add(word);
    			if (!spamWordCounts.containsKey(word)) {
    				//adds the word to the spamWordCounts and sets the count as 1 if not already in it. 
    				spamWordCounts.put(word, 1 );
    			} else {
    				//if word is already in spamWordCounts, then it increments the counter of that word by 1.
    				spamWordCounts.put(word, spamWordCounts.get(word) +1) ;  
    			}
    		}
    	}
    	
    	
    	probabilityHam  = hams.length / ((double)(hams.length+ spams.length)); 
    	probabilitySpam  = spams.length / ((double)(hams.length+ spams.length)); 
    	
    	//now goes through each word in the set of All words
    	for (String word: allWords  ) { 
    		//if the word is in hamWords
    		if (hamWordCounts.containsKey(word)) {
    			//then you add 1 to the value of the count in the hamWord counts as the numerator for the probability 
    			//the denominator is just num of ham emails + 2
    			hamWordProbabilities.put(word, ((double) hamWordCounts.get(word) + 1) / ((double)(hams.length + 2)));
    			
    		} else {
    			//otherwise its just the same but you don't execute hamWordsCounts.get(word)
    			//the denominator is just num of ham emails + 2
    			hamWordProbabilities.put(word, ((double) 1) / ((double)(hams.length + 2)));
    			
    		}
    		//if the word is in spamWords
    		if (spamWordCounts.containsKey(word)) {
    			//then you add 1 to the value of the count in the spamWord counts as the numerator for the probability 
    			//the denominator is just num of spam emails + 2
    			spamWordProbabilities.put(word, ((double) spamWordCounts.get(word) + 1) / ((double)(spams.length + 2)));
    		} else {
    			//otherwise its just the same but you don't execute spamWordsCounts.get(word)
    			//the denominator is just num of spam emails + 2
    			spamWordProbabilities.put(word, ((double) 1) / ((double)(spams.length + 2)));
    		}
    		
    	}
    	
    }

    /*
     * Classify the given unlabeled set of emails.
     *
     * Params:
     *      emails - unlabeled email files to be classified
     */
    public void classify(File[] emails) throws IOException  {
    	//goes through each email in emails
    	for(File email : emails) {
    		//makes a set of all distinct words in one email of test email files. 
        	HashSet<String> testWords = new HashSet<String>();
    		//gets set of distinct words in email. 
    		HashSet<String> wordsFromEmail =  tokenSet(email);
    		//goes through each word in the distinct words in that one email
    		for (String word: wordsFromEmail) {
    			//if that word is in the set of all training words
    			if (allWords.contains(word)) {
    				//then we will add it to the test words set. 
    				testWords.add(word);
    			}
    		}
    		
    
    		
        	double hamProbabilitySum = Math.log(probabilityHam); 
        	double spamProbabilitySum = Math.log(probabilitySpam);
        

        	//summing all the ham probabilities and spam probabilities of the test words in a specific email. 
        	for (String testWord: testWords) {
        		hamProbabilitySum = (double) hamProbabilitySum + Math.log((double) hamWordProbabilities.get(testWord));
        		
        		spamProbabilitySum = (double) spamProbabilitySum + Math.log((double) spamWordProbabilities.get(testWord));;

        	}
        
        	
        	System.out.print(email.getName() + " ");
        	if(spamProbabilitySum > hamProbabilitySum) {
        		System.out.println("spam");
        	} else {
        		System.out.println("ham");
        	}

    	}
    }


    /*
     *  Helper Function:
     *  This function reads in a file and returns a set of all the tokens. 
     *  It ignores "Subject:" in the subject line.
     *  
     *  If the email had the following content:
     *  
     *  Subject: Get rid of your student loans
     *  Hi there ,
     *  If you work for us , we will give you money
     *  to repay your student loans . You will be 
     *  debt free !
     *  FakePerson_22393
     *  
     *  This function would return to you
     *  ['be', 'student', 'for', 'your', 'rid', 'we', 'of', 'free', 'you', 
     *   'us', 'Hi', 'give', '!', 'repay', 'will', 'loans', 'work', 
     *   'FakePerson_22393', ',', '.', 'money', 'Get', 'there', 'to', 'If', 
     *   'debt', 'You']
     */

    public static HashSet<String> tokenSet(File filename) throws IOException {
        HashSet<String> tokens = new HashSet<String>();
        Scanner filescan = new Scanner(filename);
        filescan.next(); // Ignoring "Subject"
        while(filescan.hasNextLine() && filescan.hasNext()) {
            tokens.add(filescan.next());
        }
        filescan.close();
        return tokens;
    }
}
