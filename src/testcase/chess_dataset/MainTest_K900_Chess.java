package testcase.chess_dataset;

import algorithms.TUFP.BLL.CUPList;
import algorithms.TUFP.BLL.TUFPAlgorithm;
import algorithms.TUFP.DAL.Dataset;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;

public class MainTest_K900_Chess {
    public static void main(String[] args) throws IOException {

        // filePath là tên file chứa dataset
        String filePath = fileToPath("chess.txt");

        // filePathFormat là tên file chứa dataset đã được định dạng có xác suất random
        String filePathFormat = "seed_chess.txt";

        // Khởi tạo top-K
        int k = 900;

        Dataset.loadFile(filePath);

//        List<CUPList> cupLists = TUFPAlgorithm.readDatasetToCupLists(k);

        // Thực thi thuật toán TUFP
//        List<CUPList> result = TUFPAlgorithm.executeTUFP(cupLists, k);

        // In ra bảng kết quả Top-k cuối cùng
//        TUFPAlgorithm.printTopKUFP(result);

        // Thống kê bộ nhớ và thời gian chạy
        TUFPAlgorithm.printStats();

        System.out.println(Dataset.getTransactions());

    }

    /**
     * Phương thức này để lấy đường dẫn của file dataset
     *
     * @param filename tên của file
     * @return trả về đường dẫn của file dưới dạng chuỗi
     * @throws UnsupportedEncodingException xuất hiện khi sử dụng một hệ thống mã hóa ký tự
     * không được hỗ trợ trong chuỗi hoặc byte của Java
     */
    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTest_K900_Chess.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(), "UTF-8");
    }
}
