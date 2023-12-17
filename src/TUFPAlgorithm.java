import ca.pfv.spmf.tools.MemoryLogger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 *   TUFPAlgorithm: + List CUP-Lists        -   Type: T1
 *                  + Top k                 -   Type: T3
 *                  + Result Top-K UFP:     -   Type: T2
 * */

public class TUFPAlgorithm<T1, T2, T3> {
    /** start time of latest execution */
    long timeStart = 0;

    /**  end time of latest execution */
    long timeEnd = 0;
    private List<CUPList<T1, T2, T3>> cupLists;
    private int k;
    private List<CUPList<T1, T2, T3>> result;

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

    public TUFPAlgorithm(List<CUPList<T1, T2, T3>> cupLists, int k) {
        this.cupLists = cupLists;
        this.k = k;
        this.result = new ArrayList<CUPList<T1, T2, T3>>();
    }

    public List<List<T3>> readData(String filePath) {
        List<CUPList<T1, T2, T3>> cupLists = new ArrayList<>();
        List<List<T3>> probsList = new ArrayList<>();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath));

            // Đọc dòng đầu tiên để lấy danh sách itemsName
            String[] itemsList = reader.readLine().split(",");

            // Đọc từng dòng còn lại và chuẩn hoá dữ liệu thành List
            String line;
            while ((line = reader.readLine()) != null) {

                // Lấy danh sách các prob trong 1 TID
                String[] probsStr = line.split(",");

                List<T3> probsOfTID = new ArrayList<>();

                for (String item : probsStr) {
                    probsOfTID.add((T3) item);
                }

                // gộp từng danh sách các prob trong mỗi TID lại thành 1 danh sách mới
                probsList.add(probsOfTID);
                // => [[prob_Of_TID_1]  , [prob_Of_TID_2], [prob_Of_TID_3], ...]
                // => [[1.0,0,0.9,0.6,0,0,0,0], [0.9,0.9,0.7,0.6,0.4,0,0,0], [0,0.5,0.8,0.9,0,0.2,0.4,0], [], [],..]
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return probsList;
    }

    /**
     * Thực thi toàn bộ thuật toán TUFP
     * */
    public List<CUPList<T1, T2, T3>> executeTUFP(List<CUPList<T1, T2, T3>> cupLists, int k) {
        // reset statistics
        MemoryLogger.getInstance().reset(); // reset utility to check memory usage

        // record the start time
        timeStart = System.currentTimeMillis();
        // result chứa danh sách Top-k UFP
        List<CUPList<T1, T2, T3>> result = new ArrayList<>();

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

        // Khởi tạo itemsets và gán result hiện tại vào itemsets để chứa các item kích thước 1
        // phục vụ cho việc duyệt qua các item để xử lý kết hợp CUP-List
        List<CUPList<T1, T2, T3>> itemsets = new ArrayList<>(result);

        // Thực hiện chiến lược chia để trị
        TUFP_Search(result, itemsets, k);

        // record the end time
        timeEnd = System.currentTimeMillis();
        return result;
    }

    /**
     * Sắp xếp Cup-Lists theo thứ tự tăng dần ExpSup
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

        String newItemsName = (String) (cupList1.getItemName()) + (String) cupList2.getItemName();

        CUPList<T1, T2, T3> newCupList = new CUPList<>((T1) removeDuplicates(newItemsName), newTepLists);
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
    public static <T1, T2, T3> void TUFP_Search(List<CUPList<T1, T2, T3>> result, List<CUPList<T1, T2, T3>> itemsets, int k) {
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
        System.out.println("Memory : " + MemoryLogger.getInstance().getMaxMemory() + " mb");
        System.out.println("Total time : " + (timeEnd - timeStart) + " ms");
        System.out.println("===================================================");
    }

    public void printTopKUFP() {
        for (CUPList<T1, T2, T3> cupList : result) {
            System.out.println("\t\t\t" + cupList.getItemName() + ": " + cupList.getExpSup());
        }
    }
}