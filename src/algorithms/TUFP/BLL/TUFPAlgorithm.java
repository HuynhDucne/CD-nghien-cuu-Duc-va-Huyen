package algorithms.TUFP.BLL;

import algorithms.TUFP.DAL.TUFPAlgorithmDAL;
import ca.pfv.spmf.tools.MemoryLogger;

import java.util.*;

/**
 * TUFPAlgorithm:
 * + Hai biến đo thời gian chạy giải thuật     -   Type: long
 * + Biến đếm số lượng frequents itemsets      -   Type: int
 */

public class TUFPAlgorithm {
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
        cupLists.sort(Comparator.comparingDouble(CUPList::getExpSup).reversed());
    }

    /**
     * Chuẩn quá itemset name bằng cách loại bỏ cái ký tự thừa khi kết hợp
     *
     * @param str chuỗi các ký tự
     * @return phương thức trả về một chuỗi đã loại bỏ các ký tự trùng lặp
     * ví dụ: ABAD => ABD
     */
    public static String removeDuplicates(String str) {
        char[] chars = str.toCharArray();
        String result = "";
        for (int i = 0; i < chars.length; i++) {

            // Nếu ký tự nào khác khoảng trắng thì thêm vào result
            if (chars[i] != ' ') {
                result += chars[i];
                for (int j = i + 1; j < chars.length; j++) {

                    // Nếu ký tự nào trùng lặp thì gán ký tự trùng lặp đó bằng khoảng trắng
                    if (chars[j] == chars[i]) {
                        chars[j] = ' ';
                    }
                }
            }
        }
        return result;
    }

    /**
     * Kết hợp hai CUP-Lists với nhau
     *
     * @param cupList1 đại diện cho CUP-List của item/itemset thứ nhất
     * @param cupList2 đại diện cho CUP-List của item/itemset thứ hai
     * @return phương thức trả về một CUP-List mới được kết hợp từ item/itemset thứ nhất và item/itemset thứ hai
     *
     * Ví dụ kết hợp CUP-List C và D:
     *                       CUP-List C:       CUP-List D:   =>    CUP-List CD:
     *                       TID | Prob.       TID | Prob.         TID | Prob.
     *                       1  | 0.9          1  | 0.6            1  | (0.9 * 0.6)
     *                       2  | 0.7          2  | 0.6            2  | (0.7 * 0.6)
     *                       3  | 0.8          3  | 0.9            3  | (0.8 * 0.9)
     *                       4  | 0.9          5  | 0.3            5  | (0.9 * 0.3)
     *                       5  | 0.9          6  | 0.9            7  | (0.4 * 0.6)
     *                       7  | 0.4          7  | 0.6
     */
    public static CUPList cupListXY(CUPList cupList1, CUPList cupList2) {
        // Lấy danh sách TEPList từ cupList1 và cupList2
        List<TEPList> tepList1 = cupList1.getTepList();
        List<TEPList> tepList2 = cupList2.getTepList();

        // Tạo một Map để lưu trữ TEPList của cupList2 theo TID để tìm kiếm nhanh
        Map<Integer, TEPList> tepList2Map = new HashMap<>();
        for (TEPList t2 : tepList2) {
            tepList2Map.put(t2.getTid(), t2);
        }

        List<TEPList> newTepLists = new ArrayList<>();

        // Duyệt qua TEP-List của cupList1
        for (TEPList t1 : tepList1) {
            // Tìm kiếm TEP có TID tương ứng trong tepList2Map bằng cách gán vào TEPList t2
            TEPList t2 = tepList2Map.get(t1.getTid());

            // Nếu t2 khác null là tìm thấy, ngược lại không tìm thấy
            if (t2 != null) {
                // Nếu tìm thấy, thực hiện tính toán và thêm vào newTepLists
                double prob = t1.getProb() * t2.getProb();
                TEPList newTepList = new TEPList(t1.getTid(), prob);
                newTepLists.add(newTepList);
            }
        }

        // Khởi tạo CUPList mới với newTepLists
        List<String> newItemsName = Arrays.asList(cupList1.getItemName(), cupList2.getItemName());
        CUPList newCupList = new CUPList(newItemsName.toString(), newTepLists);

        // Tính toán và thiết lập ExpSup và MaxProb cho newCupList
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
     * @param result   danh sách top-k UFPs cuối cùng
     * @param itemsets danh sách các CUP-List phục vụ cho việc duyệt qua và kết hợp tạo CUP-List mới
     * @param k        top k
     */
//    public static void TUFP_Search(List<CUPList> result, List<CUPList> itemsets, int k) {
//        // Khởi tạo threshold = giá trị expSup của item cuối trong result
//        double threshold = result.get(result.size() - 1).getExpSup();
//        int sizeItemsets = itemsets.size();
//        for (int i = 0; i < sizeItemsets - 1; i++) {
//            CUPList currentCupList = itemsets.get(i);
//            List<CUPList> itemsetList = new LinkedList<>();
//
//            for (int j = i + 1; j < sizeItemsets; j++) {
//                CUPList nextCupList = itemsets.get(j);
//
//                /* Chiến Lược 2: Cắt tỉa
//                 * Nếu Overestimated expSup của mẫu hiện tại lớn hơn bằng ngưỡng threshold
//                 * thì tiếp tục, ngược lại kết hợp vs item tiếp theo
//                 * */
//                if (currentCupList.getExpSup() * nextCupList.getMaxProb() < threshold) {
//                    break;
//                }
//                // Kết hợp 2 CUP-Lists
//                CUPList cupListXY = cupListXY(currentCupList, nextCupList);
//
//                // Tăng biến đếm itemsetCount để đếm số lượng itemset (candidates) được tạo ra
//                itemsetCount++;
//
//                // Mở rộng mẫu newItemset
//                itemsetList.add(cupListXY);
//
//                // Nếu expSup > threshold
//                if (cupListXY.getExpSup() > threshold) {
//
//                    // Nếu result.size() = k: Xóa phần tử cuối cùng của result
//                    if (result.size() == k) {
//                        result.remove(result.size() - 1);
//                    }
//
//                    // Thêm item mới vào result và itemsets
//                    result.add(cupListXY);
//
//                    // Sắp xếp lại result giảm dần theo expSup
//                    sortCUPListsByExpSup(result);
//
//                    /*
//                     * Chiến lược 1: Nâng ngưỡng
//                     * Đặt lại threshold = expSup của item cuối result
//                     * */
//                    threshold = result.get(result.size() - 1).getExpSup();
//                }
//            }
//            // Nếu itemsets không trống thì tiếp tục gọi TUFP_Search
//            if (!itemsetList.isEmpty()) {
//                TUFP_Search(result, itemsetList, k);
//            }
//        }
//    }


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
        CUPList[] itemsetsArray = itemsets.toArray(new CUPList[0]);

        // Sắp xếp các tập mục theo thứ tự giảm dần Overestimated expSup (expSup của CUPList này * maxProb của CUPList khác)
        Arrays.sort(itemsetsArray, (a, b) -> Double.compare(b.getExpSup() * b.getMaxProb(), a.getExpSup() * a.getMaxProb()));

        // Sử dụng Stack thay thế đệ quy
        // Stack sẽ chứa các danh sách CUP-Lists
        Stack<List<CUPList>> stack = new Stack<>();
        stack.push(Arrays.asList(itemsetsArray));

        while (!stack.isEmpty()) {
            // Nếu stack chưa rỗng thì lấy list CUP-Lists trên đỉnh stack gán vào currentList
            // và xoá đi list CUP-Lists trên đỉnh đã được lấy ra
            List<CUPList> currentList = stack.pop();

            for (int i = 0; i < currentList.size() - 1; i++) {
                CUPList currentCupList = currentList.get(i);
                List<CUPList> itemsetList = new ArrayList<>();

                for (int j = i + 1; j < currentList.size(); j++) {
                    CUPList nextCupList = currentList.get(j);

                    /* Chiến Lược 2: Cắt tỉa
                     * Nếu Overestimated expSup của itemset hiện tại (expSup của CUPList này * maxProb của CUPList khác)
                     * nhỏ ngưỡng threshold thì kết thúc vòng lặp hiện tại. Ngược lại thì tiếp tục kết hợp 2 CUP-List
                     * */
                    if (currentCupList.getExpSup() * nextCupList.getMaxProb() < threshold) {
                        break;
                    }

                    // Kết hợp 2 CUP-Lists
                    CUPList cupListXY = cupListXY(currentCupList, nextCupList);

                    // Tăng itemsetCount để đếm số lượng itemset (candidates) được tạo
                    itemsetCount++;

                    // Mở rộng danh sách itemsetList
                    itemsetList.add(cupListXY);

                    // Nếu expSup > threshold
                    if (cupListXY.getExpSup() > threshold) {
                        // Nếu kích thước result đang bằng k thì xoá đi CUPList cuối cùng của result để tạo khoảng trống
                        if (result.size() == k) {
                            result.remove(result.size() - 1);
                        }

                        // Thêm CUP-List item/itemset mới vào result
                        result.add(cupListXY);

                        // Sắp xếp lại result theo thứ tự giảm dần của expSup
                        sortCUPListsByExpSup(result);

                        /*
                         * Chiến lược 1: Nâng ngưỡng
                         * Đặt lại threshold = expSup của CUP-List item/itemset cuối trong result
                         * */
                        threshold = result.get(result.size() - 1).getExpSup();
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
        System.out.println(" Transactions count from database : " + TUFPAlgorithmDAL.getTransactionCount());
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
        System.out.println("In Top-k UFP:");
        for (CUPList cupList : result) {
            System.out.println("\t\t\t" + cupList.getItemName() + ": " + Math.round(cupList.getExpSup() * 100.0) / 100.0);
        }
        System.out.println();
    }
}