package algorithms.TWUFP.DAL;

import algorithms.TWUFP.BLL.CUPList;
import algorithms.TWUFP.BLL.TEPList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * TUFPAlgorithm:
 * + Biến đếm số lượng transaction                              -   Type: int
 * + Danh sách tên item                                         -   Type: List<String>
 * + Map [item, weight] được sắp xếp tăng dần theo item         -   Type: Map<String, Double>
 */
public class TWUFPAlgoDAL {
    /**
     * Số lượng transaction trong dataset
     */
    static int transactionCount = 0;

    /**
     * Danh sách tên item
     */
    public static final List<String> itemsNameList = new ArrayList<>();

    /**
     * Map gồm key là các tên item và value là các giá trị weight tương ứng với item
     * Map được sắp xếp theo thứ tự tăng dần theo key
     */
    public static final Map<String, Double> weightMap = new TreeMap<>(new DatasetDAL.KeyComparator());

    public static int getTransactionCount() {
        return transactionCount;
    }

    /**
     * Đọc file fileWeight trong folder weight để lấy dữ liệu item name và weight của chúng
     *
     * @param fileWeight tên của file chứa weight của item
     */
    public static void loadWeight(String fileWeight) {
        System.out.println("Reading file " + fileWeight + ". . .");

        // Khởi tạo mảng để chứa weight của mỗi item
        double[] weightDouble = new double[0];
        BufferedReader reader;
        String line;
        // Biểu thức chính quy để tách các phần tử trong một dòng
        String regex = " ";

        try {
            // Lấy đường dẫn chưa file dataset chứa xác suất để đọc file
            reader = new BufferedReader(new FileReader(new File(".").getAbsoluteFile()
                    + "\\src\\algorithms\\TWUFP\\weight\\" + fileWeight));

            // Đọc dòng đầu tiên để lấy danh sách items
            String[] itemsName = reader.readLine().split(regex);
            // Lưu các tên item đã đọc được vào danh sách itemsNameList
            itemsNameList.addAll(Arrays.asList(itemsName));

            // Đọc từng dòng trong file
            while (((line = reader.readLine()) != null)) {
                // split để duyệt qua các weight trên dòng đó
                String[] weightStr = line.split(regex);
                // Chuyển đổi mảng String thành mảng Double
                weightDouble = Arrays.stream(weightStr).mapToDouble(Double::parseDouble).toArray();
            }

            // Lặp qua danh sách tên item để thêm tên item cùng weight tương ứng vào weightMap
            for (int i = 0; i<itemsName.length; i++) {
                weightMap.put(itemsName[i], weightDouble[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Đọc file fileProb trong folder dataset_prob đã được định dạng có xác suất ngẫu nhiên
     * và chuyển thành cấu trúc dữ liệu CUP-Lists sau đó lấy top k trong danh sách CUP-List đó
     *
     * @param fileProb tên của file dataset có xác suất
     * @param k        top k
     * @return danh sách top k CUP-Lists có expSup lớn nhất
     */
    public static List<CUPList> loadDatasetProb(String fileProb, int k) {
        System.out.println("Reading file " + fileProb + ". . .");

        // Khởi tạo list chứa danh sách CUP-Lists
        List<CUPList> cupLists = new ArrayList<>();
        BufferedReader reader;
        String line;
        // Biểu thức chính quy để tách các phần tử trong một dòng
        String regex = " ";
        // Khởi tạo TID (Transaction ID)
        int TID = 1;

        try {
            // Lấy đường dẫn chưa file dataset chứa xác suất để đọc file
            reader = new BufferedReader(new FileReader(new File(".").getAbsoluteFile()
                    + "\\src\\algorithms\\TWUFP\\dataset_prob\\" + fileProb));

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

                    // Gọi hàm getCUPListByItemName để truy cập tới CUP-List thông qua item trong danh sách CUP-Lists
                    CUPList cupList = getCUPListByItemName(cupLists, itemsName[i]);

                    // Nếu cupList bằng null thì CUP-List của item đang xét không tồn tại
                    // Ngược lại CUP-List của item đang xét đã tồn tại
                    if (cupList == null) {
                        // Khởi tạo danh sách TEPList mới
                        List<TEPList> tepList = new ArrayList<>();
                        // Thêm TEPList mới vào danh sách
                        tepList.add(new TEPList(TID, prob));
                        // Khởi tạo CUPList mới và thêm vào danh sách CUPLists
                        cupList = new CUPList(itemsName[i], prob, tepList, prob, weightMap.get(itemsName[i]), weightMap.get(itemsName[i]) * prob);
                        cupLists.add(cupList);
                    } else {
                        // Thêm TEPList mới vào danh sách TEPList của CUPList hiện tại
                        cupList.getTepList().add(new TEPList(TID, prob));
                        // Cập nhật expSup và expWeightSup của CUPList
                        cupList.setExpSup(cupList.getExpSup() + prob);
                        cupList.setMaxProb(cupList.maxProb(cupList));
                        cupList.setExpWeightSup(cupList.getExpWeightSup() + (weightMap.get(itemsName[i]) * prob));
                    }
                }
                // Tăng TID sau mỗi dòng
                TID++;
            }
            // Tính tổng số lượng transaction
            transactionCount = TID - 1;

            // Sắp xếp danh sách CUP-Lists giảm dần theo cumulative support value (expSup)
            Collections.sort(cupLists, Comparator.comparingDouble(CUPList::getExpWeightSup).reversed());

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Nếu k < kích thước CUP-Lists thì trả về một phần danh sách CUP-Lists bắt đầu từ vị trí 0 đến k
        // ngược lại thì trả về danh sách CUP-Lists
        return cupLists.subList(0, Math.min(k, cupLists.size()));
    }

    /**
     * Phương thức để lấy đối tượng theo giá trị thuộc tính itemName
     *
     * @param cupLists       danh sách CUP-List
     * @param targetItemName item để tìm kiếm CUP-List item đó
     * @return CUP-List của item targetItemName trong danh sách CUP-List
     */
    public static CUPList getCUPListByItemName(List<CUPList> cupLists, String targetItemName) {
        for (CUPList cupList : cupLists) {
            if (cupList.getItemName().equals(targetItemName)) {
                return cupList; // Trả về đối tượng nếu tìm thấy
            }
        }
        return null; // Trả về null nếu không tìm thấy đối tượng
    }
}
