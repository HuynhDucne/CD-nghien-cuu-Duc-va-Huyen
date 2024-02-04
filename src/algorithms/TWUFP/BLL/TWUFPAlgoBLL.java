package algorithms.TWUFP.BLL;

import algorithms.TWUFP.DAL.TWUFPAlgoDAL;
import ca.pfv.spmf.tools.MemoryLogger;

import java.util.*;

/**
 * TWUFPAlgorithm - Top k Weighted Uncertain Frequent Pattern:
 * + Hai biến đo thời gian chạy giải thuật     -   Type: long
 * + Biến đếm số lượng frequents itemsets      -   Type: int
 */

public class TWUFPAlgoBLL {
    /**
     * biến thời gian bắt đầu thực thi giải thuật
     */
    static long timeStart = 0;

    /**
     * biến thời gian kết thúc thực thi giải thuật
     */
    static long timeEnd = 0;

    /**
     * Số lượng frequents itemsets được tìm thấy
     */
    static int itemsetCount = 0;

    /**
     * Thực thi toàn bộ thuật toán TWUFP
     *
     * @param cupLists danh sách top k CUP-Lists có cumulative support value (expSup) lớn nhất
     * @param k top k
     * @return trả về một danh sách CUP-Lists chứa top k uncertain frequent patterns
     */
    public static List<CUPList> executeTWUFP(List<CUPList> cupLists, int k) {

        System.out.println("TWUFP Algorithm is running...");

        // reset utility to check memory usage
        MemoryLogger.getInstance().reset();

        // check memory usage
        MemoryLogger.getInstance().checkMemory();

        // record the start time
        timeStart = System.currentTimeMillis();

        // Khởi tạo list CUP-Lists để chưa kết quả top k cuối cùng
        List<CUPList> result = new ArrayList<>(cupLists);

        // Thực hiện chiến lược chia để trị, gọi hàm TWUFP_Search
        TWUFP_Search(result, cupLists, k);

        // record the end time
        timeEnd = System.currentTimeMillis();

        return result;
    }

    /**
     * Sắp xếp danh sách Cup-Lists theo thứ tự giảm dần ExpSup
     *
     * @param cupLists danh sách CUP-Lists chứa Top k uncertain frequent pattern
     */
    public static void sortCUPListsByExpWeightSup(List<CUPList> cupLists) {
        // Sử dụng phương thức sort của Java Collection Framework
        // Comparator.comparingDouble(CUPList::getExpWeightSup).reversed() tạo ra một Comparator
        // so sánh các CUP-Lists dựa trên giá trị ExpWeightSup của chúng
        cupLists.sort(Comparator.comparingDouble(CUPList::getExpWeightSup).reversed());
    }

    /**
     * Kết hợp hai CUP-Lists với nhau
     *
     * @param cupList1 đại diện cho CUP-List của item/itemset thứ nhất
     * @param itemCupListNotInCupList1 đại diện cho CUP-List của item không có trong CUP-List thứ nhất
     * @param weight trọng số của CUP-List đã kết hợp
     * @return phương thức trả về một CUP-List mới được kết hợp từ item/itemset thứ nhất và item thứ hai
     * Ví dụ kết hợp CUP-List C và D:
     *                       CUP-List C:       CUP-List D:   =>    CUP-List CD:
     *                       TID | Prob.       TID | Prob.         TID | Prob.
     *                       1  | 0.9          1  | 0.6            1  | (0.9 * 0.6)
     *                       2  | 0.7          2  | 0.6            2  | (0.7 * 0.6)
     *                       3  | 0.8          3  | 0.9            3  | (0.8 * 0.9)
     *                       4  | 0.9          5  | 0.3            5  | (0.9 * 0.3)
     *                       5  | 0.9          6  | 0.9            7  | (0.4 * 0.6)
     *                       7  | 0.4          7  | 0.6
     *
     * Ví dụ kết hợp CUP-List CD và CA:
     *                      CUP-List CD:       CUP-List A:    =>    CUP-List CDA:
     *                      TID | Prob.       TID | Prob.          TID | Prob.
     *                      1  | 0.54          1  | 1.0             1  | (0.54 * 1.0)
     *                      2  | 0.42          2  | 0.9             2  | (0.42 * 0.9)
     *                      3  | 0.72          5  | 0.4             5  | (0.27 * 0.4)
     *                      5  | 0.27          7  | 0.9             7  | (0.24 * 0.9)
     *                      7  | 0.24
     */
    public static CUPList cupListXY(CUPList cupList1, CUPList itemCupListNotInCupList1, double weight) {

        // Tạo tên mới cho CUP-List kết hợp bằng cách nối tên của hai CUP-List
        String mergedItemsName = cupList1.getItemName() + ", " + itemCupListNotInCupList1.getItemName();
        String newItemsName = String.join(", ", mergedItemsName);

        // Khởi tạo CUP-List mới với tên đã được kết hợp
        CUPList newCupList = new CUPList(newItemsName);

        // Khởi tạo Map để lưu TID và xác suất tương ứng từ CUP-List thứ hai (item trong CUP-List thứ hai nhưng chưa có
        // trong CUP-List thứ nhất)
        Map<Integer, Double> tepListMap = new HashMap<>();
        List<TEPList> tepListItemCup = itemCupListNotInCupList1.getTepList();
        for (TEPList tep : tepListItemCup) {
            tepListMap.put(tep.getTid(), tep.getProb());
        }

        // Duyệt qua danh sách TEPList của CUP-List thứ nhất
        List<TEPList> tepList1 = cupList1.getTepList();
        // Tạo danh sách chứa TEP-List mới sau khi kết hợp
        List<TEPList> newTepLists = new ArrayList<>();
        // Duyệt qua TEP-List của cupList1
        for (TEPList t1 : tepList1) {
            // Lấy xác suất tương ứng từ Map đã tạo ở trên
            Double probNew = tepListMap.get(t1.getTid());
            // Nếu xác suất tồn tại, tạo TEPList mới và thêm vào danh sách
            if (probNew != null) {
                TEPList newTepList = new TEPList(t1.getTid(), t1.getProb() * probNew);
                newTepLists.add(newTepList);
            }
        }
        // Cập nhật các thuộc tính của CUP-List mới như TEP-List, ExpSup, MaxProb, weight, ExpWeightSup
        newCupList.setTepList(newTepLists);
        newCupList.setExpSup(newCupList.sumExpSup(newCupList));
        newCupList.setMaxProb(newCupList.maxProb(newCupList));
        newCupList.setWeight(weight);
        newCupList.setExpWeightSup(weight * newCupList.getExpSup());

        // Trả về CUP-List mới
        return newCupList;
    }


    /**
     * Thực hiện thuật toán TWUFP (Top-k Weighted Uncertain Frequent Pattern) Search trên một danh sách CUP-Lists để tìm top-k itemsets.
     *
     * TWUFP_Search thực hiện chiến lược chia để trị. Với mẫu i (itemset có kích thước i),
     * thuật toán sử dụng phần tử đầu tiên để kết hợp nó với các phần tử còn lại
     * trong mẫu i để tạo ra mẫu (i + 1) (itemset có kích thước i+1).
     * Chiến lược này được sử dụng cho đến khi tất cả các mẫu được xem xét
     *
     * @param result Danh sách chứa các CUP-Lists đã được xác định là top-k itemsets
     * @param itemsets Danh sách chứa các CUP-Lists ban đầu để khởi tạo thuật toán
     * @param k Số lượng itemsets (top-k) cần tìm
     */
    public static void TWUFP_Search(List<CUPList> result, List<CUPList> itemsets, int k) {
        // Khởi tạo threshold = giá trị expSup của item cuối trong result
        double threshold = result.get(result.size() - 1).getExpWeightSup();

        // Sử dụng Stack thay thế đệ quy
        // Stack sẽ chứa các danh sách CUP-Lists
        Stack<List<CUPList>> stack = new Stack<>();
        stack.push(itemsets);

        while (!stack.isEmpty()) {
            // Nếu stack chưa rỗng thì lấy list CUP-Lists trên đỉnh stack gán vào currentList
            // và xoá đi list CUP-Lists trên đỉnh đã được lấy ra
            List<CUPList> currentList = stack.pop();
            for (int i = 0; i < currentList.size() - 1; i++) {
                CUPList currentCupList = currentList.get(i);
                List<CUPList> itemsetList = new ArrayList<>();

                for (int j = i + 1; j < currentList.size(); j++) {
                    CUPList nextCupList = currentList.get(j);

                    // Xác định item cuối cùng của CUPList tiếp theo
                    List<String> itemsListNextCupList = Arrays.asList(nextCupList.getItemName().split(", "));
                    String itemNextCupList = itemsListNextCupList.get(itemsListNextCupList.size()-1);

                    // Lấy CUPList tương ứng với item cuối cùng
                    CUPList itemCupListNotInCupList1 = TWUFPAlgoDAL.getCUPListByItemName(itemsets, itemNextCupList);
//                    System.out.println(currentCupList.getItemName()+itemsListNextCupList.toString()+itemCupListNotInCupList1.getMaxProb());

                    // Tạo danh sách các items sau khi kết hợp CUP-Lists
                    String mergedItemsName = currentCupList.getItemName() + ", " + itemNextCupList;
                    String newItemsName = String.join(", ", mergedItemsName);

                    // Tính trọng số mới cho CUPList được kết hợp
                    double sumWeight = 0;
                    int count = 0;
                    List<String> mergedItemsNameList = Arrays.asList(newItemsName.split(", "));
                    for (String itemName : mergedItemsNameList) {
                        if (TWUFPAlgoDAL.weightMap.containsKey(itemName)) {
                            count++;
                            sumWeight += TWUFPAlgoDAL.weightMap.get(itemName);
                        }
                    }
                    double weight = sumWeight / count;

                    /* Chiến Lược 2: Cắt tỉa
                     * Nếu Overestimated expWeightSup của itemset hiện tại (weight * expSup của CUPList này * maxProb của CUPList khác)
                     * nhỏ ngưỡng threshold thì kết thúc vòng lặp hiện tại. Ngược lại thì tiếp tục kết hợp 2 CUP-List
                     * */
                    if (weight * currentCupList.getExpSup() * itemCupListNotInCupList1.getMaxProb() < threshold) {
                        break;
                    }

                    // Kết hợp 2 CUP-Lists
                    CUPList cupListXY = cupListXY(currentCupList, itemCupListNotInCupList1, weight);

                    // Tăng itemsetCount để đếm số lượng itemset (candidates) được tạo
                    itemsetCount++;

                    // Mở rộng danh sách itemsetList
                    itemsetList.add(cupListXY);

                    // Nếu kích thước result đang nhỏ hơn k
                    if (result.size() < k) {
                        // Thêm vào result
                        result.add(cupListXY);
                        // Sắp xếp lại result theo thứ tự giảm dần của expWeightSup
                        sortCUPListsByExpWeightSup(result);

                        /*
                         * Chiến lược 1: Nâng ngưỡng
                         * Đặt lại threshold = expWeightSup của CUP-List item/itemset cuối cùng trong result
                         * */
                        threshold = result.get(result.size() - 1).getExpWeightSup();
                    } else {
                        // Nếu kích thước result không nhỏ hơn k và
                        // nếu expWeightSup của CUP-List vừa kết hợp không nhỏ hơn threshold
                        if (cupListXY.getExpWeightSup() > threshold) {
                            // xoá đi CUPList cuối cùng của result để tạo khoảng trống
                            result.remove(result.size() - 1);
                            // thêm CUP-List vừa kết hợp vào result
                            result.add(cupListXY);

                            // Sắp xếp lại result theo thứ tự giảm dần của expWeightSup
                            sortCUPListsByExpWeightSup(result);

                            /*
                             * Chiến lược 1: Nâng ngưỡng
                             * Đặt lại threshold = expWeightSup của CUP-List item/itemset cuối cùng trong result
                             * */
                            threshold = result.get(result.size() - 1).getExpWeightSup();
                        }
                    }
                }
                // Nếu itemsetList không trống thì thêm itemsetList vào stack
                if (!itemsetList.isEmpty()) {
                    stack.push(itemsetList);
                }
            }
        }
    }

    /**
     * Print statistics about the last algorithm execution.
     */
    public static void printStats() {
        System.out.println("=============  TOP-K WEIGHTED UFP SPMF v.2.10 - STATS =============");
        System.out.println(" Transactions count from database : " + TWUFPAlgoDAL.getTransactionCount());
        System.out.println(" Frequent itemsets count (candidate): " + itemsetCount);
        System.out.println(" Memory : " + MemoryLogger.getInstance().getMaxMemory() + " mb");
        System.out.println(" Total time : " + (timeEnd - timeStart) + " ms");
        System.out.println("===================================================");
    }

    /**
     * Print Top-k Weighted Uncertain Frequent Pattern
     *
     * @param result Top-k Weighted Uncertain Frequent Pattern
     */
    public static void printTopKWUFP(List<CUPList> result) {
        System.out.println("Print Top-k Weighted UFP:");
        for (CUPList cupList : result) {
            System.out.println("\t\t\t" + cupList.getItemName() + ": " + Math.round(cupList.getExpWeightSup() * 1000.0) / 1000.0);
        }
        System.out.println();
    }
}