package algorithms.TUFP.GUI;

import algorithms.TUFP.BLL.CUPList;
import algorithms.TUFP.BLL.TUFPAlgorithm;
import algorithms.TUFP.DAL.TUFPAlgorithmDAL;

import java.io.IOException;
import java.util.List;

public class MainTest_TUFP_BMS_POS {
    public static void main(String[] args) throws IOException {

        // filePathFormat là tên file chứa dataset đã được định dạng có xác suất random
        String filePathFormat = "bms-pos_prob.txt";

        // Khởi tạo top-K
        int k = 900;

        // Đọc dữ liệu từ dataset để tạo cấu trúc CUP-Lists
        List<CUPList> cupLists = TUFPAlgorithmDAL.loadDatasetProb(filePathFormat, k);

        // In CUP-Lists
//        CUPList.printCUPLists(tufp.getCupLists());

        // Thực thi thuật toán TUFP
        List<CUPList> result = TUFPAlgorithm.executeTUFP(cupLists, k);

        // In ra bảng kết quả Top-k cuối cùng
        TUFPAlgorithm.printTopKUFP(result);

        // Thống kê bộ nhớ và thời gian chạy
        TUFPAlgorithm.printStats();
    }
}
