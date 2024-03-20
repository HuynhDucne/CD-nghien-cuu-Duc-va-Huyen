package algorithms.tuwfi.dal;

import algorithms.tuwfi.dto.Cup;
import algorithms.tuwfi.dto.Tep;

import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * TUWFI (Top k Uncertain Weighted Frequent Itemsets) Algorithm - Data Access Layer:
 * + Biến đếm số lượng transaction                              -   Type: int
 * + Danh sách tên item                                         -   Type: List<String>
 * + Map [item, weight] được sắp xếp tăng dần theo item         -   Type: Map<String, Double>
 */
public class TuwfiDal {
    /**
     * Số lượng transaction trong dataset
     */
    static int transactionCount = 0;

    /**
     * Danh sách tên item
     */
    static final List<String> ITEMS_NAME_LIST = new ArrayList<>();

    /**
     * Map gồm key là các tên item và value là các giá trị weight tương ứng với item
     * Map được sắp xếp theo thứ tự tăng dần theo key
     */
    static final Map<String, Double> WEIGHT_MAP = new TreeMap<>(new DatasetDal.KeyComparator());

    public static int getTransactionCount() {
        return transactionCount;
    }

    public static List<String> getItemsNameList() {
        return ITEMS_NAME_LIST;
    }

    public static Map<String, Double> getWeightMap() {
        return WEIGHT_MAP;
    }

    /**
     * Đọc file weightFile trong folder dataset/weight để lấy dữ liệu item name và weight của chúng
     *
     * @param weightFile tên của file chứa weight của item
     */
    public static void loadWeight(String weightFile) {
        System.out.println("Reading file " + weightFile + ". . .");

        // Khởi tạo mảng để chứa weight của mỗi item
        double[] weightDouble = new double[0];
        String line;
        // Biểu thức chính quy để tách các phần tử trong một dòng
        String regex = " ";

        // Lấy đường dẫn của thư mục chứa file dataset và mở nó để đọc file
        try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/src/dataset/weight/" + weightFile))) {
            // Đọc dòng đầu tiên để lấy danh sách items
            String[] itemsName = reader.readLine().split(regex);
            // Lưu các tên item đã đọc được vào danh sách ITEMS_NAME_LIST
            ITEMS_NAME_LIST.addAll(Arrays.asList(itemsName));

            // Đọc từng dòng trong file
            while (((line = reader.readLine()) != null)) {
                // split để duyệt qua các weight trên dòng đó
                String[] weightStr = line.split(regex);
                // Chuyển đổi mảng String thành mảng Double
                weightDouble = Arrays.stream(weightStr).mapToDouble(Double::parseDouble).toArray();
            }

            // Lặp qua danh sách tên item để thêm tên item cùng weight tương ứng vào WEIGHT_MAP
            for (int i = 0; i<itemsName.length; i++) {
                WEIGHT_MAP.put(itemsName[i], weightDouble[i]);
            }

        } catch (FileNotFoundException e) {
            System.err.println("Weight file not found: " + weightFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Đọc file probFile trong folder dataset/probability đã được định dạng có xác suất ngẫu nhiên
     * và chuyển thành cấu trúc dữ liệu CUP-List sau đó sắp xếp cup-List giảm dần theo cumWeightSup
     *
     * @param probFile tên của file dataset có xác suất tồn tại ngẫu nhiên cho mỗi item
     * @return danh sách CUP-List được sắp xếp giảm dần theo cumWeightSup
     */
    public static List<Cup> generateCupListForItems(String probFile) {
        System.out.println("Reading file " + probFile + ". . .");

        // Khởi tạo danh sách chứa CUP-List
        List<Cup> cupList = new ArrayList<>();
        String line;
        // Biểu thức chính quy để tách các phần tử trong một dòng
        String regex = " ";
        // Khởi tạo TID (Transaction ID)
        int TID = 1;

        // Lấy đường dẫn chứa file dataset có xác suất để đọc file
        try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/src/dataset/probability/" + probFile))) {

            // Đọc dòng đầu tiên để lấy danh sách items
            String[] itemsName = reader.readLine().split(regex);

            // Đọc từng dòng trong file
            while (((line = reader.readLine()) != null)) {

                // split dòng đang đọc để duyệt qua các ký tự trên dòng đó
                String[] probsStr = line.split(regex);

                // Duyệt qua từng xác suất trong dòng
                for (int i = 0; i < probsStr.length; i++) {
                    double prob = Double.parseDouble(probsStr[i]);

                    // Bỏ qua các giá trị xác suất bằng 0 để tiết kiệm thời gian chạy
                    if (prob == 0) {
                        continue;
                    }

                    // Gọi hàm getCupByItemName để truy cập tới CUP trong CUP-List thông qua item
                    Cup cup = getCupByItemName(cupList, itemsName[i]);

                    // Nếu CUP bằng null thì CUP của item đang xét không tồn tại
                    // Ngược lại thì CUP của item đang xét đã tồn tại
                    if (cup == null) {
                        // Khởi tạo TEP-List mới
                        List<Tep> tepList = new ArrayList<>();
                        // Khởi tạo và thêm TEP mới vào danh sách
                        tepList.add(new Tep(TID, prob));
                        // Khởi tạo CUP mới và thêm vào CUP-List
                        cup = new Cup(itemsName[i], prob, tepList, prob, WEIGHT_MAP.get(itemsName[i]), WEIGHT_MAP.get(itemsName[i]) * prob);
                        cupList.add(cup);
                    } else {
                        // Thêm TEP mới vào TEP-List của CUP hiện tại
                        cup.getTepList().add(new Tep(TID, prob));
                        // Cập nhật cumSup và cumWeightSup của CUP
                        cup.setCumSup(cup.getCumSup() + prob);
                        cup.setMaxProb(cup.calculateMaxProb());
                        cup.setCumWeightSup(cup.getCumWeightSup() + (WEIGHT_MAP.get(itemsName[i]) * prob));
                    }
                }
                // Tăng TID sau mỗi dòng
                TID++;
            }
            // Tính tổng số lượng transaction
            transactionCount = TID - 1;

            // Sắp xếp CUP-List giảm dần theo Cumulative Weighted Support Value (cumWeightSup)
            sortCupListByCumWeightSup(cupList);

        } catch (FileNotFoundException e) {
            System.err.println("Weight file not found: " + probFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return cupList;
    }

    /**
     * Phương thức để lấy đối tượng CUP theo giá trị thuộc tính patternName
     *
     * @param cupList CUP-List
     * @param targetItemName item để tìm kiếm CUP của item đó
     * @return CUP của item targetItemName trong CUP-List
     */
    public static Cup getCupByItemName(List<Cup> cupList, String targetItemName) {
        for (Cup cup : cupList) {
            if (cup.getPatternName().equals(targetItemName)) {
                return cup; // Trả về CUP nếu tìm thấy
            }
        }
        return null; // Trả về null nếu không tìm thấy CUP tương ứng với item
    }

    /**
     * Phương thức này lấy top k trong CUP-List
     *
     * @param cupList CUP-List đã được sắp xếp tăng dần theo CumWeightSup
     * @param k top k muốn lấy từ CUP-List
     * @return top k trong CUP-List hoặc toàn bộ CUP-List
     */
    public static List<Cup> truncateCupList(List<Cup> cupList, int k) {
        // Trả về một phần của CUP-List, bắt đầu từ vị trí 0 đến k nếu k nhỏ hơn kích thước của CUP-List,
        // ngược lại trả về toàn bộ CUP-List
        return cupList.subList(0, Math.min(k, cupList.size()));
    }

    /**
     * Sắp xếp CUP-List theo thứ tự giảm dần CumWeightSup
     *
     * @param cupList CUP-List chứa Top k uncertain frequent itemsets
     */
    public static void sortCupListByCumWeightSup(List<Cup> cupList) {
        // Sử dụng phương thức sort của Java Collection Framework
        // Comparator.comparingDouble(CUP::getCumWeightSup).reversed() tạo ra một Comparator
        // so sánh các CUP dựa trên giá trị CumWeightSup của chúng
        cupList.sort(Comparator.comparingDouble(Cup::getCumWeightSup).reversed());
    }
}
