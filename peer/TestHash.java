public class TestHash {
    public static void main(String[] args) {
        String fileName = "Alibaba et les 40 voleurs";              //File name
        String expectedHash = "efcf9f1ec352d24378952bdad98eeb83";   //Expected hash

        Peer peer = new Peer();                                     

        System.out.println("File name : "+fileName);
        String myHash = peer.getHash(fileName);                     //Hash computation
        System.out.println("Hash associated : "+myHash);

        System.out.println(myHash.equals(expectedHash));            //Comparison
    }
}
