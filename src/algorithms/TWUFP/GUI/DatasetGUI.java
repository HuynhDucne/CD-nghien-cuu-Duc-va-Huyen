package algorithms.TWUFP.GUI;

import algorithms.TWUFP.DAL.DatasetDAL;

import java.io.IOException;

public class DatasetGUI {
    public static void main(String[] args) throws IOException {

        // bms-pos.txt - bộ này chạy hơi lâu
        // chess.txt
        // foodmartFIM.txt
        // retail.txt
        // T10I4D100K.txt

        // fileData là tên file chứa dataset
        String fileData = "retail.txt";

        // fileProb là tên file chứa dataset đã được định dạng có xác suất random
        String fileProb = "retail_prob.txt";

        // fileWeight là tên file chứa thuộc tính weight của mỗi item
        String fileWeight = "retail_weight.txt";

        // Đọc dữ liệu dataset, random prob và weight cho mỗi item
        DatasetDAL.readDataFile(fileData);

        // Ghi lại các item và weight tương ứng của mỗi item vào file fileWeight và lưu vào folder weight
        DatasetDAL.writeWeightFile(fileWeight);

        // Ghi lại dữ liệu đọc từ file dataset fileData với định dạng khác có chứa xác suất
        // vào file fileProb và lưu vào folder dataset_prob
        DatasetDAL.writeProbFile(fileProb);
    }
}
