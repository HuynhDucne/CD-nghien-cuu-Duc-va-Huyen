package algorithms.TUFP.DAL;

import java.io.*;
import java.util.*;

/**
 * Dataset:
 * + Bộ Set chứa tất cả các items trong dataset      -   Type: Set<String>
 * + Danh sách tất cả các transactions trong dataset -   Type: List<Map<String, Double>>
 * mỗi transaction là một Map chứa các key-value tương ứng với item-prob
 */

public class Dataset {
    /**
     * Bộ Set chứa tất cả các items trong dataset
     */
    private static final Set<String> items = new TreeSet<>(new KeyComparator());

    /**
     * Danh sách tất cả các transactions trong dataset
     */
    private static final List<Map<String, Double>> transactions = new ArrayList<>();

    /**
     * Phương thức này sẽ duyệt qua file dataset và thu thập các dữ liệu cơ bản như
     * danh sách các items và danh sách các transactions cùng với các Existential Probability
     * được random cho mỗi item trong từng transaction
     *
     * @param filePath đường dẫn của tệp dataset
     * @throws IOException ngoại lệ nếu lỗi đọc tệp
     */
    public static void loadFile(String filePath) throws IOException {
        System.out.println("Reading file " + filePath + ". . .");
        String line; // biến line để đọc mỗi dòng
        BufferedReader reader = null; // reader để đọc file
        String regex = " ";

        try {
            // Lấy đường dẫn file dataset để đọc file
            reader = new BufferedReader(new FileReader(
                    new File(".").getAbsoluteFile()
                            + "\\src\\algorithms\\TUFP\\dataset\\" + filePath));

            // Tạo một đối tượng Random
            Random random = new Random();

            // Cố định giá trị random
            random.setSeed(1);

            // countTransaction dùng để giới hạn transaction trong dataset khi cần
//            int countTransaction = 0;
//            && (countTransaction <= 88162)

            // for each line
            while (((line = reader.readLine()) != null)) {

                // Nếu dòng này không trống, không phải dòng nhận xét hoặc các kiểu siêu dữ liệu khác
                if (!line.isEmpty() && line.charAt(0) != '#' && line.charAt(0) != '%' && line.charAt(0) != '@') {
                    // Tách dòng theo dấu cách và gọi hàm "addTransaction" để xử lý dòng này
                    addTransaction(line.split(regex), random);
                }
//                countTransaction++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Phương thức này xử lý trên từng dòng cho file được đọc
     * Ở đây sẽ tạo ra tập Set chứa các item và
     * danh sách các transaction có kèm thêm xác suất tồn tại ngẫu nhiên
     *
     * @param itemsString các item trên một dòng
     * @param random đối tượng tạo xác suất ngẫu nhiên
     */
    private static void addTransaction(String[] itemsString, Random random) {

        // Khởi tạo Map theo thứ tự tăng dần theo Key
        Map<String, Double> itemAndProb = new TreeMap<>(new KeyComparator());
        for (String attribute : itemsString) {

            // Tạo số ngẫu nhiên từ (0, 1] (không bao gồm 0, bao gồm 1)
            double prob = Math.round((random.nextDouble() + Double.MIN_VALUE) * 100.0) / 100.0;

            // Đưa cặp item và prob vào map
            itemAndProb.put(attribute, prob);

            // Thêm items vào Set items để tự lọc ra những item bị trùng lặp
            items.add(attribute);
        }

        // Thêm các transactions vào trong danh sách chứa tất cả các transaction trong dataset này
        transactions.add(itemAndProb);
    }

    /**
     * Sắp xếp Map theo thứ tự tăng dần theo Key, so sánh Key trong Map
     */
    public static class KeyComparator implements Comparator<String> {
        @Override
        public int compare(String key1, String key2) {

//            // So sánh theo thứ tự tăng dần
//            return key1.compareTo(key2);

            double num1, num2;
            try {
                num1 = Double.parseDouble(key1);
            } catch (NumberFormatException e) {
                num1 = Double.NaN; // Đánh dấu chuỗi không phải số
            }
            try {
                num2 = Double.parseDouble(key2);
            } catch (NumberFormatException e) {
                num2 = Double.NaN; // Đánh dấu chuỗi không phải số
            }

            if (Double.isNaN(num1) && Double.isNaN(num2)) {
                return key1.compareTo(key2); // So sánh chuỗi nếu cả hai đều không phải số
            } else if (Double.isNaN(num1)) {
                return 1; // Chuỗi không phải số lớn hơn số
            } else if (Double.isNaN(num2)) {
                return -1; // Số lớn hơn chuỗi không phải số
            } else {
                return Double.compare(num1, num2); // So sánh số
            }
        }
    }

    /**
     * Ghi lại dữ liệu từ dataset vào file dataset_prob.txt với cấu trúc như ví dụ trong bài báo
     *
     * @param filePathFormat đường dẫn của tệp được định dạng có chứa xác suất ngẫu nhiên cho mỗi item
     */
    public static void writeDb(String filePathFormat) {
        System.out.println("File is generating. . .");

        // Khởi tạo 1 mảng có kich thước bằng số lượng phần tử trong set items
        String[] itemsStr = new String[items.size()];

        // chuyển đổi set items thành mảng itemsStr
        items.toArray(itemsStr);

        try {
            File dir = new File(System.getProperty("user.dir") + "\\src\\algorithms\\TUFP\\dataset_prob");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, filePathFormat);
            FileWriter writer = new FileWriter(file);

            // Ghi dòng đầu tiên
            writer.write(String.join(" ", itemsStr) + "\n");

            // Ghi dữ liệu cho từng TID
            for (int tid = 0; tid < transactions.size(); tid++) {
                String[] rowData = new String[itemsStr.length];

                // Duyệt qua từng item trong itemsStr và so sánh từng map trong transaction
                // nếu item tìm thấy trong các map của transaction thì lưu giá trị đó trong map vào mảng rowData
                // nếu không tìm thấy thì gán vào mảng rowData giá trị 0
                for (int i = 0; i < itemsStr.length; i++) {
                    if (transactions.get(tid).containsKey(itemsStr[i])) {
                        rowData[i] = String.valueOf(transactions.get(tid).get(itemsStr[i]));
                    } else {
                        rowData[i] = String.valueOf('0');
                    }
                }
                // Ghi dữ liệu vào file dataset_prob
                writer.write(String.join(" ", rowData) + "\n");
            }
            writer.close();
            System.out.println("File " + filePathFormat + " generated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Phương thức này in ra toàn bộ nội dung của dataset có chứa xác suất tồn tại
     */
    public static void printDatabase() {
        System.out.println("=================== TRANSACTION DATABASE ===================");
        int count = 0;
        // for each transaction
        for (Map<String, Double> itemset : transactions) {
            System.out.print(count + "=>  ");
            printMap(itemset); // print the transaction
            count++;
        }
    }

    /**
     * In một transaction kiểu map
     *
     * @param itemset map gồm key là item và value là xác suất của item tương ứng.
     */
    private static void printMap(Map<String, Double> itemset) {
        Set<String> set = itemset.keySet();
        for (String key : set) {
            System.out.print(key + ":" + itemset.get(key) + " ");
        }
        System.out.println();
    }

    /**
     * Trả về danh sách các transaction trong bộ dataset
     *
     * @return một danh sách các giao dịch (một giao dịch là một Map các item - prob).
     */
    public static List<Map<String, Double>> getTransactions() {
        return transactions;
    }

    /**
     * Trả về một Set tập hợp các items có trong bộ dataset
     *
     * @return Set các items.
     */
    public static Set<String> getItems() {
        return items;
    }
}
