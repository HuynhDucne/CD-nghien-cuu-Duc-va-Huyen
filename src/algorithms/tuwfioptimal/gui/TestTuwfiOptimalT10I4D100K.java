package algorithms.tuwfioptimal.gui;

import algorithms.tuwfioptimal.dto.Cup;
import algorithms.tuwfioptimal.bll.TuwfiOptimalBll;
import algorithms.tuwfioptimal.dal.TuwfiOptimalDal;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class TestTuwfiOptimalT10I4D100K {
    public static void main(String[] args) throws IOException {

        // probFile là tên file chứa dataset đã được định dạng có xác suất random
        String probFile = "T10I4D100K_prob.txt";

        // weightFile là tên file chứa tất cả các item trong dataset và weight của từng item
        String weightFile = "T10I4D100K_weight.txt";

        // Nhập top-K
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter top-k of UWFPs: ");
        String input = sc.nextLine();
        int k = Integer.parseInt(input);

        // resultFile là tên file chứa kết quả Top-k UWFIs Optimal
        String resultFile = "result_TUWFI_optimal_T10I4D100K_k_" + k + ".txt";

        // Thực thi thuật toán TUWFI-Optimal
        List<Cup> result = TuwfiOptimalBll.executeTuwfiOptimal(probFile, weightFile, k);

        // In CUP-List
//        List<Cup> cupList = TuwfiOptimalDal.generateCupListForItems(probFile);
//        Cup.printCupList(cupList);

        // In ra bảng kết quả Top-k Uncertain Weighted Frequent Itemsets
        TuwfiOptimalBll.printTuwfiOptimal(result);

        // Thống kê bộ nhớ và thời gian chạy
        TuwfiOptimalBll.printStats();

        // Ghi kết quả chạy chương trình vào file resultFile
        TuwfiOptimalBll.writeResultsToFile(result, resultFile);
    }
}
