import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class DecryptV {
    private final String cypher = "F96DE8C227A259C87EE1DA2AED57C93FE5DA36ED4EC87EF2C63AAE5B9A7EFF"
            + "D673BE4ACF7BE8923CAB1ECE7AF2DA3DA44FCF7AE29235A24C963FF0DF3CA3599A70E5DA36BF1"
            + "ECE77F8DC34BE129A6CF4D126BF5B9A7CFEDF3EB850D37CF0C63AA2509A76FF9227A55B9A6FE"
            + "3D720A850D97AB1DD35ED5FCE6BF0D138A84CC931B1F121B44ECE70F6C032BD56C33FF9D320ED5"
            + "CDF7AFF9226BE5BDE3FF7DD21ED56CF71F5C036A94D963FF8D473A351CE3FE5DA3CB84DDB71F5C"
            + "17FED51DC3FE8D732BF4D963FF3C727ED4AC87EF5DB27A451D47EFD9230BF47CA6BFEC12ABE4ADF"
            + "72E29224A84CDF3FF5D720A459D47AF59232A35A9A7AE7D33FB85FCE7AF5923AA31EDB3FF7D33A"
            + "BF52C33FF0D673A551D93FFCD33DA35BC831B1F43CBF1EDF67F0DF23A15B963FE5DA36ED68D378"
            + "F4DC36BF5B9A7AFFD121B44ECE76FEDC73BE5DD27AFCD773BA5FC93FE5DA3CB859D26BB1C63CED"
            + "5CDF3FE2D730B84CDF3FF7DD21ED5ADF7CF0D636BE1EDB79E5D721ED57CE3FE6D320ED57D469F4"
            + "DC27A85A963FF3C727ED49DF3FFFDD24ED55D470E69E73AC50DE3FE5DA3ABE1EDF67F4C030A44D"
            + "DF3FF5D73EA250C96BE3D327A84D963FE5DA32B91ED36BB1D132A31ED87AB1D021A255DF71B1C43"
            + "6BF479A7AF0C13AA14794";
    
    private final int keyMin = 1;
    private final int keyMax = 13;
    private Map<Integer, Double> cf;
    
    DecryptV() {
        //HashMap of lowercase character distributions in English taken from Wikipedia.
        cf = new HashMap<>();
        cf.put(97, .08167);
        cf.put(98, .01492);
        cf.put(99, .02782);
        cf.put(100, .04253);
        cf.put(101, .12702);
        cf.put(102, .02228);
        cf.put(103, .02015);
        cf.put(104, .06094);
        cf.put(105, .06966);
        cf.put(106, .00153);
        cf.put(107, .00772);
        cf.put(108, .04025);
        cf.put(109, .02406);
        cf.put(110, .06749);
        cf.put(111, .07507);
        cf.put(112, .01929);
        cf.put(113, .00095);
        cf.put(114, .05987);
        cf.put(115, .06327);
        cf.put(116, .09056);
        cf.put(117, .02758);
        cf.put(118, .00978);
        cf.put(119, .02361);
        cf.put(120, .00150);
        cf.put(121, .01974);
        cf.put(122, .00074);
    }
    
    //Returns the p squared value of an input map of letters (represented as ASCII ints) to their occurence
    //rates (represented as a double > 0 and <= 1)
    public double p2(Map<Integer,Double> m) {
        double p = 0;
        for (double d : m.values()) {
            p += d*d;
        }
        
        return p;
    }
    
    //Returns the squared value of two occurance rate maps of character multiplied together.
    public double p2(Map<Integer,Double> m1, Map<Integer,Double> m2) {
        double p = 0;
        for (Entry<Integer,Double> e : m1.entrySet()) {
            p += (m2.containsKey(e.getKey())) ? 
                    e.getValue() * m2.get(e.getKey()) :
                    0;
        }
        return p;
    }
   
    //Converts a 256 hex value represented as a String to a Integer of the same value.
    public int getHex(String hex) {
        hex = "0x" + hex;
        return Integer.decode(hex);
    }
    
    //Returns whether the input int is a valid lowercase character.
    public boolean isChar(int ch) {
        if (31 < ch && ch < 128) {
            return true;
        }
        return false;
    }
    
    //Returns a list of only lowercase characters from the input hex String with each hex value being XORed
    //by the key
    public List<Integer> lwList(String text, int key) {
        return makeList(text,key,false);
    }
    
    //Returns a list of all characters from the input hex String with each hex value being XORed/
    //by the key
    public List<Integer> rawList(String text, int key) {
        return makeList(text, key, true);
    }
    
    //Returns a list of all character from an input hex String XORed with a key.
    public List<Integer> makeList(String text, int key, boolean rawList) {
        List<Integer> rl = new ArrayList<>();
        for (int i = 2; i <= text.length(); i+=2) {
            int ch = key ^ getHex(text.substring(i-2, i));
            if (rawList || (97 <= ch && ch <= 122)) {
                rl.add(ch); 
            }
        }
        return rl;
    }
    
    //Returns a character frequency map from a given hex String.
    public Map<Integer,Double> chFreqMap(String text) {
        List<Integer> tl = lwList(text, 0);
        return chFreqMap(tl);
    }
    
    //Returns a character frequence map from an input list of character ints.
    public Map<Integer,Double> chFreqMap(List<Integer> tl) {
        int length = tl.size();
        Map<Integer,Double> cm = tl.stream()
                .collect(Collectors.groupingBy(
                b -> b, 
                Collectors.summingDouble(c -> (double) 1/length)
                ));
        return cm;
    }
    
    //Returns the ASCII version of a hex String.
    public String printString(String text) {
        StringBuilder sb = new StringBuilder();
        int length = text.length();
        for (int i = 2; i < length ; i+=2) {
            int cur = getHex(text.substring(i-2, i));
            sb.append((char) cur);
        }
        return sb.toString();
    }
    
    //Returns the hex version of an ASCII String.
    public String str2Hex(String text) {
        StringBuilder sb = new StringBuilder();
        for (char ch : text.toCharArray()) {
            sb.append(Integer.toHexString(ch));
        }
        return sb.toString();
    }
    
    //decrypts an input String given an array of key values by which to do the decryption.
    public String decrypt(String text, int[] keys) {
        StringBuilder sb = new StringBuilder();
        int length = text.length();
        for (int i = 2; i < length ; i+=2) {
            int cur = getHex(text.substring(i-2, i));
            int key = keys[((i-2)/2) % keys.length];
//            System.out.println(key);
            cur = cur ^ key;
            sb.append((char) cur);
        }   
        return sb.toString();
    }
    
    //Returns an array of ints that is the key needed to decrypt the input hex String.
    public int[] getKey(String text) {
        int kLength = getKeyLength(text);
        int[] key = new int[kLength];
        for (int i = 0; i < kLength; i++) {
            key[i] = getKeyDigit(slHexStr(text, i, kLength));
        }
        return key;
    }

    //Returns an array of ints representing the input hex String.
    public int[] strToArray(String text) {
        int[] ta = new int[text.length()/2];
        for (int i = 2; i < text.length(); i+=2) {
            ta[i/2] = getHex(text.substring(i-2, i));
        }
        return ta;
    }
    
    //Returns the single decryption key for a given hex String where the String is expected to be one
    //partition of the original hex String.
    public int getKeyDigit(String text) {
        int key = 0;
        double keyFreq = 0;
        for (int i = key; i <= 256; i++) {
            double curKeyFreq = getFreq(text,i);
            if (curKeyFreq > keyFreq) {
                keyFreq = curKeyFreq;
                key = i;
            }
        }
        System.out.println("KeyFreq = " + keyFreq);
        return key;
    }
    
    //Returns the p-i frequency of elements from an input hex String partition combined with an attempted decrypt from 
    //the input key
    public double getFreq(String text, int key) {
        List<Integer> tl = rawList(text,key);
        Map<Integer,Double> cm = chFreqMap(tl);
//        System.out.println("Key = " + key + " Map = " + cm);
        return p2(chFreqMap(tl),cf);
    }
    
    //Returns the key length of the vigenere cypher used the encrypt the input hex String.
    public int getKeyLength(String text) {
        int keyLength = 0;
        double keyFreq = 0;
        for (int i = keyMin; i <= keyMax; i++) {
            double curKeyFreq = calcPSq(slHexStr(cypher, 0, i));
//            System.out.println("Key length " + i + " has freq = " + curKeyFreq);
            if (curKeyFreq > keyFreq) {
                keyFreq = curKeyFreq;
                keyLength = i;
            }
        }
        return keyLength;
    }
    
    //Returns the psquared value of the input hex String.
    public double calcPSq(String text) {
        List<Integer> rl = rawList(text, 0);
        Map<Integer,Double> cfm = chFreqMap(rl);
        return p2(cfm);
    }
    
    //Returns one parition of a hex String, provided the slice desired, and the total keylength/number of slices
    public String slHexStr(String message, int whichSlice, int totalSlices) {
        return slStrBy(message,whichSlice,totalSlices,2);
    }
    
    //Returns one parition of an ASCII String, provided the slice desired, and the total keylength/number of slices    
    public String slStr(String message, int whichSlice, int totalSlices) {
        return slStrBy(message,whichSlice,totalSlices,1);
    }
    
    //Returns slices from both hex and ASCII Strings.
    private String slStrBy(String message, int whichSlice, int totalSlices,int amount) {
        whichSlice = (whichSlice > 0) ? whichSlice % totalSlices : -(whichSlice % totalSlices);
        StringBuilder sb = new StringBuilder();
        for (int i = whichSlice*amount + amount; i < message.length(); i+=amount*totalSlices) {
                sb.append(message.substring(i-amount, i));
        }
        return sb.toString();
    }
    
    //Main metod that runs a bunch of different things I used while debugging my code. Left in for reference.
    public static void main(String[] args) {
        DecryptV dv = new DecryptV();
        System.out.println("Keylength = " + dv.getKeyLength(dv.cypher));
        System.out.println("Keydigit 0 = " + dv.getKeyDigit(dv.slHexStr(dv.cypher, 3, 7)));
        System.out.println("Key array = ");
        Arrays.stream(dv.getKey(dv.cypher)).forEach(a -> System.out.print(a + ", "));
        System.out.println();
//        System.out.println(dv.decrypt(dv.cypher, dv.getKey(dv.cypher)));
//        System.out.println(dv.printString(dv.cypher));
//        System.out.println(dv.printString(dv.slHexStr(dv.cypher, 0, 7)));
//        System.out.println(dv.decrypt(dv.slHexStr(dv.cypher, 0, 7),new int[]{187}));
        System.out.println("P squared = " + dv.p2(dv.cf));
//        System.out.println("P squared = " + dv.p2(dv.cf,dv.cf));
//        System.out.println("Slice squared = " + dv.p2(dv.chFreqMap(dv.str2Hex(dv.decrypt(dv.slHexStr(dv.cypher, 0, 7),new int[]{187})))));
        System.out.println("Ars slice squared = " + dv.p2(dv.chFreqMap(dv.str2Hex("Controlling the map matters for two reasons. First, if Imperial ground troops land on a planet containing the Rebel base, the base is revealed—and all of its units are moved from the Rebel base space at the edge of the board to the system where the base actually resides. Second, controlling territory matters because most planets contain production icons for building new ships or ground units."
                + "Once all leaders have been deployed and their actions resolved, the game gets refreshed. Leaders are pulled back to each player's leader pool for the next round, new missions are drawn, the Rebels gain a new objective card, and the Imperials draw a couple of probe droid cards that narrow down the hunt for the hidden base."
                + "Finally, the round tracker is advanced, new leaders are recruited by each side, and new units are built. (These last two actions don't happen every round; icons on the tracker indicate when they do.) New units enter a build queue on one side of the board and can take one, two, or even three turns to advance down the queue and into play, where they are finally deployed to systems loyal to your faction."
                + "The game proceeds in this way until the Rebel base is destroyed or the Rebels gain enough reputation to win."
                + "But if you think that sounds simple, you will find that it is you who are mistaken... about a great many things."
                )),dv.cf));
    }
    
//    #include <stdio.h>#define KEY_LENGTH 2 // Can be anything from 1 to 13
//
//    main(){  
//      unsigned char ch;  
//      FILE *fpIn, *fpOut;  
//      int i;  unsigned char key[KEY_LENGTH] = {0x00, 0x00};
//      /* of course, I did not use the all-0s key to encrypt */
//
//      fpIn = fopen("ptext.txt", "r");  
//      fpOut = fopen("ctext.txt", "w");
//      i=0;  
//      while (fscanf(fpIn, "%c", &ch) != EOF) {
//        /* avoid encrypting newline characters */      
//       /* In a "real-world" implementation of the Vigenere cipher,        
//          every ASCII character in the plaintext would be encrypted.       
//          However, I want to avoid encrypting newlines here because        
//          it makes recovering the plaintext slightly more difficult... */    
//       /* ...and my goal is not to create "production-quality" code =) */    
//       if (ch!='\n') {      
//         fprintf(fpOut, "%02X", ch ^ key[i % KEY_LENGTH]); // ^ is logical XOR          
//         i++;      }    
//      }   
//     
//      fclose(fpIn);  
//      fclose(fpOut);  
//      return;
//    } 
    
    
}
