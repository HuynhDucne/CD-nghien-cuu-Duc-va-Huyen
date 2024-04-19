package algorithms.tuwfi.gui;

import algorithms.tuwfi.bll.TuwfiBll;
import algorithms.tuwfi.dto.Cup;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class TestTuwfiTestcase {

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java TestTuwfiTestcase1 <probFile> <weightFile> <k>");
            return;
        }

        String probFile = args[0];
        String weightFile = args[1];
        int k = Integer.parseInt(args[2]);

        // resultFile là tên file chứa kết quả Top-k UWFIs
        String resultFile = "result_TUWFI_" + probFile.substring(0, probFile.indexOf("_prob")) + "_k_" + k + ".txt";

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
