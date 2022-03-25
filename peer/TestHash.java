import java.io.File;

public class TestHash {
    public static void main(String[] args) {
        Peer peer = new Peer(); 
        String expectedHash = "b18ea92d528952c1724ac36a3079dfd8";   //Expected hash
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
