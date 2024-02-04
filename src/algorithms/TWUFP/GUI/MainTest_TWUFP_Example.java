package algorithms.TWUFP.GUI;

import algorithms.TWUFP.BLL.CUPList;
import algorithms.TWUFP.BLL.TWUFPAlgoBLL;
import algorithms.TWUFP.DAL.TWUFPAlgoDAL;

import java.io.IOException;
import java.util.List;

public class MainTest_TWUFP_Example {
    /**
     * Hàm main này để test ví dụ mẫu trong bài báo
     */
    public static void main(String[] args) throws IOException {

        // fileProb là tên file chứa dataset đã được định dạng có xác suất
        String fileProb = "example_prob_weighted.txt";

        // fileWeight là tên file chứa tất cả các item trong dataset và weight của từng item
        String fileWeight = "example_weight.txt";

        // Khởi tạo top-K
        int k = 33;

        // Đọc file chứa weight để lấy dữ liệu
        TWUFPAlgoDAL.loadWeight(fileWeight);
//        System.out.println(TUFPAlgoDAL.weightMap);

        // Chuyển dữ liệu trong file đã định dạng sang cấu trúc CUP-Lists
        List<CUPList> cupLists = TWUFPAlgoDAL.loadDatasetProb(fileProb, k);
//        System.out.println(TUFPAlgoDAL.itemsNameList);
//        System.out.println(TUFPAlgoDAL.tubwpList);

        // In CUP-Lists
//        CUPList.printCUPLists(cupLists);

        // Thực thi thuật toán TWUFP
        List<CUPList> result = TWUFPAlgoBLL.executeTWUFP(cupLists, k);

        // In ra bảng kết quả Top-k cuối cùng
        TWUFPAlgoBLL.printTopKWUFP(result);

        // Thống kê bộ nhớ và thời gian chạy
        TWUFPAlgoBLL.printStats();
    }
}