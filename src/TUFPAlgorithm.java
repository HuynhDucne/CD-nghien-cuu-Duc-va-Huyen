import ca.pfv.spmf.tools.MemoryLogger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 *   TUFPAlgorithm: + List CUP-Lists                            -   Type: List<CUPList<T1, T2, T3>>
 *                  + Top k                                     -   Type: int
 *                  + Result Top-K UFP:                         -   Type: List<CUPList<T1, T2, T3>>
 *                  + thuộc tính dataset để chạy các hàm của lớp Dataset               -   Type: Dataset
 *                  + Hai biến đo thời gian chạy giải thuật     -   Type: long
 *                  + Biến đếm số lượng frequents itemsets      -   Type: int
 *                  + Biến đếm số lượng transaction             -   Type: int
 * */

public class TUFPAlgorithm<T1, T2, T3> {
    /** biến thời gian bắt đầu thực thi giải thuật */
    long timeStart = 0;

    /** biến thời gian kết thúc thực thi giải thuật */
    long timeEnd = 0;

    /** Số lượng frequents itemsets được tìm thấy */
    private int itemsetCount = 0;

    /** Số lượng transaction trong dataset */
    private int transactionCount = 0;

    /** Danh sách CUP-List có được từ dataset */
    private List<CUPList<T1, T2, T3>> cupLists = new ArrayList<>();

    /** Top k trong danh sách result */
    private int k;

    /** Danh sách Top-K Uncertain Frequent Pattern */
    private List<CUPList<T1, T2, T3>> result = new ArrayList<>();

    /** Dữ liệu từ dataset */
    private Dataset<T1, T3> dataset;

    public List<CUPList<T1, T2, T3>> getCupLists() {
        return cupLists;
    }

    public void setCupLists(List<CUPList<T1, T2, T3>> cupLists) {
        this.cupLists = cupLists;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public List<CUPList<T1, T2, T3>> getResult() {
        return result;
    }

    public void setResult(List<CUPList<T1, T2, T3>> result) {
        this.result = result;
    }

    public Dataset<T1, T3> getDataset() {
        return dataset;
    }

    public void setDataset(Dataset<T1, T3> dataset) {
        this.dataset = dataset;
    }

    public TUFPAlgorithm(int k) {
        this.k = k;
        this.dataset = new Dataset<>();
    }

    /**
     * Đọc file dataset_prob.txt đã được định dạng và chuyển thành cấu trúc dữ liệu CUP-Lists
     * */

    public void readDataAndConvertToCUPLists(String filePath) {
        List<List<T3>> probs = new ArrayList<>();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath));

            // Đọc dòng đầu tiên để lấy danh sách itemsName
            String[] itemsName = reader.readLine().split(" ");

            // Đọc từng dòng còn lại và chuẩn hoá dữ liệu thành List
            String line;
            while ((line = reader.readLine()) != null) {

                // Đếm số lượng transaction
                transactionCount++;

                // Lấy danh sách các prob trong 1 TID
                String[] probsStr = line.split(" ");

                List<T3> probsOfTID = new ArrayList<>();

                // i=1 để bỏ qua dữ liệu tid
                for (int i = 1; i < probsStr.length; i++) {
                    T3 item = (T3) Double.valueOf(probsStr[i]);
                    probsOfTID.add(item);
                }

                // gộp từng danh sách các prob trong mỗi TID lại thành 1 danh sách mới
                probs.add(probsOfTID);
                // => [[prob_Of_TID_1]  , [prob_Of_TID_2], [prob_Of_TID_3], ...]
                // => [[1.0,0,0.9,0.6,0,0,0,0], [0.9,0.9,0.7,0.6,0.4,0,0,0], [0,0.5,0.8,0.9,0,0.2,0.4,0], [], [],..]
            }

            // chuyển sang cấu trúc CUP-Lists
            int i = 0;
            // duyệt qua mỗi TID
            while (i < probs.get(0).size()) {
                List<TEPList<T2, T3>> tepList = new ArrayList<>();
                Integer tid = 1;
                Double probNum = 0.0;

                // Duyệt qua từng prob của 1 item
                for (List<T3> innerProbs : probs) {
                    T3 prob = innerProbs.get(i);
                    probNum = (Double) prob;

                    // Bỏ các TID có prob = 0
                    if (probNum != 0.0) {
                        tepList.add(new TEPList<>((T2) tid, prob));
                    }
                    tid++;
                }

                // Tạo đối tượng CUPList và thêm vào danh sách
                CUPList<T1, T2, T3> cupList = new CUPList<>((T1) itemsName[i], tepList);
                cupList.setExpSup(cupList.sumExpSup(cupList));
                cupList.setMaxProb(cupList.maxProb(cupList));
                cupLists.add(cupList);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Thực thi toàn bộ thuật toán TUFP
     * */
    public void executeTUFP(int k) {
        // reset statistics
        MemoryLogger.getInstance().reset(); // reset utility to check memory usage

        // check memory usage
        MemoryLogger.getInstance().checkMemory();

        // record the start time
        timeStart = System.currentTimeMillis();

        // Sắp xếp CUPList theo thứ tự giảm dần Existential Probability
        sortCUPListsByExpSup(cupLists);

        // Lấy Top-k trong CUP-Lists đưa vào result
        // Nếu k nhỏ hơn kích thước CUP-Lists mỗi item thì đưa top-k CUP-List vào result
        // ngược lại đưa toàn bộ CUP-Lists vào result
        if (k < cupLists.size()) {
            for (int i = 0; i < k; i++) {
                result.add(cupLists.get(i));
            }
        } else {
            result = new ArrayList<>(cupLists);
        }

        // Khởi tạo itemsets và gán result hiện tại vào itemsets để chứa các item kích thước 1 nằm trong top k
        // phục vụ cho việc duyệt qua các item để xử lý kết hợp CUP-List
        List<CUPList<T1, T2, T3>> itemsets = new ArrayList<>(result);

        // Thực hiện chiến lược chia để trị, gọi hàm TUFP_Search
        TUFP_Search(result, itemsets, k);

        // record the end time
        timeEnd = System.currentTimeMillis();
    }

    /**
     * Sắp xếp Cup-Lists theo thứ tự giảm dần ExpSup
     * */
    private static <T1, T2, T3> void sortCUPListsByExpSup(List<CUPList<T1, T2, T3>> cupLists) {
        // Ghi đè lại phương thức compare trong Comparator để so sánh các đối tượng CUPList với nhau
        cupLists.sort(new Comparator<>() {
            @Override
            public int compare(CUPList<T1, T2, T3> cupList1, CUPList<T1, T2, T3> cupList2) {
                return Double.compare((Double) cupList2.getExpSup(), (Double) cupList1.getExpSup());
            }
        });
    }

    /**
     * Chuẩn quá itemset name bằng cách loại bỏ cái ký tự thừa khi kết hợp
     * ví dụ: ABAD => ABD
     * */
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

    /** Kết hợp 2 CUP-Lists
    *
    * CUP-List C:       CUP-List D:   =>    CUP-List CD:
    * TID | Prob.       TID | Prob.         TID | Prob.
    *  1  | 0.9          1  | 0.6            1  | (0.9 * 0.6)
    *  2  | 0.7          2  | 0.6            2  | (0.7 * 0.6)
    *  3  | 0.8          3  | 0.9            3  | (0.8 * 0.9)
    *  4  | 0.9          5  | 0.3            5  | (0.9 * 0.3)
    *  5  | 0.9          6  | 0.9            7  | (0.4 * 0.6)
    *  7  | 0.4          7  | 0.6
    * */
    public static <T1, T2, T3> CUPList<T1, T2, T3> cupListXY(CUPList<T1, T2, T3> cupList1, CUPList<T1, T2, T3> cupList2) {
        List<TEPList<T2, T3>> tepList1 = cupList1.getTepList();
        List<TEPList<T2, T3>> tepList2 = cupList2.getTepList();
        List<TEPList<T2, T3>> newTepLists = new ArrayList<>();

        // Duyệt qua cả 2 TEP-List
        for (TEPList<T2, T3> t1 : tepList1) {
            for (TEPList<T2, T3> t2 : tepList2) {
                if (t1.getTid() == t2.getTid()) {
                    Double prob1 = Double.parseDouble(String.valueOf(t1.getProb()));
                    Double prob2 = Double.parseDouble(String.valueOf(t2.getProb()));
                    Double prob = (prob1 * prob2);
                    TEPList<T2, T3> newTepList = new TEPList<>(t1.getTid(), (T3) prob);
                    newTepLists.add(newTepList);

                    /* Nếu có 1 TID ở TEP-List 2 xuất hiện bằng với TID ở TEP-List 1 đang xét
                    * thì dừng xét các TID còn lại ở TEP-List 2
                    * */
                    break;
                }
            }
        }
        // dòng comment bên dưới dùng để tạo CUP-Lists mới cho ví dụ mẫu trong bài báo có item là chữ cái
//        String newItemsName = (String) (cupList1.getItemName()) + (String) cupList2.getItemName();
//        CUPList<T1, T2, T3> newCupList = new CUPList<>((T1) removeDuplicates(newItemsName), newTepLists);

        // khởi tạo list để chứa các item CUP-List khi kết hợp
        List<T1> newItemsName = Arrays.asList(cupList1.getItemName(), cupList2.getItemName());

        CUPList<T1, T2, T3> newCupList = new CUPList<>((T1) newItemsName, newTepLists);
        newCupList.setExpSup(newCupList.sumExpSup(newCupList));
        newCupList.setMaxProb(newCupList.maxProb(newCupList));

        return newCupList;
    }

    /**
    * TUFP_Search thực hiện chiến lược chia để trị. Với mẫu i (itemset có kích thước i),
    * thuật toán sử dụng phần tử đầu tiên để kết hợp nó với các phần tử còn lại
    * trong mẫu i để tạo ra mẫu (i + 1) (itemset có kích thước i+1).
    * Chiến lược này được sử dụng cho đến khi tất cả các mẫu được xem xét
     * */
    public  void TUFP_Search(List<CUPList<T1, T2, T3>> result, List<CUPList<T1, T2, T3>> itemsets, int k) {
        // set threshold = giá trị expSup của item cuối trong result
        double threshold = (Double) result.get(result.size() - 1).getExpSup();

        for (int i = 0; i < itemsets.size() - 1; i++) {

            CUPList<T1, T2, T3> currentCupList = itemsets.get(i);
            List<CUPList<T1, T2, T3>> newItemsets = new ArrayList<>();

            for (int j = i + 1; j < itemsets.size(); j++) {
                CUPList<T1, T2, T3> nextCupList = itemsets.get(j);

                /* Chiến Lược 2: Cắt tỉa
                * Nếu Overestimated expSup của mẫu hiện tại lớn hơn bằng ngưỡng threshold
                * thì tiếp tục, ngược lại kết hợp vs item tiếp theo
                * */
                if ((Double) currentCupList.getExpSup() * (Double) nextCupList.getMaxProb() >= threshold) {

                    // Kết hợp 2 CUP-Lists
                    CUPList<T1, T2, T3> cupListXY = cupListXY(currentCupList, nextCupList);

                    // Tăng biến đếm itemsetCount để đếm số lượng itemset (candidates) được tạo ra
                    itemsetCount++;

                    // Mở rộng mẫu newItemset
                    newItemsets.add(cupListXY);

                    // Nếu expSup > threshold
                    if ((Double) cupListXY.getExpSup() > threshold) {

                        // Nếu result.size() = k: Xóa phần tử cuối cùng của result
                        if (result.size() == k) {
                            result.remove(result.size() - 1);
                        }

                        // Thêm item mới vào result và itemsets
                        result.add(cupListXY);

                        // Sắp xếp lại result giảm dần theo expSup
                        sortCUPListsByExpSup(result);

                        /*
                        * Chiến lược 1: Nâng ngưỡng
                        * Đặt lại threshold = expSup của item cuối result
                        * */
                        threshold = (Double) result.get(result.size() - 1).getExpSup();
                    }
                }
            }

            // Nếu itemsets không trống thì tiếp tục gọi TUFP_Search
            if (!newItemsets.isEmpty()) {
                TUFP_Search(result, newItemsets, k);
            }
        }
    }

    /**
     * Print statistics about the last algorithm execution.
     */
    public void printStats() {
        System.out.println("=============  TOP-K UFP SPMF v.2.10 - STATS =============");
        System.out.println(" Transactions count from database : " + transactionCount);
        System.out.println(" Frequent itemsets count (candidate): " + itemsetCount);
        System.out.println(" Memory : " + MemoryLogger.getInstance().getMaxMemory() + " mb");
        System.out.println(" Total time : " + (timeEnd - timeStart) + " ms");
        System.out.println("===================================================");
    }

    /**
     * Print Top-k Uncertain Frequent Pattern
     * */
    public void printTopKUFP() {
        System.out.println("In Top-k UFP:");
        for (CUPList<T1, T2, T3> cupList : result) {
            System.out.println("\t\t\t" + cupList.getItemName() + ": " + cupList.getExpSup());
        }
        System.out.println();
    }
}