package algorithms.TUFP.BLL;

import java.util.List;

/**
*   CUPList: + Tên mẫu                                                              -   Type: String
*            + Tổng giá trị hỗ trợ (cumulative support value - expSup)              -   Type: double
*            + TEP-List: - TID của các giao dịch                                    -   Type: String
*                        - Xác suất tồn tại tương ứng (Existential Probability)     -   Type: double
*            + Max: giá trị xác suất tồn tại tối đa trong TEP-List                  -   Type: double
* */

public class CUPList {
    private String itemName;
    private double expSup;
    private List<TEPList> tepList;
    private double maxProb;

    public CUPList(String itemName, double expSup, List<TEPList> tepList, double maxProb) {
        this.itemName = itemName;
        this.expSup = expSup;
        this.tepList = tepList;
        this.maxProb = maxProb;
    }

    public CUPList(String itemName) {
        this.itemName = itemName;
    }

    public CUPList(String itemName, List<TEPList> tepList) {
        this.itemName = itemName;
        this.tepList = tepList;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getExpSup() {
        return expSup;
    }

    public void setExpSup(double expSup) {
        this.expSup = expSup;
    }

    public List<TEPList> getTepList() {
        return tepList;
    }

    public void setTepList(List<TEPList> tepList) {
        this.tepList = tepList;
    }

    public double getMaxProb() {
        return maxProb;
    }

    public void setMaxProb(double maxProb) {
        this.maxProb = maxProb;
    }

    /**
     * Tính tổng Existential Probability cho CUP-List của item/itemset
     *
     * @param cupList CUP-List của một item/itemset
     * @return trả về tổng xác suất của cupList
     */
    public double sumExpSup(CUPList cupList) {
//        double sum = 0.0;
//        for (TEPList tepList : cupList.getTepList()) {
//            sum += tepList.getProb();
//        }
//        sum = Math.round(sum*100.0)/100.0;
//        return sum;

        // Lấy danh sách các đối tượng TEPList từ cupList và chuyển thành Stream
        // Áp dụng phương thức getProb cho mỗi đối tượng trong Stream để lấy giá trị double
        // Tính tổng tất cả các giá trị double này
        return cupList.getTepList().stream().mapToDouble(TEPList::getProb).sum();
    }

    /**
     * Tìm Max Existential Probability cho CUP-List của item/itemset
     *
     * @param cupList CUP-List của một item/itemset
     * @return trả về xác suất lớn nhất của cupList
     */
    public double maxProb(CUPList cupList) {
//        double max = 0.0;
//        for (TEPList tepList : cupList.getTepList()) {
//            if (max < tepList.getProb()) {
//                max = tepList.getProb();
//            }
//        }
//        return max;

        // Lấy danh sách các đối tượng TEPList từ cupList và chuyển thành Stream
        // Áp dụng phương thức getProb cho mỗi đối tượng trong Stream để lấy giá trị double
        // Tìm giá trị lớn nhất trong Stream. Nếu Stream không có phần tử nào, trả về 0.0
        return cupList.getTepList().stream().mapToDouble(TEPList::getProb).max().orElse(0.0);
    }

    /**
     * In danh sách CUP-Lists
     *
     * @param cupLists danh sách CUP-Lists
     */
    public static void printCUPLists(List<CUPList> cupLists) {
        System.out.println("Print list CUP-Lists");
        for (CUPList cupList : cupLists) {
            System.out.println("Name: " + cupList.getItemName());
            System.out.println("ExpSup: " + cupList.getExpSup());
            System.out.println("MaxProb: " + cupList.getMaxProb());
            for (TEPList tepList : cupList.getTepList()) {
                System.out.println("\t\t" + tepList.getTid() + ": " + tepList.getProb());
            }
            System.out.println("-----------------------");
        }
    }

    /**
     * In CUP-List của một item/itemset
     *
     * @param cupList CUP-Lists của một item/itemset
     */
    public static void printCup(CUPList cupList) {
        System.out.println("Name: " + cupList.getItemName());
        System.out.println("ExpSup: " + cupList.getExpSup());
        System.out.println("MaxProb: " + cupList.getMaxProb());
        for (TEPList tepList : cupList.getTepList()) {
            System.out.println("\t\t" + tepList.getTid() + ": " + tepList.getProb());
        }
        System.out.println("-----------------------");
    }
}
