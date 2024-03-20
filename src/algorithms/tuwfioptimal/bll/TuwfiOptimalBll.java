package algorithms.tuwfioptimal.bll;

import algorithms.tuwfioptimal.dal.TuwfiOptimalDal;
import algorithms.tuwfioptimal.dto.Cup;
import algorithms.tuwfioptimal.dto.Tep;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * TUWFI (Top k Uncertain Weighted Frequent Itemsets) Optimal Algorithm - Business Logic Layer:
 * + Hai biến đo thời gian chạy giải thuật                        -   Type: long
 * + Biến đếm số lượng candidate được tạo ra                      -   Type: int
 * + Top k                                                        -   Type: int
 * + Top k Cup item đơn được sắp xếp giảm dần theo CumWeightSup   -   Type: List<Cup>
 */

public class TuwfiOptimalBll {
    /**
     * biến thời gian bắt đầu thực thi giải thuật
     */
    static long timeStart = 0;

    /**
     * biến thời gian kết thúc thực thi giải thuật
     */
    static long timeEnd = 0;

    /**
     * Số lượng candidate được tạo ra
     */
    static int candidateCount = 0;

    /**
     * Top K uncertain weighted frequent itemsets
     */
    static int topK = 0;

    /**
     * Top k Cup item đơn được sắp xếp giảm dần theo CumWeightSup
     */
    static List<Cup> topKCupOneItem = new ArrayList<>();

    public static List<Cup> getTopKCupOneItem() {
        return topKCupOneItem;
    }

    public static void setTopKCupOneItem(List<Cup> topKCupOneItem) {
        TuwfiOptimalBll.topKCupOneItem = topKCupOneItem;
    }

    /**
     * Thực thi toàn bộ thuật toán TUWFI-Optimal
     *
     * @param probFile Tên tệp chứa bộ dữ liệu có xác suất (cơ sở dữ liệu không chắc chắn)
     * @param weightFile Tên tệp chứa bộ dữ liệu có trọng số (cơ sở dữ liệu có trọng số)
     * @param k top-k
     * @return trả về kết quả top k uncertain weighted frequent itemsets (TUWFI)
     */
    public static List<Cup> executeTuwfiOptimal(String probFile, String weightFile, int k) {

        System.out.println("TUWFI-Optimal Algorithm is running...");

        // Ghi lại thời gian bắt đầu
        timeStart = System.currentTimeMillis();

        // Đặt lại mức sử dụng bộ nhớ đã ghi
        MemoryLogger.getInstance().reset();

        // Lưu giá trị k
        topK = k;

        // Quét qua bộ dữ liệu chứa weight để lấy dữ liệu các item và weight tương ứng
        TuwfiOptimalDal.loadWeight(weightFile);

        // Quét qua bộ dữ liệu có xác suất (cơ sở dữ liệu không chắc chắn) để tạo danh sách các CUP
        // cho từng item, danh sách CUP sẽ được sắp xếp giảm dần theo cumWeightSup, sau đó lấy top-k
        // trong danh sách CUP và cuối cùng gán vào topKCupOneItem
        setTopKCupOneItem(TuwfiOptimalDal.truncateCupList(TuwfiOptimalDal.generateCupListForItems(probFile), k));

        // Copy các phần tử từ topKCupOneItem vào result
        List<Cup> result = new ArrayList<>(topKCupOneItem);

        // Gọi hàm TuwfiOptimalSearch để tìm kiếm CUP itemset mới phù hợp để đưa vào result
        TuwfiOptimalSearch(result);

        // ghi lại thời gian kết thúc
        timeEnd = System.currentTimeMillis();

        // Kiểm tra việc sử dụng bộ nhớ
        MemoryLogger.getInstance().checkMemory();

        return result;
    }

    /**
     * Kết hợp hai CUP với nhau
     *
     * @param cup1 đại diện cho CUP của pattern thứ nhất
     * @param cup2 đại diện cho CUP của item trong CUP thứ hai nhưng không có trong CUP thứ nhất
     * @param weight trọng số của CUP đã kết hợp
     * @return phương thức trả về một CUP mới được kết hợp từ item/itemset thứ nhất và item thứ hai
     */
    public static Cup CombinedTwoCup(Cup cup1, Cup cup2, double weight) {
        // Tạo tên mới cho CUP kết hợp bằng cách nối tên của hai CUP với nhau
        String mergedItemsName = cup1.getPatternName() + ", " + cup2.getPatternName();

        // Khởi tạo CUP mới với tên đã được kết hợp
        Cup newCup = new Cup(mergedItemsName);

        // Gọi hàm tính toán TEP-List từ 2 cup
        List<Tep> newTepList = getTepListOfCombinedCup(cup1, cup2);

        // Cập nhật các thuộc tính của CUP mới như TEP-List, CumSup, MaxProb, weight, CumWeightSup
        newCup.setTepList(newTepList);
        newCup.setCumSup(newCup.calculateCumSup());
        newCup.setMaxProb(newCup.calculateMaxProb());
        newCup.setWeight(weight);
        newCup.setCumWeightSup(weight * newCup.getCumSup());

        // Trả về CUP mới
        return newCup;
    }

    /**
     * Tính toán TEP-List cho CUP sau khi kết hợp. Sử dụng Map để so sánh
     *
     * @param cup1 đại diện cho CUP của item/itemset thứ nhất
     * @param cup2 đại diện cho CUP của item thứ hai
     * @return phương thức trả về danh sách chứa TEP-List mới sau khi kết hợp 2 cup
     * Ví dụ kết hợp CUP C và CUP D:
     *                       CUP C:            CUP D:   =>         CUP CD:
     *                       TID | Prob.       TID | Prob.         TID | Prob.
     *                       1  | 0.9          1  | 0.6            1  | (0.9 * 0.6)
     *                       2  | 0.7          2  | 0.6            2  | (0.7 * 0.6)
     *                       3  | 0.8          3  | 0.9            3  | (0.8 * 0.9)
     *                       4  | 0.9          5  | 0.3            5  | (0.9 * 0.3)
     *                       5  | 0.9          6  | 0.9            7  | (0.4 * 0.6)
     *                       7  | 0.4          7  | 0.6
     * <p>
     * Ví dụ kết hợp CUP CD và CUP CA:
     *                      CUP CD:           CUP A:    =>         CUP CDA:
     *                      TID | Prob.       TID | Prob.          TID | Prob.
     *                      1  | 0.54          1  | 1.0             1  | (0.54 * 1.0)
     *                      2  | 0.42          2  | 0.9             2  | (0.42 * 0.9)
     *                      3  | 0.72          5  | 0.4             5  | (0.27 * 0.4)
     *                      5  | 0.27          7  | 0.9             7  | (0.24 * 0.9)
     *                      7  | 0.24
     */
    private static List<Tep> getTepListOfCombinedCup(Cup cup1, Cup cup2) {
        // Khởi tạo Map để lưu TID và xác suất tương ứng từ CUP thứ hai
        // (item trong CUP thứ hai nhưng không có trong CUP thứ nhất)
        Map<Integer, Double> tepListMap = new HashMap<>();
        List<Tep> tepListItemCup = cup2.getTepList();
        for (Tep tep : tepListItemCup) {
            tepListMap.put(tep.getTid(), tep.getProb());
        }
        // Lấy TEPList của CUP thứ nhất
        List<Tep> tepList1 = cup1.getTepList();
        // Tạo danh sách chứa TEP-List mới sau khi kết hợp
        List<Tep> newTepList = new ArrayList<>();
        // Duyệt qua TEPList của CUP thứ nhất
        for (Tep tep1 : tepList1) {
            // Lấy xác suất tương ứng từ Map đã tạo từ CUP thứ hai
            Double newProb = tepListMap.get(tep1.getTid());
            // Nếu xác suất tồn tại, tạo TEP mới và thêm vào danh sách
            if (newProb != null) {
                Tep newTep = new Tep(tep1.getTid(), tep1.getProb() * newProb);
                newTepList.add(newTep);
            }
        }
        return newTepList;
    }

    /**
     * Thực hiện thuật toán TUWFI (Top-k Uncertain Weighted Frequent Itemsets) Optimal Search trên CUP-List để tìm Top-k UWFIs
     * TuwfiOptimalSearch sử dụng Stack. Với mẫu i (itemset có kích thước i),
     * thuật toán sử dụng phần tử đầu tiên để kết hợp nó với các phần tử còn lại
     * trong mẫu i để tạo ra mẫu (i + 1) (itemset có kích thước i+1).
     * Việc kết hợp này được sử dụng cho đến khi tất cả các mẫu được xem xét
     * và có được Top-k Uncertain Weighted Frequent Itemsets
     *
     * @param result CUP-List đã được xác định là top-k item có kích thước 1 có CumWeightSup lớn nhất
     */
    public static void TuwfiOptimalSearch(List<Cup> result) {
        /*
         * Chiến lược nâng ngưỡng
         * Khởi tạo threshold = giá trị cumWeightSup của item cuối trong result
         * */
        double threshold = result.get(result.size() - 1).getCumWeightSup();

        // Sử dụng Stack để chứa CUP-List
        Stack<List<Cup>> stack = new Stack<>();
        stack.push(topKCupOneItem);

        while (!stack.isEmpty()) {
            // Nếu stack chưa rỗng thì lấy CUP-List trên đỉnh stack gán vào currentCupList
            // và xoá đi CUP-List trên đỉnh đã được lấy ra
            List<Cup> currentCupList = stack.pop();
            for (int i = 0; i < currentCupList.size() - 1; i++) {
                Cup currentCup = currentCupList.get(i);
                List<Cup> candidates = new ArrayList<>();

                for (int j = i + 1; j < currentCupList.size(); j++) {
                    Cup nextCup = currentCupList.get(j);

                    // Xác định item cuối cùng của CUP tiếp theo
                    List<String> itemsNextCup = Arrays.asList(nextCup.getPatternName().split(", "));
                    String lastItemNextCup = itemsNextCup.get(itemsNextCup.size()-1);

                    // Gọi hàm getCupByItemName để truy cập tới CUP trong CUP-List thông qua lastItemNextCup
                    Cup realNextCup = TuwfiOptimalDal.getCupByItemName(topKCupOneItem, lastItemNextCup);

                    // Nếu realNextCup không tìm thấy thì tiếp tục vòng lặp hiện tại.
                    if (realNextCup == null) {
                        continue;
                    }

                    // Tạo patternName mới cho CUP sau khi kết hợp
                    String mergedPatternName = currentCup.getPatternName() + ", " + lastItemNextCup;

                    // Tính trọng số mới cho CUP sau khi kết hợp
                    double sumWeight = 0;
                    int count = 0;
                    String[] mergedItemsNameList = mergedPatternName.split(", ");
                    for (String itemName : mergedItemsNameList) {
                        if (TuwfiOptimalDal.getWeightMap().containsKey(itemName)) {
                            count++;
                            sumWeight += TuwfiOptimalDal.getWeightMap().get(itemName);
                        }
                    }
                    double weight = sumWeight / count;

                    /*
                     * Chiến lược cắt tỉa dựa trên giá trị cumWeightSup được ước tính (Overestimated cumWeightSup)
                     * Nếu Overestimated cumWeightSup của mergedPatternName (cup chuẩn bị kết hợp)
                     * (= weight * cumWeightSup của CUP thứ nhất * calculateMaxProb của CUP thứ hai)
                     * nhỏ hơn ngưỡng threshold thì bỏ qua các lệnh phía sau và tiếp tục vòng lặp hiện tại.
                     * Ngược lại thì tiếp tục kết hợp 2 CUP
                     * */
                    if (weight * currentCup.getCumSup() * realNextCup.getMaxProb() < threshold) {
                        continue;
                    }

                    // Kết hợp hai CUP với nhau
                    Cup combinedCup = CombinedTwoCup(currentCup, realNextCup, weight);

                    // Cập nhật result
                    if (result.size() < topK || combinedCup.getCumWeightSup() > threshold) {
                        if (result.size() == topK) {
                            // xoá đi CUP cuối cùng của result để tạo khoảng trống
                            result.remove(result.size() - 1);
                        }
                        // Thêm vào result
                        result.add(combinedCup);
                        // Sắp xếp lại result theo thứ tự giảm dần của cumWeightSup
                        TuwfiOptimalDal.sortCupListByCumWeightSup(result);

                        // Tăng candidateCount để đếm số lượng candidates được tạo ra
                        candidateCount++;
                        // Mở rộng danh sách candidates
                        candidates.add(combinedCup);

                        /*
                         * Chiến lược nâng ngưỡng
                         * Đặt lại threshold = cumWeightSup của CUP item/itemset cuối cùng trong result
                         * */
                        threshold = result.get(result.size() - 1).getCumWeightSup();
                    }
                }
                // Nếu candidates không trống thì thêm candidates vào stack
                if (!candidates.isEmpty()) {
                    stack.push(candidates);
                }
            }
        }
    }

    /**
     * In số liệu thống kê về việc thực hiện thuật toán cuối cùng.
     */
    public static void printStats() {
        System.out.println("============= TOP-K UNCERTAIN WEIGHTED FREQUENT ITEMSETS (OPTIMAL) - STATS =============");
        System.out.println(" Top K = " + topK);
        System.out.println(" Items count from dataset: " + TuwfiOptimalDal.getItemsNameList().size());
        System.out.println(" Transactions count from dataset : " + TuwfiOptimalDal.getTransactionCount());
        System.out.println(" Number of candidates generated: " + candidateCount);
        System.out.println(" Maximum memory usage : " + MemoryLogger.getInstance().getMaxMemory() + " mb");
        System.out.println(" Total time ~ " + (timeEnd - timeStart) + " ms");
        System.out.println("===================================================");
    }

    /**
     * Print Top-k Uncertain Weighted Frequent Itemsets.
     *
     * @param result Top-k Uncertain Weighted Frequent Itemsets.
     */
    public static void printTuwfiOptimal(List<Cup> result) {
        System.out.println("============= PRINT TOP-K UNCERTAIN WEIGHTED FREQUENT ITEMSETS (OPTIMAL) =============");
        int no = 0;
        for (Cup cup : result) {
            no++;
            System.out.printf("\t\tNo: %d - Itemset: (%s) - Weight: %.3f - CumSup: %.3f - CumWeightSup: %.3f%n",
                    no, cup.getPatternName(), cup.getWeight(), cup.getCumSup(), cup.getCumWeightSup());
        }
        System.out.println();
    }

    /**
     * Ghi kết quả vào một tập tin được chỉ định.
     *
     * @param result Danh sách các đối tượng Cup chứa kết quả cần ghi.
     * @param resultFile Tên của tập tin mà kết quả sẽ được ghi vào.
     */
    public static void writeResultsToFile(List<Cup> result, String resultFile) {
        System.out.println("File " + resultFile + " is generating. . .");

        // Xác định thư mục nơi tập tin sẽ được lưu
        File dir = new File(System.getProperty("user.dir") + "/src/algorithms/tuwfioptimal/result");

        // Kiểm tra xem thư mục có tồn tại không, nếu không thì tạo mới
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                // In thông báo lỗi nếu việc tạo thư mục không thành công
                System.err.println("Failed to create directory for result files.");
                return; // Thoát khỏi phương thức
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dir, resultFile)))) {
            writer.write("============= PRINT TOP-K UNCERTAIN WEIGHTED FREQUENT ITEMSETS (OPTIMAL) =============\n");
            int no = 1;
            // Lặp lại danh sách các đối tượng Cup và ghi thông tin của chúng vào tệp
            for (Cup cup : result) {
                writer.write(String.format("\t\tNo: %d - Itemset: (%s) - Weight: %.3f - CumSup: %.3f - CumWeightSup: %.3f%n",
                        no, cup.getPatternName(), cup.getWeight(), cup.getCumSup(), cup.getCumWeightSup()));
                no++;
            }
            writer.write("\n");
            // Viết thông tin thống kê.
            writer.write("============= TOP-K UNCERTAIN WEIGHTED FREQUENT ITEMSETS (OPTIMAL) - STATS =============\n");
            writer.write(" Top K = " + topK + "\n");
            writer.write(" Items count from dataset: " + TuwfiOptimalDal.getItemsNameList().size() + "\n");
            writer.write(" Transactions count from dataset : " + TuwfiOptimalDal.getTransactionCount() + "\n");
            writer.write(" Number of candidates generated: " + candidateCount + "\n");
            writer.write(" Maximum memory usage : " + MemoryLogger.getInstance().getMaxMemory() + " mb\n");
            writer.write(" Total time ~ " + (timeEnd - timeStart) + " ms\n");
            writer.write("===================================================\n");
            System.out.println("Successfully wrote results to file: " + resultFile);
        } catch (IOException e) {
            System.err.println("Error writing results to file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}