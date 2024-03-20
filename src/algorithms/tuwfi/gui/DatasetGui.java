package algorithms.tuwfi.gui;

import algorithms.tuwfi.dal.DatasetDal;

import java.io.IOException;

public class DatasetGui {
    public static void main(String[] args) throws IOException {

        /**
         * Dataset:
         * foodmart.txt
         * retail.txt
         * T10I4D100K.txt
         * chess.txt
         */

        // originFile là tên file chứa dataset gốc
        String originFile = "foodmart.txt";

        // probFile là tên file chứa dataset đã được định dạng có xác suất random
        String probFile = "foodmart_prob.txt";

        // weightFile là tên file chứa thuộc tính weight của mỗi item
        String weightFile = "foodmart_weight.txt";

        // Đọc dữ liệu dataset, random prob và weight cho mỗi item
        DatasetDal.readDataFile(originFile);

        // Ghi lại các item và weight tương ứng của mỗi item vào file weightFile và lưu vào folder src/dataset/weight
        DatasetDal.writeWeightFile(weightFile);

        // Ghi lại dữ liệu đọc từ file dataset originFile với định dạng khác có chứa xác suất
        // vào file probFile và lưu vào folder src/dataset/probability
        DatasetDal.writeProbFile(probFile);
    }
}
