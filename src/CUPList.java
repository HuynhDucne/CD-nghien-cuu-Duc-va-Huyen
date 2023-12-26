import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Map;

/**
*   CUPList: + Tên mẫu                                                              -   Type: T1
*            + Tổng giá trị hỗ trợ (cumulative support value - expSup)              -   Type: T3
*            + TEP-List: - TID của các giao dịch                                    -   Type: T2
*                        - Xác suất tồn tại tương ứng (Existential Probability)     -   Type: T3
*            + Max: giá trị xác suất tồn tại tối đa trong TEP-List                  -   Type: T3
* */

public class CUPList<T1, T2, T3> {
    private T1 itemName;
    private T3 expSup;
    private List<TEPList<T2, T3>> tepList;
    private T3 maxProb;

    public CUPList(T1 itemName, List<TEPList<T2, T3>> tepList) {
        this.itemName = itemName;
        this.tepList = tepList;
    }

    public CUPList(T1 itemName, T3 expSup, List<TEPList<T2, T3>> tepList, T3 maxProb) {
        this.itemName = itemName;
        this.expSup = expSup;
        this.tepList = tepList;
        this.maxProb = maxProb;
    }

    public T1 getItemName() {
        return itemName;
    }

    public void setItemName(T1 itemName) {
        this.itemName = itemName;
    }

    public T3 getExpSup() {
        return expSup;
    }

    public void setExpSup(T3 expSup) {
        this.expSup = expSup;
    }

    public List<TEPList<T2, T3>> getTepList() {
        return tepList;
    }

    public void setTepList(List<TEPList<T2, T3>> tepList) {
        this.tepList = tepList;
    }

    public T3 getMaxProb() {
        return maxProb;
    }

    public void setMaxProb(T3 maxProb) {
        this.maxProb = maxProb;
    }

    /**
     * Tính tổng Existential Probability cho CUP-Lists
     * */
    public T3 sumExpSup(CUPList<T1, T2, T3> cupList) {
        Double sum = 0.0;
        for (TEPList<T2, T3> tepList : cupList.getTepList()) {
            sum += (Double) tepList.getProb();
        }
        sum = Math.round(sum*100.0)/100.0;
        return (T3) sum;
    }

    /**
     * Tìm Max Existential Probability cho CUP-Lists
     * */
    public T3 maxProb(CUPList<T1, T2, T3> cupList) {
        Double max = 0.0;
        for (TEPList<T2, T3> tepList : cupList.getTepList()) {
            if (max < (Double) tepList.getProb()) {
                max = (Double) tepList.getProb();
            }
        }
        return (T3) max;
    }

    /**
     * Đọc file db.txt và chuyển thành cấu trúc dữ liệu CUP-Lists
     * */
//    public static <T1, T2, T3> List<CUPList<T1, T2, T3>> readDataAndConvertToCUPLists(String filePath) {
//        List<CUPList<T1, T2, T3>> cupLists = new ArrayList<>();
//        List<List<T3>> probs = new ArrayList<>();
//
//        BufferedReader reader;
//        try {
//            reader = new BufferedReader(new FileReader(filePath));
//
//            // Đọc dòng đầu tiên để lấy danh sách itemsName
//            String[] itemsName = reader.readLine().split(" ");
//
//            // Đọc từng dòng còn lại và chuẩn hoá dữ liệu thành List
//            String line;
//            while ((line = reader.readLine()) != null) {
//
//                // Lấy danh sách các prob trong 1 TID
//                String[] probsStr = line.split(" ");
//
//                List<T3> probsOfTID = new ArrayList<>();
//
//                for (int i = 0; i < probsStr.length; i++) {
//                    T3 item = (T3) Double.valueOf(probsStr[i]);
//                    probsOfTID.add(item);
//                }
//
//                // gộp từng danh sách các prob trong mỗi TID lại thành 1 danh sách mới
//                probs.add(probsOfTID);
//                // => [[prob_Of_TID_1]  , [prob_Of_TID_2], [prob_Of_TID_3], ...]
//                // => [[1.0,0,0.9,0.6,0,0,0,0], [0.9,0.9,0.7,0.6,0.4,0,0,0], [0,0.5,0.8,0.9,0,0.2,0.4,0], [], [],..]
//            }
//
//            // chuyển sang cấu trúc CUP-Lists
//            int i = 0;
//            // duyệt qua mỗi TID
//            while (i < probs.get(0).size()) {
//                List<TEPList<T2, T3>> tepList = new ArrayList<>();
//                Integer tid = 1;
//                Double probNum = 0.0;
//
//                // Duyệt qua từng prob của 1 item
//                for (List<T3> innerProbs : probs) {
//                    T3 prob = innerProbs.get(i);
//                    probNum = (Double) prob;
//
//                    // Bỏ các TID có prob = 0
//                    if (probNum != 0.0) {
//                        tepList.add(new TEPList<>((T2) tid, prob));
//                    }
//                    tid++;
//                }
//
//                // Tạo đối tượng CUPList và thêm vào danh sách
//                CUPList<T1, T2, T3> cupList = new CUPList<>((T1) itemsName[i], tepList);
//                cupList.setExpSup(cupList.sumExpSup(cupList));
//                cupList.setMaxProb(cupList.maxProb(cupList));
//                cupLists.add(cupList);
//                i++;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return cupLists;
//    }

    public static <T1, T2, T3> List<CUPList<T1, T2, T3>> readDatasetToCupLists(Dataset<T1, T3> dataset) {
        List<CUPList<T1, T2, T3>> cupLists = new ArrayList<>();

        for (T1 item : dataset.getItems()) {
            List<TEPList<T2, T3>> tepList = new ArrayList<>();
            Integer tid = 1;
            for (Map<T1, T3> trans : dataset.getTransactions()) {
                if (trans.containsKey(item)) {

                    tepList.add(new TEPList<>((T2) tid, trans.get(item)));

                    tid++;
                }
            }
            // Tạo đối tượng CUPList và thêm vào danh sách
            CUPList<T1, T2, T3> cupList = new CUPList<>(item, tepList);
            cupList.setExpSup(cupList.sumExpSup(cupList));
            cupList.setMaxProb(cupList.maxProb(cupList));
            cupLists.add(cupList);
        }
        return cupLists;
    }

    public static <T1, T2, T3> void printCUPLists(List<CUPList<T1, T2, T3>> cupLists) {
        System.out.println("In cấu trúc CUP-Lists");
        for (CUPList<T1, T2, T3> cupList : cupLists) {
            System.out.println("Name: " + cupList.getItemName());
            System.out.println("ExpSup: " + cupList.getExpSup());
            System.out.println("MaxProb: " + cupList.getMaxProb());
            for (TEPList<T2, T3> tepList : cupList.getTepList()) {
                System.out.println("\t\t" + tepList.getTid() + ": " + tepList.getProb());
            }
            System.out.println("-----------------------");
        }
    }

}
