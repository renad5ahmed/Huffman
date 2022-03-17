package DataCompression;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.Scanner;

public class Huffman {
    public static HashMap<Character, Integer> map=new HashMap();
    private static  int alphabetSize=265;
    static class Node implements Comparable<Node> {
    private  char character;
    private  int frequency;
    private  Node leftChild;
    private  Node rightChild;
    
   public Node( char character,  int frequency,  Node left,  Node right){
    this.character=character;
    this.frequency=frequency;
    leftChild=left;
    rightChild=right;
    
    }
    boolean isLeaf(){
        return this.leftChild==null && this.rightChild==null;
    }

        @Override
        public int compareTo( Node that) {
             int freqComparison=Integer.compare(this.frequency, that.frequency);
            if(freqComparison!=0){
                return freqComparison;
            }
            return Integer.compare(this.character, that.character);
        }
    }
    static class HuffmanEncodedResult{

        Node root;
        String encodedData;
        HuffmanEncodedResult( String encodedData, Node root)
        {
            this.encodedData=encodedData;
            this.root=root;        
        }
        public Node getroot()
        {
            return this.root;
        }
        public String getEncodedData()
        {
             return this.encodedData;   
        }
    }
    public static int[] buildFreq(String data){  
    int original=data.length()*8;
      int counterArr[]= new int [alphabetSize];
      int count= (int) data.chars().distinct().count();
     //System.out.println(count); // show the count of the alphabet
       for(int i=0; i<data.length(); i++){
            char a = data.charAt(i);
            counterArr[a]++;
            map.put(a, counterArr[a]);//storing char and its counter
            
    }
       System.out.println("The Original size of the file :"+original);
       System.out.println(map);
       
       return counterArr;
    }
    public static HashMap getMap(){
    return map;
    }
    
    public static Node buildHuffmanTree(int[] freq){
         PriorityQueue<Node> priorityQueue= new PriorityQueue<>();
        for(char i=0;i<alphabetSize;i++){
            if(freq[i]>0){
        priorityQueue.add(new Node(i,freq[i], null, null));
    }
}
        
if(priorityQueue.size()==1)
{
    priorityQueue.add(new Node('\0',1,null,null));
}

while(priorityQueue.size()>1){
    Node left=priorityQueue.poll();
    Node right=priorityQueue.poll();
    Node parent;
    parent = new Node('\0', (left.frequency+right.frequency), left, right); 
    priorityQueue.add(parent);
}
        return priorityQueue.poll();

    }
    
    public static void buildTableImpl(Node node,  String s,  Map<Character,String>lookupTable)
    {
        if (!node.isLeaf())
        {
            buildTableImpl(node.leftChild, s + '0',lookupTable);
            buildTableImpl(node.rightChild, s+'1', lookupTable);
        }
        else
        {
            lookupTable.put(node.character, s);
        }
    }
    public static Map<Character, String>buildTable( Node root)
    {
        Map<Character, String>lookupTable=new HashMap<>();
        buildTableImpl(root,"",lookupTable);//mohem
        return lookupTable;
        
    }
    public HuffmanEncodedResult compress( String data){
      
        int[] freq=buildFreq(data);
        
        Node root=buildHuffmanTree(freq);
        Map<Character,String>lookupTable=buildTable(root);
        
        return new HuffmanEncodedResult(generateEncodedData(data,lookupTable),root);
    }
    public static String generateEncodedData( String data, Map<Character,String>lookupTable)
    {
         StringBuilder builder=new StringBuilder();
        for( char character:data.toCharArray())
        {
            builder.append(lookupTable.get(character));
        }
   //     System.out.println(builder.toString());
        return builder.toString();
        }

    public void decompress(){
       try
        {
            HashMap<String, String> lookup=new HashMap();
            File file2=new File("D:\\compressed.txt");
            Scanner myReader2=new Scanner(file2);//reading compressed one
            while (myReader2.hasNextLine())
            {
                String test=myReader2.nextLine();
                String subTest=test.substring(0,1);
                if(!subTest.equals("0")&& !subTest.equals("1"))
                {
                    lookup.put(subTest,test.substring(2));                
                } 
                else
                {
                    String code= test;
                
                    int z=0;
                    String line="";
                    for(int i=1;i<code.length()+1;i++){

                    for (String value : lookup.keySet()) {
                        if(code.substring(z,i).equals(lookup.get(value))){
                        
                      
                          line+=value;
                        z=i;
                        }
                    }
                    }
                                System.out.println("Decompressed file line="+line);
                }
        }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Error Occured");
        }
        
    }
    public static void main(String[] args) throws IOException{
            try
        {
            File file1=new File("D:\\Data.txt");
            Scanner myReader1=new Scanner(file1);
            String test=myReader1.nextLine();
            int original=test.length()*8;
            System.out.println("String that will be compressed ="+test);
            int[] ft=buildFreq(test);
            Node n=buildHuffmanTree(ft);
            HuffmanEncodedResult h=new HuffmanEncodedResult(test,n);
            Huffman f=new Huffman();
            Map<Character,String>lookup=buildTable(n);
            String z=f.generateEncodedData(test,lookup);

//read text to be compressed
HashMap k=getMap();
File file = new File("D:\\compressed.txt");

        BufferedWriter bf = null;
        try{
            
            //create new BufferedWriter for the output file
            bf = new BufferedWriter( new FileWriter(file) );
            //iterate map entries
            int sum=0; int len=0; int counter=0;
            for(Map.Entry<Character, String> entry : lookup.entrySet()){
                //put key and value separated by a colon
                bf.write( entry.getKey() + ":" + entry.getValue() );
                counter=(int)k.get(entry.getKey());
                len=entry.getValue().length();
                sum+=len*counter;
                //new line
                bf.newLine();
            }
            
            System.out.println("Compressed size of the file="+sum);
            double ratio=((double)original)/sum;
            System.out.println("Compression Ratio="+ratio);
            bf.write(z);
            bf.flush();
        }catch(IOException e){}
            try{
                //always close the writer
                bf.close();
            }catch(IOException e){}
                        
            f.decompress();
        }
            
        catch (FileNotFoundException e)
        {
            System.out.println("Error Occured");
        }
}
    }
    