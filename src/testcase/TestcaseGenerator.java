package testcase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class TestcaseGenerator {
    /**
     * Khởi tạo một danh sách để lưu trữ các item
     */
    private static final String[] ITEMS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    /**
     * Khởi tạo kích thước của transaction
     */
    private static final int SIZE_TRANSACTION = 10;

    /**
     * Phương thức này tạo ra một tệp mới và ghi xác suất của các item vào tệp đó.
     * Xác suất được tạo ngẫu nhiên cho mỗi item trong mỗi giao dịch.
     *
     * @param probFile Tên của tệp mà xác suất sẽ được ghi vào.
     */
    public static void writeProbFile(String probFile) {
        System.out.println("File " + probFile + " is generating. . .");
        Random random = new Random();

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

        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(dir, probFile)))) {

            // Ghi các items vào dòng đầu tiên
            writer.println(String.join(" ", ITEMS));

            // Tạo xác suất ngẫu nhiên cho từng item trong mỗi giao dịch
            for (int i = 0; i < SIZE_TRANSACTION; i++) {
                for (int j = 0; j < ITEMS.length; j++) {
                    double probability = (double) Math.round(random.nextDouble() * 10) / 10;
                    writer.print(probability + " ");
                }
                writer.println();
            }

            System.out.println(probFile + " file generated successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
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
        Random random = new Random();

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
            for (int i = 0; i < ITEMS.length; i++) {
                double weight;
                do {
                    weight = (double) Math.round((random.nextDouble() + Double.MIN_VALUE) * 10) / 10;
                } while (weight == 0.0);
                writer.print(weight + " ");
            }
            writer.println();
            System.out.println(weightFile + " file generated successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        for (int i = 1; i <= 30; i++) {
            // probFile là tên file chứa dataset đã được định dạng có xác suất random
            String probFile = "testcase" + i + "_prob.txt";
            // weightFile là tên file chứa thuộc tính weight của mỗi item
            String weightFile = "testcase" + i + "_weight.txt";

            // Ghi lại các item và weight tương ứng của mỗi item
            // vào file weightFile và lưu vào folder src/dataset/weight
            writeWeightFile(weightFile);

            // Ghi lại các item và xác suất ngẫu nhiên tương ứng của mỗi item
            // vào file probFile và lưu vào folder src/dataset/probability
            writeProbFile(probFile);
        }
    }
}
