package algorithms.TWUFP.GUI;

import algorithms.TWUFP.BLL.CUPList;
import algorithms.TWUFP.BLL.TWUFPAlgoBLL;
import algorithms.TWUFP.DAL.TWUFPAlgoDAL;

import java.io.IOException;
import java.util.List;

public class MainTest_TWUFP_Chess {
    public static void main(String[] args) throws IOException {

        // fileProb là tên file chứa dataset đã được định dạng có xác suất random
        String fileProb = "chess_prob.txt";

        // fileWeight là tên file chứa tất cả các item trong dataset và weight của từng item
        String fileWeight = "chess_weight.txt";

        // Khởi tạo top-K
        int k = 900;

        // Đọc file chứa weight để lấy dữ liệu
        TWUFPAlgoDAL.loadWeight(fileWeight);
//        System.out.println(TWUFPAlgoDAL.weightMap);

        // Đọc dữ liệu từ dataset để tạo cấu trúc CUP-Lists
        List<CUPList> cupLists = TWUFPAlgoDAL.loadDatasetProb(fileProb, k);

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
