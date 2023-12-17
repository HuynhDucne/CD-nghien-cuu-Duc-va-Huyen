import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String filePath = "db.txt";

        // Sử dụng hàm để đọc dữ liệu và chuyển sang CUP-Lists
        List<CUPList<String, Integer, Double>> cupLists = CUPList.readDataAndConvertToCUPLists(filePath);

        // In cấu trúc CUP-Lists
        System.out.println("In cấu trúc CUP-Lists");
        for (CUPList<String, Integer, Double> cupList : cupLists) {
            System.out.println("Name: " + cupList.getItemName());
            System.out.println("ExpSup: " + cupList.getExpSup());
            System.out.println("MaxProb: " + cupList.getMaxProb());
            for (TEPList<Integer, Double> tepList : cupList.getTepList()) {
                System.out.println("\t\t" + tepList.getTid() + ": " + tepList.getProb());
            }
            System.out.println("-----------------------");
        }

        System.out.println("Result:");

        // Khởi tạo và chạy thuật toán TUFP
        TUFPAlgorithm<String, Integer, Double> tufp = new TUFPAlgorithm<>(cupLists, 6);
        tufp.setResult(tufp.executeTUFP(tufp.getCupLists(), tufp.getK()));

        // In ra bảng result kết quá cuối cùng
        tufp.printTopKUFP();

        // Thống kê bộ nhớ và thời gian chạy
        tufp.printStats();

//        TransactionDatabase td = new TransactionDatabase();
//        td.loadFile("chess.txt");
//        td.printDatabase();
    }
}