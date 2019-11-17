import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class FasterLCS {

        static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

        //Define classes
        public static class LCS {
            int LcsLength = 0;
            int lcsStartIndexInS1 = 0;
            int lcsStartIndexInS2 = 0;

        }

        /* define constants */
        static long MAXVALUE =  2000000000;
        static long MINVALUE = -2000000000;
        static int numberOfTrials = 1;
        static int MAXINPUTSIZE  = (int) Math.pow(1.5,10);
        static int MININPUTSIZE  =  1;

        static int randomNumber = 0;
        static String EnglishText = "";
        static String EnglishText2 = "";
        // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time
        static final String fnl = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        static Random rnd = new Random();
        static  String filepath1 = "/home/diana/JaneEyre.txt";

        static String s1 = "BELOWIFJASDIEKJSAJASDSASHFAEBD";
        static String s2 = "AAEIOLOOSIDJSDIJAWEJKNBE";
        static String s12 = "";
        static String s22 = "";
        static LCS result = new LCS();

        static String ResultsFolderPath = "/home/diana/Results/"; // pathname to results folder
        static FileWriter resultsFile;
        static PrintWriter resultsWriter;




        public static void main(String[] args) throws Exception {

            // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized

            System.out.println("Running first full experiment...");
            runFullExperiment("BruteForceLCSEnglishCompare2Text-Exp1-ThrowAway.txt");
            System.out.println("Running second full experiment...");
            runFullExperiment("BruteForceLCSEnglishCompare2Text-Exp2.txt");
            System.out.println("Running third full experiment...");
            runFullExperiment("BruteForceLCSEnglishCompare2Text-Exp3.txt");
        }

        static void runFullExperiment(String resultsFileName) throws Exception {
            //declare variables for doubling ratio
            double[] averageArray = new double[1000];
            double currentAv = 0;
            double doublingTotal = 0;
            int x = 1;

            //test run
            verifyLCS(s1, s2);

            //set up print to file
            try {
                resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
                resultsWriter = new PrintWriter(resultsFile);
            } catch(Exception e) {
                System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
                return; // not very foolproof... but we do expect to be able to create/open the file...
            }

            //declare variables for stop watch
            ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
            ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial

            //add headers to text file
            resultsWriter.println("#X(Value) LCSLength  N(Size)  AverageTime        FibNumber   NumberOfTrials"); // # marks a comment in gnuplot data
            resultsWriter.flush();

            /* for each size of input we want to test: in this case starting small and doubling the size each time */
            for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize*=2) {

                //Create Random string
                //s12 =  RandomString(inputSize);
                //s22= RandomString(inputSize);

                ///generate strings with the same length and every character is the same - worst case scenario
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < inputSize; i++){
                    sb.append('C');
                }
                s12 = sb.toString();
                StringBuilder sb2 = new StringBuilder();
                for(int i = 0; i < inputSize; i++){
                    sb2.append('C');
                }
                s22 = sb2.toString();

                //Create two strings from English Text String
                /*EnglishText = readFileAsString("/home/diana/senseandsensibility");
                EnglishText = EnglishText.replaceAll("\\s", "");
                EnglishText2 = readFileAsString("/home/diana/prideandprejudice");
                EnglishText2 = EnglishText2.replaceAll("\\s", "");
                //System.out.println(EnglishText);
                //s12 = SubstringEnglishText(inputSize, EnglishText);
                //s22 = SubstringEnglishText(inputSize, EnglishText);

                s12 = EnglishText;
                s22 = EnglishText2;*/

                //System.out.println("s1 = " + s12);
                //System.out.println("s2 = " + s22);

                // progress message...
                System.out.println("Running test for input size "+inputSize+" ... ");

                /* repeat for desired number of trials (for a specific size of input)... */
                long batchElapsedTime = 0;
                // generate a list of randomly spaced integers in ascending sorted order to use as test input
                // In this case we're generating one list to use for the entire set of trials (of a given input size)
                // but we will randomly generate the search key for each trial
                //System.out.print("    Generating test data...");

                //generate random integer list
                //long resultFib = Fib(x);

                //print progress to screen
                //System.out.println("...done.");
                System.out.print("    Running trial batch...");

                /* force garbage collection before each batch of trials run so it is not included in the time */
                System.gc();


                // instead of timing each individual trial, we will time the entire set of trials (for a given input size)
                // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the
                // stopwatch methods themselves
                BatchStopwatch.start(); // comment this line if timing trials individually

                // run the trials
                for (long trial = 0; trial < numberOfTrials; trial++) {
                    // generate a random key to search in the range of a the min/max numbers in the list
                    //long testSearchKey = (long) (0 + Math.random() * (testList[testList.length-1]));
                    /* force garbage collection before each trial run so it is not included in the time */
                    //System.gc();

                    //TrialStopwatch.start(); // *** uncomment this line if timing trials individually
                    /* run the function we're testing on the trial input */
                    result = LargestCommonSubstring(s12, s22);
                    System.out.println("LSC length = " + result.LcsLength);
                    //System.out.println("Index at String 1 = " + result.lcsStartIndexInS1);
                    //System.out.println("Index at String 2 = " + result.lcsStartIndexInS2);

                    // batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually
                }
                batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually
                double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch

                //put current average time in array of average times. We will be able to use this to calculate the doubling ratio
                averageArray[x] = averageTimePerTrialInBatch;

                //skip this round if this is the first one (no previous average for calculation)
                if(inputSize != 0){
                    doublingTotal = averageTimePerTrialInBatch/averageArray[x-1]; //Calculate doubling ratio

                }
                x++;
                int countingbits = countBits(inputSize);
                /* print data for this size of input */
                resultsWriter.printf("%6d %d %6d %15.2f %4d\n",inputSize, result.LcsLength, countingbits, averageTimePerTrialInBatch, numberOfTrials); // might as well make the columns look nice
                resultsWriter.flush();
                System.out.println(" ....done.");
            }
        }

        //create substring from English Text
        public static String SubstringEnglishText(int x, String EngText){
            StringBuilder sb = new StringBuilder();
            int randomStartIndex = 0;
            randomStartIndex =(int) (0 + Math.random() * ((EngText.length() - x) - 0));
            int i = 0;

            for(i = 0; i < x; i++){
                sb.append((EngText.charAt(randomStartIndex + i)));
            }
            //System.out.println("new String = " + sb.toString());
            return sb.toString();
        }

        public static String readFileAsString(String fileName) throws Exception {
            String data = "";
            data = new String(Files.readAllBytes(Paths.get(fileName)));
            return data;
        }

        //create a random string
        public static String RandomString(int x){
            StringBuilder sb = new StringBuilder();
            int y = 0;
            for(y = 0; y < x; y++){
                sb.append((fnl.charAt(rnd.nextInt(fnl.length()))));
            }
            return sb.toString();
        }

        /*Verify LCS is working*/
        static void verifyLCS(String s1,String s2){
            //Declare variables for Largest common string class
            LCS resultLcs =new LCS();
            resultLcs = LargestCommonSubstring(s1, s2); //run the largest common string and return the result
            //print the result to verify it is correct
            System.out.println("Testing..." + s1 + " and " + s2 + " lcs = " + resultLcs.LcsLength + "," + resultLcs.lcsStartIndexInS1 + "," + resultLcs.lcsStartIndexInS2);
        }

        public static LCS LargestCommonSubstring(String s1, String s2){
            /*Declare Variables*/
            LCS common = new LCS();
            int l1 = s1.length() - 1;
            int l2 = s2.length() - 1;
            common.LcsLength = 0;
            common.lcsStartIndexInS1 = 0;
            common.lcsStartIndexInS2 = 0;
            int i, j, k = 0;
            for(i = 0; i <= l1; i++ ){ //loop through the first string
                for(j = 0; j<= l2; j++){ //loop through the second string
                    for(k=0; k<= Math.min(l1-i, l2-j); k++){  //loop through the substring
                        if(s1.charAt(i + k) != s2.charAt(j+k)) //check if the characters from string 1 and 2 are the same or not
                        {
                            break;  //if they are not the same, exit the loop
                        }
                    }
                    if(k >= common.LcsLength){ //check if the length of the substring (k) is the longest substring.
                        common.LcsLength = k; //if it's the longest, set the LCS length to k
                        common.lcsStartIndexInS1 = i; //set the start index of the substring of s1 to i
                        common.lcsStartIndexInS2 = j; //set the start index of the substring of s2 to j
                    }
                }
            }
            //return the class LCS (largest common string)
            return  common;
        }
        //count the number of bits required for current fib number
        static int countBits(int n)
        {
            int count = 0;
            //if n == 0, count will be 1
            if(n == 0){
                count = 1;
            }
            //loop while n does not equal 0
            while (n != 0)
            {
                //each loop add 1 to count
                count++;
                //shift n to the left by 1
                n >>= 1;
            }
            //System.out.println("number of bits = " + count);
            return count;
        }

}
