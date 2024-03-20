package algorithms.tuwfi.dto;

import java.util.List;

/**
 * CUP:
 * + patternName: Tên của mẫu                                                         -   Type: String
 * + cumSup: Tổng giá trị hỗ trợ (cumulative support value)                           -   Type: double
 * + tepList: Danh sách TEP-List.
 *            Mỗi TEP gồm: - TID của các giao dịch                                    -   Type: String
 *                         - Xác suất tồn tại tương ứng (Existential Probability)     -   Type: double
 * + calculateMaxProb: Giá trị xác suất tồn tại tối đa trong TEP-List                 -   Type: double
 * + weight: Trọng số của mẫu                                                         -   Type: double
 * + cumWeightSup: Tổng giá trị hỗ trợ có trọng số                                    -   Type: double
 */

public class Cup {
    private String patternName;
    private double cumSup;
    private List<Tep> tepList;
    private double maxProb;
    private double weight;
    private double cumWeightSup;

    public Cup(String patternName, double cumSup, List<Tep> tepList, double maxProb, double weight, double cumWeightSup) {
        this.patternName = patternName;
        this.cumSup = cumSup;
        this.tepList = tepList;
        this.maxProb = maxProb;
        this.weight = weight;
        this.cumWeightSup = cumWeightSup;
    }

    public Cup(String patternName) {
        this.patternName = patternName;
    }

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }

    public double getCumSup() {
        return cumSup;
    }

    public void setCumSup(double cumSup) {
        this.cumSup = cumSup;
    }

    public List<Tep> getTepList() {
        return tepList;
    }

    public void setTepList(List<Tep> tepList) {
        this.tepList = tepList;
    }

    public double getMaxProb() {
        return maxProb;
    }

    public double getCumWeightSup() {
        return cumWeightSup;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setCumWeightSup(double cumWeightSup) {
        this.cumWeightSup = cumWeightSup;
    }

    public void setMaxProb(double maxProb) {
        this.maxProb = maxProb;
    }

    /**
     * Tính tổng Existential Probability cho CUP của pattern
     *
     * @return trả về tổng xác suất của CUP
     */
    public double calculateCumSup() {
        // Lấy danh sách các đối tượng TEPList từ cup và chuyển thành Stream
        // Áp dụng phương thức getProb cho mỗi đối tượng trong Stream để lấy giá trị double
        // Tính tổng tất cả các giá trị double này
        return tepList.stream().mapToDouble(Tep::getProb).sum();
    }

    /**
     * Tìm Max Existential Probability cho CUP của pattern
     *
     * @return trả về xác suất lớn nhất của CUP
     */
    public double calculateMaxProb() {
        // Lấy danh sách các đối tượng TEPList từ cup và chuyển thành Stream
        // Áp dụng phương thức getProb cho mỗi đối tượng trong Stream để lấy giá trị double
        // Tìm giá trị lớn nhất trong Stream. Nếu Stream không có phần tử nào, trả về 0.0
        return tepList.stream().mapToDouble(Tep::getProb).max().orElse(0.0);
    }

    /**
     * In CUP-List (Danh sách CUP)
     *
     * @param cupList CUP-List
     */
    public static void printCupList(List<Cup> cupList) {
        System.out.println("Print CUP-List");
        for (Cup cup : cupList) {
            printCup(cup);
        }
    }

    /**
     * In CUP của một pattern
     *
     * @param cup CUP của một pattern
     */
    public static void printCup(Cup cup) {
        System.out.printf("Name: %s%n", cup.getPatternName());
        System.out.printf("CumSup: %.2f%n", cup.getCumSup());
        System.out.printf("Weight: %.2f%n", cup.getWeight());
        System.out.printf("CumWeightSup: %.2f%n", cup.getCumWeightSup());
        System.out.printf("MaxProb: %.2f%n", cup.getMaxProb());
        for (Tep tep : cup.getTepList()) {
            System.out.printf("\t\t%s: %.2f%n", tep.getTid(), tep.getProb());
        }
        System.out.println("-----------------------");
    }
}
