package algorithms.TUFP.GUI;

import algorithms.TUFP.BLL.CUPList;
import algorithms.TUFP.BLL.TUFPAlgoBLL;
import algorithms.TUFP.DAL.TUFPAlgoDAL;

import java.io.IOException;
import java.util.List;

public class MainTest_TUFP_Example {
    /**
     * Hàm main này để test ví dụ mẫu trong bài báo
     */
    public static void main(String[] args) throws IOException {

        // fileProb là tên file chứa dataset đã được định dạng có xác suất
        String fileProb = "example_prob.txt";

        // Khởi tạo top-K
        int k = 10;

        // Chuyển dữ liệu trong file đã định dạng sang cấu trúc CUP-Lists
        List<CUPList> cupLists = TUFPAlgoDAL.loadDatasetProb(fileProb, k);

        // In CUP-Lists
        CUPList.printCUPLists(cupLists);

        // Thực thi thuật toán TUFP
        List<CUPList> result = TUFPAlgoBLL.executeTUFP(cupLists, k);

        // In ra bảng kết quả Top-k cuối cùng
        TUFPAlgoBLL.printTopKUFP(result);

        // Thống kê bộ nhớ và thời gian chạy
        TUFPAlgoBLL.printStats();
    }
}