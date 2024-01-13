package algorithms.TUFP.GUI;

import algorithms.TUFP.DAL.Dataset;

import java.io.IOException;

public class DatasetFileFormat {
    public static void main(String[] args) throws IOException {

        // bms-pos.txt
        // chess.txx
        // foodmartFIM.txt
        // retail.txt
        // T10I4D100K.txt

        // filePath là tên file chứa dataset
        String filePath = "retail.txt";

        // filePathFormat là tên file chứa dataset đã được định dạng có xác suất random
        String filePathFormat = "retail_prob.txt";

        // Đọc dữ liệu dataset và random prob cho mỗi item
        Dataset.loadFile(filePath);

        // Ghi lại dữ liệu đọc từ file dataset filePath với định dạng khác có chứa xác suất
        // vào file filePathFormat và lưu vào folder dataset_prob
        Dataset.writeDb(filePathFormat);
    }
}
