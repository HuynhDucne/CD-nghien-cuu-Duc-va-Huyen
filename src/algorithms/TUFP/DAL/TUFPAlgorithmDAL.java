package algorithms.TUFP.DAL;

import algorithms.TUFP.BLL.CUPList;
import algorithms.TUFP.BLL.TEPList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * TUFPAlgorithm:
 * + Biến đếm số lượng transaction             -   Type: int
 */
public class TUFPAlgorithmDAL {
    /**
     * Số lượng transaction trong dataset
     */
    static int transactionCount = 0;

    public static int getTransactionCount() {
        return transactionCount;
    }

    /**
     * Đọc file filePath trong folder dataset_prob đã được định dạng có xác suất ngẫu nhiên
     * và chuyển thành cấu trúc dữ liệu CUP-Lists sau đó lấy top k trong danh sách CUP-List đó
     *
     * @param filePath tên của file dataset có xác suất
     * @param k top k
     * @return danh sách top k CUP-Lists có expSup lớn nhất
     */
    public static List<CUPList> loadDatasetProb(String filePath, int k) {
        System.out.println("Reading file " + filePath + ". . .");

        // Khởi tạo list chứa danh sách CUP-Lists
        List<CUPList> cupLists = new ArrayList<>();
        BufferedReader reader;
        String line;
        String regex = " ";
        int TID = 1;

        try {
            // Lấy đường dẫn chưa file dataset chứa xác suất để đọc file
            reader = new BufferedReader(new FileReader(new File(".").getAbsoluteFile()
                    + "\\src\\algorithms\\TUFP\\dataset_prob\\" + filePath));

            // Đọc dòng đầu tiên để lấy danh sách items
            String[] itemsName = reader.readLine().split(regex);

            while (((line = reader.readLine()) != null)) {

                // split dòng đang đọc để duyệt qua các ký tự trên dòng đó
                String[] probsStr = line.split(regex);
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
                        // Khởi tạo TEP-List và CUP-List cho item đó
                        List<TEPList> tepList = new ArrayList<>();
                        tepList.add(new TEPList(TID, prob));
                        cupList = new CUPList(itemsName[i], prob, tepList, prob);
                        cupLists.add(cupList);
                    } else {
                        // Cập nhật TEP-List, ExpSup, Max cho item đó
                        cupList.getTepList().add( new TEPList(TID, prob));
                        cupList.setExpSup(Math.round((cupList.getExpSup() + prob) * 100.0) / 100.0);
                        cupList.setMaxProb(cupList.maxProb(cupList));
                    }
                }
                TID++;
            }
            // Tính tổng số lượng transaction
            transactionCount = TID-1;

            // Sắp xếp danh sách CUP-Lists giảm dần theo cumulative support value (expSup)
            Collections.sort(cupLists, Comparator.comparingDouble(CUPList::getExpSup).reversed());

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
     * @param cupLists danh sách CUP-List
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
