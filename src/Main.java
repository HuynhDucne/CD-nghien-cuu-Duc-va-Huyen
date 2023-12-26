import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {


        /**
         * Khởi tạo và chạy thuật toán TUFP
         * */
        TUFPAlgorithm<Integer, Integer, Double> tufp = new TUFPAlgorithm<>(6);

        // filePath là tên file chứa dataset
        String filePath = "chess.txt";
        // filePathFormat là tên file chứa dataset đã được định dạng
        String filePathFormat = "dataset_prob.txt";

//        Dataset<Integer, Double> data = new Dataset();

        // Đọc dữ liệu dataset và random prob cho mỗi item
        tufp.getDataset().loadFile(filePath);
        // Ghi lại dữ liệu đọc từ dataset với cấu trúc khác vào file dataset_prob.txt
        tufp.getDataset().writeDb(filePathFormat);

        // Chuyển dữ liệu trong txt đã định dạng sang dạng CUP-Lists
        tufp.readDataAndConvertToCUPLists(filePathFormat);

        // In cấu trúc CUP-Lists
//        CUPList.printCUPLists(tufp.getCupLists());

        // Thực thi thuật toán TUFP
        tufp.executeTUFP(tufp.getK());

        // In ra bảng kết quá Top-k cuối cùng
        tufp.printTopKUFP();

        // Thống kê bộ nhớ và thời gian chạy
        tufp.printStats();

//        System.out.println(tufp.getDataset().getTransactions());
//        tufp.getDataset().printDatabase();
//        System.out.println(tufp.getDataset().getItems());
    }
}