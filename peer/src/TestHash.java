import java.io.File;

public class TestHash {
    public static void main(String[] args) {
        Peer peer = new Peer(); 
        String expectedHash = "9b82756c759dcc81911ab9643b334da9";   //Expected hash
        File file = new File("Alibaba_et_les_40_voleurs.txt");

        if(file.exists()) {                                    
            System.out.println("File name : "+file);
            String myHash = peer.getHash(file);                 //Hash computation
            System.out.println("Hash associated : "+myHash);

            System.out.println(myHash.equals(expectedHash));        //Comparison
        }           
        else {
            System.out.println("The file does not exist.");
        }
    }
}
