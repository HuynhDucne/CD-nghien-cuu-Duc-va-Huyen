package algorithms.TUFP.GUI;

import algorithms.TUFP.DAL.DatasetDAL;

import java.io.IOException;

public class DatasetGUI {
    public static void main(String[] args) throws IOException {

        // bms-pos.txt - chạy khá lâu
        // chess.txx
        // foodmartFIM.txt
        // retail.txt
        // T10I4D100K.txt

        // fileData là tên file chứa dataset
        String fileData = "T10I4D100K.txt";

        // fileProb là tên file chứa dataset đã được định dạng có xác suất random
        String fileProb = "T10I4D100K_prob.txt";

        // Đọc dữ liệu dataset và random prob cho mỗi item
        DatasetDAL.loadFile(fileData);

        // Ghi lại dữ liệu đọc từ file dataset fileData với định dạng khác có chứa xác suất
        // vào file fileProb và lưu vào folder dataset_prob
        DatasetDAL.writeDb(fileProb);
    }
}
