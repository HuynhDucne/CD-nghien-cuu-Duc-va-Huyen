package algorithms.TUFP.BLL;

import algorithms.TUFP.DAL.TUFPAlgoDAL;
import ca.pfv.spmf.tools.MemoryLogger;

import java.util.*;

/**
 * TUFPAlgorithm:
 * + Hai biến đo thời gian chạy giải thuật     -   Type: long
 * + Biến đếm số lượng frequents itemsets      -   Type: int
 */

public class TUFPAlgoBLL {
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
     * Thực thi toàn bộ thuật toán TUFP
     *
     * @param cupLists danh sách top k CUP-Lists có cumulative support value (expSup) lớn nhất
     * @param k top k
     * @return trả về một danh sách CUP-Lists chứa top k uncertain frequent patterns
     */
    public static List<CUPList> executeTUFP(List<CUPList> cupLists, int k) {

        System.out.println("TUFP Algorithm is running...");

        // reset utility to check memory usage
        MemoryLogger.getInstance().reset();

        // check memory usage
        MemoryLogger.getInstance().checkMemory();

        // record the start time
        timeStart = System.currentTimeMillis();

        // Khởi tạo list CUP-Lists để chưa kết quả cuối cùng
        List<CUPList> result = new ArrayList<>(cupLists);

        // Thực hiện chiến lược chia để trị, gọi hàm TUFP_Search
        TUFP_Search(result, cupLists, k);

        // record the end time
        timeEnd = System.currentTimeMillis();

        return result;
    }

    /**
     * Sắp xếp Cup-Lists theo thứ tự giảm dần ExpSup
     *
     * @param cupLists danh sách CUP-Lists chứa Top k uncertain frequent pattern
     */
    public static void sortCUPListsByExpSup(List<CUPList> cupLists) {
        // Sử dụng phương thức sort của Java Collection Framework
        // Comparator.comparingDouble(CUPList::getExpSup).reversed() tạo ra một Comparator
        // so sánh các CUP-Lists dựa trên giá trị ExpSup của chúng
        cupLists.sort(Comparator.comparingDouble(CUPList::getExpSup).reversed());
    }

    /**
     * Kết hợp hai CUP-Lists với nhau
     *
     * @param cupList1 đại diện cho CUP-List của item/itemset thứ nhất
     * @param itemCupListNotInCupList1 đại diện cho CUP-List của item không có trong CUP-List thứ nhất
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
    public static CUPList cupListXY(CUPList cupList1, CUPList itemCupListNotInCupList1) {
        // Khởi tạo set chứa các item khi kết hợp 2 CUP-List
//        Set<String> mergedItemsNameSet = new LinkedHashSet<>();
//
//        // Khởi tạo list chứa các item của mỗi CUP-List
//        List<String> itemsCupList1 = Arrays.asList(cupList1.getItemName().split(", "));
//        List<String> itemsCupList2 = Arrays.asList(cupList2.getItemName().split(", "));
//
//        mergedItemsNameSet.addAll(itemsCupList1);
//        mergedItemsNameSet.addAll(itemsCupList2);

        // Tạo tên mới cho CUP-List kết hợp bằng cách nối tên của hai CUP-List
        String newItemsName = String.join(", ", cupList1.getItemName(), itemCupListNotInCupList1.getItemName());

        CUPList newCupList = new CUPList(newItemsName);

        // Tạo một Map để lưu trữ TEPList của item chưa có trong CUP-List1 theo TID để tìm kiếm nhanh
        Map<Integer, Double> tepListMap = new HashMap<>();
        List<TEPList> tepListItemCup = itemCupListNotInCupList1.getTepList();
        for (TEPList tep : tepListItemCup) {
            tepListMap.put(tep.getTid(), tep.getProb());
        }

        // Lấy danh sách TEPList từ cupList1 và cupList2
        List<TEPList> tepList1 = cupList1.getTepList();
        // Tạo danh sách chứa TEP-List mới sau khi kết hợp
        List<TEPList> newTepLists = new ArrayList<>();
        // Duyệt qua TEP-List của cupList1
        for (TEPList t1 : tepList1) {
            // Tìm kiếm TEP có TID tương ứng trong tepListMap bằng cách gán vào TEPList t2
            Double probNew = tepListMap.get(t1.getTid());
            // Nếu t2 khác null là tìm thấy, ngược lại không tìm thấy
            if (probNew != null) {
                // Nếu tìm thấy, thực hiện tính toán và thêm vào newTepLists
                TEPList newTepList = new TEPList(t1.getTid(), t1.getProb() * probNew);
                newTepLists.add(newTepList);
            }
        }

        // Tính toán và thiết lập ExpSup và MaxProb cho newCupList
        newCupList.setTepList(newTepLists);
        newCupList.setExpSup(newCupList.sumExpSup(newCupList));
        newCupList.setMaxProb(newCupList.maxProb(newCupList));

        return newCupList;
    }

    /**
     * TUFP_Search thực hiện chiến lược chia để trị. Với mẫu i (itemset có kích thước i),
     * thuật toán sử dụng phần tử đầu tiên để kết hợp nó với các phần tử còn lại
     * trong mẫu i để tạo ra mẫu (i + 1) (itemset có kích thước i+1).
     * Chiến lược này được sử dụng cho đến khi tất cả các mẫu được xem xét
     *
     * @param result danh sách top-k CUP-Lists cuối cùng
     * @param itemsets danh sách chưa các item/itemset tạo ra
     * @param k top k
     */
    public static void TUFP_Search(List<CUPList> result, List<CUPList> itemsets, int k) {
        // Khởi tạo threshold = giá trị expSup của item cuối trong result
        double threshold = result.get(result.size() - 1).getExpSup();

        // Chuyển đổi các item thành mảng để truy cập nhanh hơn
//        CUPList[] itemsetsArray = itemsets.toArray(new CUPList[0]);

        // Sắp xếp các tập mục theo thứ tự giảm dần Overestimated expSup (expSup của CUPList này * maxProb của CUPList khác)
//        Arrays.sort(itemsetsArray, (a, b) -> Double.compare(b.getExpSup() * b.getMaxProb(), a.getExpSup() * a.getMaxProb()));

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

                    List<String> itemsListNextCupList = Arrays.asList(nextCupList.getItemName().split(", "));
                    String itemNextCupList = itemsListNextCupList.get(itemsListNextCupList.size()-1);
                    CUPList itemCupListNotInCupList1 = TUFPAlgoDAL.getCUPListByItemName(itemsets, itemNextCupList);
//                    System.out.println(currentCupList.getItemName()+itemsListNextCupList.toString()+itemCupListNotInCupList1.getMaxProb());

                    /* Chiến Lược 2: Cắt tỉa
                     * Nếu Overestimated expSup của itemset hiện tại (expSup của CUPList này * maxProb của CUPList khác)
                     * nhỏ ngưỡng threshold thì kết thúc vòng lặp hiện tại. Ngược lại thì tiếp tục kết hợp 2 CUP-List
                     * */
                    if (currentCupList.getExpSup() * itemCupListNotInCupList1.getMaxProb() < threshold) {
                        break;
                    }

                    // Kết hợp 2 CUP-Lists
                    CUPList cupListXY = cupListXY(currentCupList, itemCupListNotInCupList1);

                    // Tăng itemsetCount để đếm số lượng itemset (candidates) được tạo
                    itemsetCount++;

                    // Mở rộng danh sách itemsetList
                    itemsetList.add(cupListXY);

                    // Nếu kích thước result đang nhỏ hơn k
                    if (result.size() < k) {
                        // Thêm vào result
                        result.add(cupListXY);
                        // Sắp xếp lại result theo thứ tự giảm dần của expSup
                        sortCUPListsByExpSup(result);

                        /*
                         * Chiến lược 1: Nâng ngưỡng
                         * Đặt lại threshold = expSup của CUP-List item/itemset cuối cùng trong result
                         * */
                        threshold = result.get(result.size() - 1).getExpSup();
                    } else {
                        // Nếu kích thước result không nhỏ hơn k và
                        // nếu expSup của CUP-List vừa kết hợp không nhỏ hơn threshold
                        if (cupListXY.getExpSup() > threshold) {
                            // xoá đi CUPList cuối cùng của result để tạo khoảng trống
                            result.remove(result.size() - 1);
                            // thêm CUP-List vừa kết hợp vào result
                            result.add(cupListXY);

                            // Sắp xếp lại result theo thứ tự giảm dần của expSup
                            sortCUPListsByExpSup(result);

                            /*
                             * Chiến lược 1: Nâng ngưỡng
                             * Đặt lại threshold = expSup của CUP-List item/itemset cuối cùng trong result
                             * */
                            threshold = result.get(result.size() - 1).getExpSup();
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
        System.out.println("=============  TOP-K UFP SPMF v.2.10 - STATS =============");
        System.out.println(" Transactions count from database : " + TUFPAlgoDAL.getTransactionCount());
        System.out.println(" Frequent itemsets count (candidate): " + itemsetCount);
        System.out.println(" Memory : " + MemoryLogger.getInstance().getMaxMemory() + " mb");
        System.out.println(" Total time : " + (timeEnd - timeStart) + " ms");
        System.out.println("===================================================");
    }

    /**
     * Print Top-k Uncertain Frequent Pattern
     *
     * @param result Top-k Uncertain Frequent Pattern
     */
    public static void printTopKUFP(List<CUPList> result) {
        System.out.println("Print Top-k UFP:");
        for (CUPList cupList : result) {
            System.out.println("\t\t\t" + cupList.getItemName() + ": " + Math.round(cupList.getExpSup() * 100.0) / 100.0);
        }
        System.out.println();
    }
}