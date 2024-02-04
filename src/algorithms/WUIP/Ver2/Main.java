package algorithms.WUIP.Ver2;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Tạo một cơ sở dữ liệu mẫu
        List<List<Item<String>>> UDB = new ArrayList<>();

        List<Item<String>> transaction1 = new ArrayList<>();
        transaction1.add(new Item<>("D", 0.6));
        transaction1.add(new Item<>("A", 0.5));
        transaction1.add(new Item<>("C", 0.4));
        transaction1.add(new Item<>("B", 0.9));
        transaction1.add(new Item<>("E", 0.3));
        transaction1.add(new Item<>("F", 0.4));
        transaction1.add(new Item<>("G", 0.3));
        UDB.add(transaction1);

        List<Item<String>> transaction2 = new ArrayList<>();
        transaction2.add(new Item<>("D", 0.5));
        transaction2.add(new Item<>("A", 0.6));
        transaction2.add(new Item<>("C", 0.4));
        transaction2.add(new Item<>("B", 0.3));
        transaction2.add(new Item<>("F", 0.5));
        transaction2.add(new Item<>("G", 0.5));
        UDB.add(transaction2);

        List<Item<String>> transaction3 = new ArrayList<>();
        transaction3.add(new Item<>("D", 0.2));
        transaction3.add(new Item<>("A", 0.7));
        transaction3.add(new Item<>("C", 0.9));
        transaction3.add(new Item<>("E", 0.4));
        transaction3.add(new Item<>("G", 0.4));
        UDB.add(transaction3);

        List<Item<String>> transaction4 = new ArrayList<>();
        transaction4.add(new Item<>("D", 0.2));
        transaction4.add(new Item<>("A", 0.9));
        transaction4.add(new Item<>("C", 0.5));
        transaction4.add(new Item<>("E", 0.6));
        UDB.add(transaction4);

        List<Item<String>> transaction5 = new ArrayList<>();
        transaction5.add(new Item<>("D", 0.9));
        transaction5.add(new Item<>("A", 0.6));
        transaction5.add(new Item<>("C", 0.6));
        transaction5.add(new Item<>("B", 0.3));
        transaction5.add(new Item<>("F", 0.4));
        transaction5.add(new Item<>("G", 0.4));
        UDB.add(transaction5);

        List<Item<String>> transaction6 = new ArrayList<>();
        transaction6.add(new Item<>("D", 0.5));
        transaction6.add(new Item<>("B", 0.3));
        transaction6.add(new Item<>("E", 0.6));
        transaction6.add(new Item<>("F", 0.2));
        transaction6.add(new Item<>("G", 0.2));
        UDB.add(transaction6);

        List<Item<String>> transaction7 = new ArrayList<>();
        transaction7.add(new Item<>("D", 0.4));
        transaction7.add(new Item<>("B", 0.2));
        transaction7.add(new Item<>("E", 0.2));
        transaction7.add(new Item<>("F", 0.3));
        transaction7.add(new Item<>("G", 0.1));
        UDB.add(transaction7);

        List<Item<String>> transaction8 = new ArrayList<>();
        transaction8.add(new Item<>("D", 0.7));
        transaction8.add(new Item<>("B", 0.6));
        transaction8.add(new Item<>("E", 0.2));
        transaction8.add(new Item<>("F", 0.2));
        UDB.add(transaction8);

        double d = 0.0;

//        for(List<Item<String>> transaction : UDB) {
//            for(Item<String> tr : transaction) {
//                System.out.print(tr.getTid() + "-" + tr.getProb() + " ");
//            }
//            System.out.println();
//        }


        // Gọi phương thức xây dựng cây TPN
        TPNTreeProcessor<String> tpnTreeProcessor = new TPNTreeProcessor<>();
        TreeNode<String> tpnTree = tpnTreeProcessor.buildTPNTree(UDB, d);
        // In cây TPN
        System.out.println("Cây TPN:");
        printTree(tpnTree, "", true);

        // Gọi phương thức calculateI1 từ đối tượng tpnTreeProcessor
        List<Item<String>> I1 = tpnTreeProcessor.calculateI1(UDB, d);

        // In tập hợp các mục thường xuất hiện dự kiến I1
//        System.out.print("Tap hop cac muc thuong xuat hien du kien I1: ");
//        for(Item<String> i : I1) {
//            System.out.printf("%s-%.2f ", i.getTid(), tpnTreeProcessor.calculateExpSup(i, UDB));
//
//        }

    }

    // Phương thức in cây
    private static void printTree(TreeNode<String> node, String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") +
                node.getItem_name() + " (TpCap: " + node.getTpCap() + ", pProxy: " + node.getpProxy() + ")");

        List<TreeNode<String>> children = node.getChild_list();
        for(int i = 0; i < children.size() - 1; i++) {
            printTree(children.get(i), prefix + (isTail ? "    " : "│   "), false);
        }
        if(!children.isEmpty()) {
            printTree(children.get(children.size() - 1), prefix + (isTail ? "    " : "│   "), true);
        }
    }
}

