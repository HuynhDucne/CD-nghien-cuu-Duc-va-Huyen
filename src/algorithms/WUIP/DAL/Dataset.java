package algorithms.WUIP.DAL;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Dataset {
    private static final Random RANDOM = new Random();
    private static final List<String> items = new ArrayList<>();
    private static final Map<String, Double> weights = new HashMap<>();
    private static final Map<String, Double> sortedWeights = new LinkedHashMap<>();
    private static final List<List<String>> transactions = new ArrayList<>();

    public static void readChessDataFile(String fileData) throws IOException {
        System.out.println("Reading file " + fileData + ". . .");
        RANDOM.setSeed(1);
        try (BufferedReader reader = new BufferedReader(new FileReader(
                new File(".").getAbsoluteFile()
                        + "\\src\\algorithms\\TWUFP\\dataset\\" + fileData))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] transactionItems = line.split(" ");
                transactions.add(Arrays.asList(transactionItems));
                for (String item : transactionItems) {
                    if (!items.contains(item)) {
                        items.add(item);
                        double weight = Math.round((RANDOM.nextDouble() + Double.MIN_VALUE) * 100.0) / 100.0;
                        weights.put(item, weight);
                    }
                }
            }
            sortedWeights.putAll(sortMapByValue(weights));
        }
    }

    public static Map<String, Double> sortMapByValue(Map<String, Double> unsortedMap) {
        // Sắp xếp Map theo giá trị bằng cách sử dụng Stream API
        Map<String, Double> sortedMap = unsortedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));

        return sortedMap;
    }

    public static void writeWeightFile(String fileWeight) throws IOException {
        System.out.println("File " + fileWeight + " is generating. . .");

        // Kiểm tra và tạo thư mục để chứa tập tin nếu nó không tồn tại
        File dir = new File(System.getProperty("user.dir") + "\\src\\algorithms\\WUIP\\weight");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Tạo tập tin để ghi trọng số
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(dir, fileWeight)))) {
            writer.println(String.join(" ", sortedWeights.keySet()));
            for (String item : sortedWeights.keySet()) {
                writer.print(sortedWeights.get(item) + " ");
            }
            writer.println();
        }
    }

    public static void writeProbFile(String fileProb) throws IOException {
        System.out.println("File " + fileProb + " is generating. . .");

        // Kiểm tra và tạo thư mục để chứa tập tin nếu nó không tồn tại
        File dir = new File(System.getProperty("user.dir") + "\\src\\algorithms\\WUIP\\dataset_prob");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Tạo tập tin để ghi xác suất
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(dir, fileProb)))) {
            writer.println(String.join(" ", sortedWeights.keySet()));
            for (List<String> transaction : transactions) {
                for (String item : sortedWeights.keySet()) {
                    if (transaction.contains(item)) {
                        // Tạo số ngẫu nhiên từ (0, 1] (không bao gồm 0, bao gồm 1)
                        double prob = Math.round((RANDOM.nextDouble() + Double.MIN_VALUE) * 100.0) / 100.0;
                        writer.print(prob + " ");
                    } else {
                        writer.print("0 ");
                    }
                }
                writer.println();
            }
        }
    }

    public static List<String> getItems() {
        return items;
    }

    public static Map<String, Double> getWeights() {
        return weights;
    }

    public static Map<String, Double> getSortedWeights() {
        return sortedWeights;
    }

    public static List<List<String>> getTransactions() {
        return transactions;
    }

}
