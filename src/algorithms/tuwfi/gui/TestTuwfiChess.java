package algorithms.tuwfi.gui;

import algorithms.tuwfi.dto.Cup;
import algorithms.tuwfi.bll.TuwfiBll;
import algorithms.tuwfi.dal.TuwfiDal;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class TestTuwfiChess {
    public static void main(String[] args) throws IOException {

        // probFile là tên file chứa dataset đã được định dạng có xác suất random
        String probFile = "chess_prob.txt";

        // weightFile là tên file chứa tất cả các item trong dataset và weight của từng item
        String weightFile = "chess_weight.txt";

        // Nhập top-K
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter top-k of UWFPs: ");
        String input = sc.nextLine();
        int k = Integer.parseInt(input);

        // resultFile là tên file chứa kết quả Top-k UWFIs
        String resultFile = "result_TUWFI_chess_k_" + k + ".txt";

        // Thực thi thuật toán TUWFI
        List<Cup> result = TuwfiBll.executeTuwfi(probFile, weightFile, k);

        // In CUP-List
//        List<Cup> cupList = TuwfiDal.generateCupListForItems(probFile);
//        Cup.printCupList(cupList);

        // In ra bảng kết quả Top-k Uncertain Weighted Frequent Itemsets
        TuwfiBll.printTuwfi(result);

        // Thống kê bộ nhớ và thời gian chạy
        TuwfiBll.printStats();

        // Ghi kết quả chạy chương trình vào file resultFile
        TuwfiBll.writeResultsToFile(result, resultFile);
    }
}
