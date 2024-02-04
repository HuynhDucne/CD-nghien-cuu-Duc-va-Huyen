package algorithms.WUIP.GUI;

import algorithms.WUIP.DAL.Dataset;

import java.io.IOException;

//        DatasetProcessorGUI
public class DatasetFileFormat {
    public static void main(String[] args) throws IOException {

        // bms-pos.txt
        // chess.txt
        // foodmartFIM.txt
        // retail.txt
        // T10I4D100K.txt

        // fileData là tên file chứa dataset
        String fileData = "chess.txt";

        // fileProb là tên file chứa dataset đã được định dạng có xác suất random
        String fileProb = "chess_prob.txt";

        // fileProb là tên file chứa thuộc tính weight của mỗi item
        String fileWeight = "chess_weight.txt";

        // Read the chess data file
        Dataset.readChessDataFile(fileData);
        System.out.println(Dataset.getWeights());
        System.out.println(Dataset.getSortedWeights());

        // Write the chess weight file
        Dataset.writeWeightFile(fileWeight);

        // Write the chess prob file
        Dataset.writeProbFile(fileProb);
    }

}

