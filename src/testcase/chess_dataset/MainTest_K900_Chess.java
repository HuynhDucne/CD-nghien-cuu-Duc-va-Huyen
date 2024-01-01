package testcase.chess_dataset;

import algorithms.TUFP.TUFPAlgorithm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class MainTest_K900_Chess {
    public static void main(String[] args) throws IOException {

        // filePath là tên file chứa dataset
        String filePath = fileToPath("chess.txt");

        // filePathFormat là tên file chứa dataset đã được định dạng có xác suất random
        String filePathFormat = "dataset_prob.txt";

        // Khởi tạo top-K
        int k = 900;

        // Khởi tạo và chạy thuật toán TUFP
        TUFPAlgorithm<Integer, Integer, Double> tufp = new TUFPAlgorithm<>(k);

        // Đọc dữ liệu dataset và random prob cho mỗi item
        tufp.getDataset().loadFile(filePath);

        // Ghi lại dữ liệu đọc từ dataset với cấu trúc khác vào file dataset_prob.txt
        tufp.getDataset().writeDb(filePathFormat);

        // Đọc dữ liệu từ dataset để tạo cấu trúc CUP-Lists
        tufp.readDatasetToCupLists();

        // In CUP-Lists
//        CUPList.printCUPLists(tufp.getCupLists());

        // In size CUP-Lists
//        System.out.println(tufp.getCupLists().size());

        // Thực thi thuật toán TUFP
        tufp.executeTUFP();

        // In ra bảng kết quả Top-k cuối cùng
        tufp.printTopKUFP();

        // Thống kê bộ nhớ và thời gian chạy
        tufp.printStats();
    }

    /**
     * Phương thức này để lấy đường dẫn của file dataset
     *
     * @param filename tên của file
     * @return trả về đường dẫn của file dưới dạng chuỗi
     * @throws UnsupportedEncodingException
     */
    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTest_K900_Chess.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(), "UTF-8");
    }
}
