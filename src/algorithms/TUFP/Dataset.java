package algorithms.TUFP;

import java.io.*;
import java.util.*;

/**
 * Dataset:
 * + Bộ Set chứa tất cả các items trong dataset      -   Type: Set<T1>
 * + Danh sách tất cả các transactions trong dataset -   Type: List<Map<T1, T2>>
 * mỗi transaction là một Map chứa các key-value tương ứng với item-prob
 */

public class Dataset<T1, T2> {
    /**
     * Bộ Set chứa tất cả các items trong dataset
     */
    private final Set<T1> items = new TreeSet<>(new KeyComparator());

    /**
     * Danh sách tất cả các transactions trong dataset
     */
    private final List<Map<T1, T2>> transactions = new ArrayList<>();

    /**
     * Method to add a new transaction to this database.
     *
     * @param transaction the transaction to be added
     */
    public void addTransaction(Map<T1, T2> transaction) {
        transactions.add(transaction);
        items.addAll(transaction.keySet());
    }

    /**
     * Phương thức này sẽ duyệt qua file dataset và thu thập các dữ liệu cơ bản như
     * danh sách các items và danh sách các transactions cùng với các Existential Probability
     * được random cho mỗi item trong từng transaction
     *
     * @param filePath đường dẫn của tệp dataset
     * @throws IOException ngoại lệ nếu lỗi đọc tệp
     */
    public void loadFile(String filePath) throws IOException {
        String line; // biến line để đọc mỗi dòng
        BufferedReader reader = null; // reader để đọc file
        String regex = " ";
        try {
            reader = new BufferedReader(new FileReader(filePath));

            // countTransaction dùng để giới hạn transaction trong dataset khi cần
//            int countTransaction = 0;
//            && (countTransaction <= 88162)

            // for each line
            while (((line = reader.readLine()) != null)) {

                // Nếu dòng này không trống, không phải dòng nhận xét hoặc các kiểu siêu dữ liệu khác
                if (!line.isEmpty() && line.charAt(0) != '#' && line.charAt(0) != '%' && line.charAt(0) != '@') {
                    // Tách dòng theo dấu cách và gọi hàm "addTransaction" để xử lý dòng này
                    addTransaction(line.split(regex));
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
     */
    private void addTransaction(String[] itemsString) {
        // Khởi tạo Map theo thứ tự tăng dần theo Key
        Map<T1, T2> itemAndProb = new TreeMap<>(new KeyComparator());
        for (String attribute : itemsString) {

            // Tạo một đối tượng Random
            Random random = new Random();

            // Tạo số ngẫu nhiên từ (0, 1] (không bao gồm 0, bao gồm 1)
            Double prob = Math.round((random.nextDouble() + Double.MIN_VALUE) * 100.0) / 100.0;

            // Đưa cặp item và prob vào map
            itemAndProb.put((T1) attribute, (T2) prob);

            // Thêm items vào Set items để tự lọc ra những item bị trùng lặp
            items.add((T1) attribute);
        }

        // Thêm các transactions vào trong danh sách chứa tất cả các transaction trong dataset này
        transactions.add(itemAndProb);
    }

    /**
     * Sắp xếp Map theo thứ tự tăng dần theo Key, so sánh Key trong Map
     */
    class KeyComparator implements Comparator<T1> {
        @Override
        public int compare(T1 key1, T1 key2) {
            // So sánh theo thứ tự tăng dần
            return Double.compare(Double.parseDouble((String) key1), Double.parseDouble((String) key2));
        }
    }

    /**
     * Ghi lại dữ liệu từ dataset vào file dataset_prob.txt với cấu trúc như ví dụ trong bài báo
     *
     * @param filePathFormat đường dẫn của tệp được định dạng có chứa xác suất ngẫu nhiên cho mỗi item
     */
    public void writeDb(String filePathFormat) {
        // Khởi tạo 1 mảng có kich thước bằng số lượng phần tử trong set items
        String[] itemsStr = new String[items.size()];

        // chuyển đổi set items thành mảng itemsStr
        items.toArray(itemsStr);

        try {
            FileWriter writer = new FileWriter(filePathFormat);

            // Ghi dòng đầu tiên
//            writer.write("TID," + String.join(",", items) + "\n");
            writer.write(String.join(" ", itemsStr) + "\n");

            // Ghi dữ liệu cho từng TID
            for (int tid = 1; tid <= transactions.size(); tid++) {
                String[] rowData = new String[itemsStr.length + 1];
                rowData[0] = String.valueOf(tid);

                // Duyệt qua từng item trong itemsStr và so sánh từng map trong transaction
                // nếu item tìm thấy trong các map của transaction thì lưu giá trị đó trong map vào mảng rowData
                // nếu không tìm thấy thì gán vào mảng rowData giá trị 0
                for (int i = 0; i < itemsStr.length; i++) {
                    T1 item = (T1) itemsStr[i];
                    if (transactions.get(tid - 1).containsKey(item)) {
                        rowData[i + 1] = String.valueOf(transactions.get(tid - 1).get(item));
                    } else {
                        rowData[i + 1] = String.valueOf('0');
                    }
                }
                // Ghi dữ liệu vào file dataset_prob
                writer.write(String.join(" ", rowData) + "\n");
            }
            writer.close();
            System.out.println("File dataset_prob.txt đã được tạo thành công.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Phương thức này in ra toàn bộ nội dung của dataset có chứa xác suất tồn tại
     */
    public void printDatabase() {
        System.out
                .println("===================  TRANSACTION DATABASE ===================");
        int count = 0;
        // for each transaction
        for (Map<T1, T2> itemset : transactions) {
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
    private void printMap(Map<T1, T2> itemset) {
        Set<T1> set = itemset.keySet();
        for (T1 key : set) {
            System.out.print(key + ":" + itemset.get(key) + " ");
        }
        System.out.println();
    }

    /**
     * Trả về số lượng transaction trong bộ dataset
     *
     * @return số lượng transaction.
     */
    public int size() {
        return transactions.size();
    }

    /**
     * Trả về danh sách các transaction trong bộ dataset
     *
     * @return một danh sách các giao dịch (một giao dịch là một Map các item - prob).
     */
    public List<Map<T1, T2>> getTransactions() {
        return transactions;
    }

    /**
     * Trả về một Set tập hợp các items có trong bộ dataset
     *
     * @return Set các items.
     */
    public Set<T1> getItems() {
        return items;
    }
}
