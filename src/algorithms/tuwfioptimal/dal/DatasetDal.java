package algorithms.tuwfioptimal.dal;

import java.io.*;
import java.util.*;

/**
 * DatasetDal:
 *     + Một đối tượng Random                                                       -   Type: Random
 *     + Một danh sách để lưu trữ các item                                          -   Type: List<String>
 *     + Một map[item;weightOfItem] để lưu trữ các item và trọng số của các item    -   Type: Map<String, Double>
 *     + Một danh sách các giao dịch, mỗi giao dịch chứa các item                   -   Type: List<List<String>>
 */

public class DatasetDal {
    /**
     * Khởi tạo một đối tượng Random
     */
    private static final Random RANDOM = new Random();

    /**
     * Khởi tạo một danh sách để lưu trữ các item
     */
    private static final List<String> ITEMS = new ArrayList<>();

    /**
     * Khởi tạo một map để lưu trữ các item và trọng số của các item
     */
    private static final Map<String, Double> WEIGHTS = new HashMap<>();

    /**
     * Khởi tạo một danh sách các giao dịch
     */
    private static final List<List<String>> TRANSACTIONS = new ArrayList<>();

    /**
     * Phương thức này để đọc dữ liệu từ file dataFile trong folder dataset/origin
     *
     * @param dataFile tên của tệp dataset
     */
    public static void readDataFile(String dataFile) {
        System.out.println("Reading file " + dataFile + ". . .");

        // Cố định giá trị random
        RANDOM.setSeed(1);

        // Mở file data gốc để đọc
        try (BufferedReader reader = new BufferedReader(new FileReader("src/dataset/origin/" + dataFile))) {
            String line;
            // Đọc từng dòng trong file
            while ((line = reader.readLine()) != null) {
                // Tách dòng thành các item
                String[] transactionItems = line.split(" ");
                // Thêm giao dịch vào danh sách giao dịch
                TRANSACTIONS.add(Arrays.asList(transactionItems));
                // Duyệt qua từng item trong giao dịch
                for (String item : transactionItems) {
                    // Nếu item này chưa có trong danh sách item, thêm nó vào
                    if (!ITEMS.contains(item)) {
                        ITEMS.add(item);
                        // Tạo trọng số ngẫu nhiên cho item từ (0, 1] (không bao gồm 0, bao gồm 1)
                        // và thêm vào map weight
                        double weight = RANDOM.nextDouble() + Double.MIN_VALUE;
                        WEIGHTS.put(item, weight);
                    }
                }
            }
            // Sắp xếp danh sách item
            sortItemsList(ITEMS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Phương thức này tạo ra một tệp mới và ghi trọng số của các mục vào tệp đó.
     *
     * @param weightFile Tên của tệp mà trọng số sẽ được ghi vào.
     */
    public static void writeWeightFile(String weightFile) {
        System.out.println("File " + weightFile + " is generating. . .");

        // Lấy đường dẫn thư mục
        File dir = new File(System.getProperty("user.dir") + "/src/dataset/weight");

        // Kiểm tra nếu thư mục đó không tồn tại thì tạo thư mục đó để chứa tập tin weightFile
        // nếu tạo không thành công sẽ báo lỗi
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.err.println("Failed to create directory for weight files.");
                return; // Không thể tạo thư mục, thoát khỏi phương thức
            }
        }

        // Tạo tập tin để ghi weight
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(dir, weightFile)))) {

            // Ghi tất cả các items vào file
            writer.println(String.join(" ", ITEMS));

            // Duyệt qua từng item và ghi trọng số của nó vào file
            for (String item : ITEMS) {
                writer.print(WEIGHTS.get(item) + " ");
            }
            writer.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Phương thức này tạo ra một tệp mới và ghi xác suất của các item vào tệp đó.
     * Xác suất được tạo ngẫu nhiên cho mỗi item trong mỗi giao dịch.
     *
     * @param probFile Tên của tệp mà xác suất sẽ được ghi vào.
     */
    public static void writeProbFile(String probFile) {
        System.out.println("File " + probFile + " is generating. . .");

        // Lấy đường dẫn thư mục
        File dir = new File(System.getProperty("user.dir") + "/src/dataset/probability");

        // Kiểm tra nếu thư mục đó không tồn tại thì tạo thư mục đó để chứa tập tin probFile
        // nếu tạo không thành công sẽ báo lỗi
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.err.println("Failed to create directory for probability files.");
                return; // Không thể tạo thư mục, thoát khỏi phương thức
            }
        }

        // Tạo tập tin để ghi xác suất
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(dir, probFile)))) {
            // Ghi tất cả các items vào file
            writer.println(String.join(" ", ITEMS));

            // Duyệt qua từng giao dịch
            for (List<String> transaction : TRANSACTIONS) {
                // Tạo một set từ danh sách giao dịch để kiểm tra sự có mặt của mỗi mục trong danh sách
                Set<String> transactionSet = new HashSet<>(transaction);
                // Duyệt qua từng item
                for (String item : ITEMS) {
                    // Nếu giao dịch chứa item, tạo xác suất ngẫu nhiên và ghi vào file
                    if (transactionSet.contains(item)) {
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
        }
    }

    /**
     * Lớp so sánh để sắp xếp Map theo thứ tự tăng dần theo Key, so sánh Key trong Map
     */
    public static class KeyComparator implements Comparator<String> {
        /**
         * So sánh hai chuỗi
         * Chuỗi có giá trị số < chuỗi không phải giá trị số
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
     * Phương thức này để sắp xếp danh sách items theo thứ tự tăng dần
     *
     * @param items danh sách item ban đầu
     */
    public static void sortItemsList(List<String> items) {
        // Sắp xếp danh sách
        items.sort(new Comparator<String>() {
            /**
             * So sánh hai chuỗi.
             * Chuỗi có giá trị số < chuỗi không phải giá trị số
             *
             * @param a Chuỗi thứ nhất cần so sánh
             * @param b Chuỗi thứ hai cần so sánh
             * @return -1 nếu a nhỏ hơn b, 1 nếu a lớn hơn b, 0 nếu chúng bằng nhau
             */
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
        return ITEMS;
    }

    public static Map<String, Double> getWeights() {
        return WEIGHTS;
    }

    public static List<List<String>> getTransactions() {
        return TRANSACTIONS;
    }
}
