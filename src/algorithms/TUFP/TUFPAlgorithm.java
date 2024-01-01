package algorithms.TUFP;

import ca.pfv.spmf.tools.MemoryLogger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TUFPAlgorithm:
 * + List CUP-Lists                            -   Type: List<CUPList<T1, T2, T3>>
 * + Top k                                     -   Type: int
 * + Result Top-K UFP:                         -   Type: List<CUPList<T1, T2, T3>>
 * + thuộc tính dataset để chạy các hàm của lớp Dataset               -   Type: Dataset
 * + Hai biến đo thời gian chạy giải thuật     -   Type: long
 * + Biến đếm số lượng frequents itemsets      -   Type: int
 * + Biến đếm số lượng transaction             -   Type: int
 */

public class TUFPAlgorithm<T1, T2, T3> {
    /**
     * biến thời gian bắt đầu thực thi giải thuật
     */
    long timeStart = 0;

    /**
     * biến thời gian kết thúc thực thi giải thuật
     */
    long timeEnd = 0;

    /**
     * Số lượng frequents itemsets được tìm thấy
     */
    private int itemsetCount = 0;

    /**
     * Số lượng transaction trong dataset
     */
    private int transactionCount = 0;

    /**
     * Danh sách CUP-List có được từ dataset
     */
    private List<CUPList<T1, T2, T3>> cupLists = new ArrayList<>();

    /**
     * Top k trong danh sách result
     */
    private int k;

    /**
     * Danh sách Top-K Uncertain Frequent Pattern
     */
    private List<CUPList<T1, T2, T3>> result = new ArrayList<>();

    /**
     * Dữ liệu từ dataset
     */
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
     * phương thức này chạy cho data có sẵn xác suất
     */
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
     * Phương thức này tạo ra một map được sắp xếp theo thứ tự giảm dần Value - tổng xác suất của mỗi item
     *
     * @return phương thức này trả về một map có Key là item và Value là tổng xác suất tồn tại của item đó
     */
    public Map<T1, T3> sortedValueMap() {
        // Tạo 1 map là cupMap chứa Key là item và Value là tổng xác suất của item
        Map<T1, T3> cupMap = new HashMap<>();

        // Duyệt qua từng item
        for (T1 item : dataset.getItems()) {
            Double probSum = 0.0;
            // Duyệt qua từng transaction
            for (Map<T1, T3> trans : dataset.getTransactions()) {
                // Nếu có tồn tại item trong map trans đang xét thì
                // tính tổng xác suất của item đó
                if (trans.containsKey(item)) {
                    probSum += (Double) trans.get(item);
                }
            }
            cupMap.put(item, (T3) probSum);
        }

        // Sắp xếp cupMap theo thứ tự giảm dần giá trị Value là tổng xác suất của item
        /*
           Tạo mới đối tượng Stream từ đối tượng Map rồi sau đó sử dụng
           các phương thức sorted() và collect() của đối tượng Stream này để sắp xếp

           Phương thức sorted() đóng vai trò là một intermediate operation trong Stream pipeline.
           Tham số của phương thức này là một đối tượng Comparator cho phép ta có thể định nghĩa
           tiêu chí cần sắp xếp là gì. Có thể sử dụng phương thức static comparingByValue()
           của class Map.Entry để sắp xếp theo value

           Còn phương thức collect() sẽ đóng vai trò là một terminal operation trong Stream pipeline.
           Tham số của phương thức này là một đối tượng Collectors và sẽ sử dụng phương thức toMap()
           của đối tượng Collectors này để xây dựng một đối tượng Map mới sau khi sắp xếp.

           Trong phương thức toMap():
                + Tham số đầu và thứ 2 được sử dụng để generate key và value cho đối tượng Map mới.
                + Tham số thứ 3 là một tham số optional, được sử dụng trong trường hợp key/value của đối tượng
                  Map mới có những giá trị duplicate.
                  (oldValue, newValue) -> oldValue có nghĩa trong trường hợp value bị duplicate,
                  thì sẽ lấy giá trị của key trước đó.
                + Tham số cuối cùng cũng là một tham số optional, cho phép định nghĩa đối tượng Map mới
                  là thể hiện của class nào. Mặc định nếu không sử dụng tham số này thì class đó sẽ là HashMap.
                  LinkedHashMap::new Sử dụng LinkedHashMap để duy trì các phần tử theo thứ tự chèn.
         */
        Map<T1, T3> cupMapSorted = cupMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue((Comparator<? super T3>) Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        return cupMapSorted;
    }

    /**
     * Phương thức này cắt giảm bớt item để tạo CUP-List giúp tiết kiệm thời gian và bộ nhớ
     *
     * @param sortedValueMap map [key:item, value:probability] được sắp xếp theo thứ tự giảm dần value
     * @return Phương thức này kiểm tra nếu số lượng item > k thì trả về danh sách top k item
     * ngược lại thì trả về toàn bộ danh sách item ban đầu
     */
    public List<T1> topKItem(Map<T1, T3> sortedValueMap) {
        // Khởi tạo Set chứa tất cả các item từ map[key:item, value:probability]
        // đã được sắp xếp giảm dần value
        Set<T1> setItem = sortedValueMap.keySet();
        // Chuyển Set sang List
        List<T1> listItem = new ArrayList<>(setItem);
        List<T1> listTopKItem = new ArrayList<>();

        if (setItem.size() > k) {
            for (int i = 0; i < k; i++) {
                listTopKItem.add(listItem.get(i));
            }
        } else {
            listTopKItem.addAll(listItem);
        }
        return listTopKItem;
    }

    /**
     * Chuyển dữ liệu đọc được thành cấu trúc dữ liệu CUP-Lists
     */
    public void readDatasetToCupLists() {
        // khởi tạo danh sách top-k item được sắp xếp theo thứ tự giảm dần tổng expSup
        List<T1> listTopKItem = topKItem(sortedValueMap());

        // Duyệt qua từng item trong danh sách item
        for (T1 item : listTopKItem) {
            List<TEPList<T2, T3>> tepList = new ArrayList<>();
            Integer tid = 1;
            // Duyệt qua từng transaction trong danh sách transactions
            for (Map<T1, T3> trans : dataset.getTransactions()) {
                // Nếu có tồn tại item trong map transaction đang duyệt thì
                // tạo TEP-Lists từ tid và xác suất của item đó
                if (trans.containsKey(item)) {
                    tepList.add(new TEPList<>((T2) tid, trans.get(item)));
                    tid++;
                }
            }
            // Tạo đối tượng CUPList và thêm vào danh sách
            CUPList<T1, T2, T3> cupList = new CUPList<>(item, tepList);
            cupList.setExpSup(cupList.sumExpSup(cupList));
            cupList.setMaxProb(cupList.maxProb(cupList));
            cupLists.add(cupList);
        }
    }

    /**
     * Thực thi toàn bộ thuật toán TUFP
     */
    public void executeTUFP() {
        // reset statistics
        MemoryLogger.getInstance().reset(); // reset utility to check memory usage

        // check memory usage
        MemoryLogger.getInstance().checkMemory();

        // record the start time
        timeStart = System.currentTimeMillis();

        // Kiểm tra nếu đang xử lí data mẫu trong ví dụ bài báo thì thực hiện các dòng lệnh trong if
        // ngược lại nếu đang xử lý các bộ dataset chưa có xác suất thì thực hiện các dòng lệnh trong else
        if (dataset.size() == 0) {

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
        } else {
            transactionCount = dataset.size();
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
     */
    private static <T1, T2, T3> void sortCUPListsByExpSup(List<CUPList<T1, T2, T3>> cupLists) {
        // Ghi đè lại phương thức compare trong Comparator để so sánh các đối tượng CUPList với nhau
        cupLists.sort(new Comparator<CUPList<T1, T2, T3>>() {
            @Override
            public int compare(CUPList<T1, T2, T3> cupList1, CUPList<T1, T2, T3> cupList2) {
                return Double.compare((Double) cupList2.getExpSup(), (Double) cupList1.getExpSup());
            }
        });
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
     * @param cupList1 đại diện cho CUP-List của item thứ nhất
     * @param cupList2 đại diện cho CUP-List của item thứ hai
     * @param <T1>     kiểu dữ liệu item
     * @param <T2>     kiểu dữ liệu của TID
     * @param <T3>     kiểu dữ liệu của xác suất (prob)
     *                 <p>
     *                 Ví dụ kết hợp CUP-List C và D:
     *                 CUP-List C:       CUP-List D:   =>    CUP-List CD:
     *                 TID | Prob.       TID | Prob.         TID | Prob.
     *                 1  | 0.9          1  | 0.6            1  | (0.9 * 0.6)
     *                 2  | 0.7          2  | 0.6            2  | (0.7 * 0.6)
     *                 3  | 0.8          3  | 0.9            3  | (0.8 * 0.9)
     *                 4  | 0.9          5  | 0.3            5  | (0.9 * 0.3)
     *                 5  | 0.9          6  | 0.9            7  | (0.4 * 0.6)
     *                 7  | 0.4          7  | 0.6
     * @return phương thức trả về một CUP-List mới được kết hợp từ item thứ nhất và item thứ hai
     */
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
     *
     * @param result   danh sách top-k UFPs cuối cùng
     * @param itemsets danh sách các CUP-List phục vụ cho việc duyệt qua và kết hợp tạo CUP-List mới
     * @param k        top k
     */
    public void TUFP_Search(List<CUPList<T1, T2, T3>> result, List<CUPList<T1, T2, T3>> itemsets, int k) {
        // Khởi tạo threshold = giá trị expSup của item cuối trong result
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
     */
    public void printTopKUFP() {
        System.out.println("In Top-k UFP:");
        for (CUPList<T1, T2, T3> cupList : result) {
            System.out.println("\t\t\t" + cupList.getItemName() + ": " + cupList.getExpSup());
        }
        System.out.println();
    }
}