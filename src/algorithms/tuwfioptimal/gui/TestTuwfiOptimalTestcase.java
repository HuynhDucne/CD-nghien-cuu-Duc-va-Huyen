package algorithms.tuwfioptimal.gui;

import algorithms.tuwfioptimal.bll.TuwfiOptimalBll;
import algorithms.tuwfioptimal.dto.Cup;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class TestTuwfiOptimalTestcase {
    public static void main(String[] args) throws IOException {

        if (args.length != 3) {
            System.out.println("Usage: java TestTuwfiTestcase1 <probFile> <weightFile> <k>");
            return;
        }

        String probFile = args[0];
        String weightFile = args[1];
        int k = Integer.parseInt(args[2]);

        // resultFile là tên file chứa kết quả Top-k UWFIs Optimal
        String resultFile = "result_TUWFI_optimal_" + probFile.substring(0, probFile.indexOf("_prob")) + "_k_" + k + ".txt";

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
