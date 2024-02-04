package algorithms.TWUFP.DAL;

import java.io.*;
import java.util.*;

/**
 * Dataset:
 *     + Một đối tượng Random                                                       -   Type: Random
 *     + Một danh sách để lưu trữ các item                                          -   Type: List<String>
 *     + Một map[item;weightOfItem] để lưu trữ các item và trọng số của các item    -   Type: Map<String, Double>
 *     + Một danh sách các giao dịch, mỗi giao dịch chứa các item                   -   Type: List<List<String>>
 */

public class DatasetDAL {
    /**
     * Khởi tạo một đối tượng Random
     */
    private static final Random RANDOM = new Random();

    /**
     * Khởi tạo một danh sách để lưu trữ các item
     */
    private static final List<String> items = new ArrayList<>();

    /**
     * Khởi tạo một map để lưu trữ các item và trọng số của các item
     */
    private static final Map<String, Double> weights = new HashMap<>();

    /**
     * Khởi tạo một danh sách các giao dịch
     */
    private static final List<List<String>> transactions = new ArrayList<>();

    /**
     * Phương thức này để đọc dữ liệu từ file fileData
     *
     * @param fileData tên của tệp dataset
     * @throws IOException Ngoại lệ này được ném ra nếu có lỗi xảy ra trong quá trình đọc tệp.
     */
    public static void readDataFile(String fileData) throws IOException {
        System.out.println("Reading file " + fileData + ". . .");

        // Cố định giá trị random
        RANDOM.setSeed(1);
        BufferedReader reader = null;
        try {
            // Mở file để đọc
            reader = new BufferedReader(new FileReader(
                    new File(".").getAbsoluteFile()
                            + "\\src\\algorithms\\TWUFP\\dataset\\" + fileData));
            String line;
            // Đọc từng dòng trong file
            while ((line = reader.readLine()) != null) {

                // Tách dòng thành các item
                String[] transactionItems = line.split(" ");

                // Thêm giao dịch vào danh sách giao dịch
                transactions.add(Arrays.asList(transactionItems));

                // Duyệt qua từng item trong giao dịch
                for (String item : transactionItems) {
                    // Nếu item này chưa có trong danh sách item, thêm nó vào
                    if (!items.contains(item)) {
                        items.add(item);
                        // Tạo trọng số ngẫu nhiên cho item từ (0, 1] (không bao gồm 0, bao gồm 1)
                        // và thêm vào map weight
                        double weight = RANDOM.nextDouble() + Double.MIN_VALUE;
                        weights.put(item, weight);
                    }
                }
            }
            // Sắp xếp danh sách item
            sortItemsList(items);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Phương thức này tạo ra một tệp mới và ghi trọng số của các mục vào tệp đó.
     *
     * @param fileWeight Tên của tệp mà trọng số sẽ được ghi vào.
     */
    public static void writeWeightFile(String fileWeight) {
        System.out.println("File " + fileWeight + " is generating. . .");

        PrintWriter writer = null;

        // Kiểm tra và tạo thư mục để chứa tập tin nếu nó không tồn tại
        File dir = new File(System.getProperty("user.dir") + "\\src\\algorithms\\TWUFP\\weight");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            // Khởi tạo PrintWriter để ghi vào file
            writer = new PrintWriter(new FileWriter(new File(dir, fileWeight)));

            // Ghi tất cả các items vào file
            writer.println(String.join(" ", items));

            // Duyệt qua từng item và ghi trọng số của nó vào file
            for (String item : items) {
                writer.print(weights.get(item) + " ");
            }
            writer.println();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * Phương thức này tạo ra một tệp mới và ghi xác suất của các item vào tệp đó.
     * Xác suất được tạo ngẫu nhiên cho mỗi item trong mỗi giao dịch.
     *
     * @param fileProb Tên của tệp mà xác suất sẽ được ghi vào.
     */
    public static void writeProbFile(String fileProb) {
        System.out.println("File " + fileProb + " is generating. . .");

        PrintWriter writer = null;

        // Kiểm tra và tạo thư mục để chứa tập tin nếu nó không tồn tại
        File dir = new File(System.getProperty("user.dir") + "\\src\\algorithms\\TWUFP\\dataset_prob");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Tạo tập tin để ghi xác suất
        try {
            // Khởi tạo PrintWriter để ghi vào file
            writer = new PrintWriter(new FileWriter(new File(dir, fileProb)));

            // Ghi tất cả các items vào file
            writer.println(String.join(" ", items));

            // Duyệt qua từng giao dịch
            for (List<String> transaction : transactions) {
                // Duyệt qua từng item
                for (String item : items) {
                    // Nếu giao dịch chứa item, tạo xác suất ngẫu nhiên và ghi vào file
                    if (transaction.contains(item)) {
                        // Tạo xác suất ngẫu nhiên từ (0, 1] (không bao gồm 0, bao gồm 1)
                        double prob = RANDOM.nextDouble() + Double.MIN_VALUE;
                        writer.print(prob + " ");
                    } else {
                        // Nếu giao dịch không chứa mục, ghi "0" vào file
                        writer.print("0 ");
                    }
                }
                writer.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * Lớp so sánh để sắp xếp Map theo thứ tự tăng dần theo Key, so sánh Key trong Map
     */
    public static class KeyComparator implements Comparator<String> {
        /**
         * So sánh hai chuỗi
         *
         * @param key1 Chuỗi thứ nhất cần so sánh
         * @param key2 Chuỗi thứ hai cần so sánh
         * @return -1 nếu key1 nhỏ hơn key2, 1 nếu key1 lớn hơn key2, 0 nếu chúng bằng nhau
         */
        @Override
        public int compare(String key1, String key2) {
            double num1, num2;

            // Chuyển đổi chuỗi thành số thực
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
     * Hàm để sắp xếp danh sách mục các item
     */
    public static void sortItemsList(List<String> items) {
        // Sắp xếp danh sách
        Collections.sort(items, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                double num1, num2;

                // Chuyển đổi chuỗi thành số thực
                try {
                    num1 = Double.parseDouble(a);
                } catch (NumberFormatException e) {
                    num1 = Double.NaN; // Đánh dấu chuỗi không phải số
                }
                try {
                    num2 = Double.parseDouble(b);
                } catch (NumberFormatException e) {
                    num2 = Double.NaN; // Đánh dấu chuỗi không phải số
                }

                if (Double.isNaN(num1) && Double.isNaN(num2)) {
                    return a.compareTo(b); // So sánh chuỗi nếu cả hai đều không phải số
                } else if (Double.isNaN(num1)) {
                    return 1; // Chuỗi không phải số lớn hơn số
                } else if (Double.isNaN(num2)) {
                    return -1; // Số lớn hơn chuỗi không phải số
                } else {
                    return Double.compare(num1, num2); // So sánh số
                }
            }
        });
    }

    public static List<String> getItems() {
        return items;
    }

    public static Map<String, Double> getWeights() {
        return weights;
    }

    public static List<List<String>> getTransactions() {
        return transactions;
    }
}
