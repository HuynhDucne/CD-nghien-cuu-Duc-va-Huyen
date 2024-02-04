import java.util.*;

public class TPNTreeProcessor<T1> {
    private T1 Null;
    private final double INFINITY = Double.POSITIVE_INFINITY;
    // Phương thức xây dựng cây TPN từ dữ liệu UDB và giá trị d
    public TreeNode<T1> buildTPNTree(List<List<Item<T1>>> UDB, double d) {
        // Tính toán I1, danh sách các mục có ExpSupi lớn hơn hoặc bằng d × |UDB|
        List<Item<T1>> I1 = calculateI1(UDB, d);
        I1.sort((item1, item2) -> compareExpSup(item1, item2, UDB));

        // Khởi tạo nút gốc của cây TPN
        TreeNode<T1> TPN_tree = new TreeNode<T1>(Null, 0, 0);
//        for(Item<T1> i : I1) {
//            System.out.print(i.getTid() + "-" + i.getProb() + " ");
//        }
//
//        System.out.println();

        // Duyệt qua mỗi giao dịch trong UDB để xây dựng cây
        for(List<Item<T1>> transaction : UDB) {
            List<Item<T1>> t = new ArrayList<>();
            for(Item<T1> item : transaction) {
                if(I1.stream().anyMatch(i -> i.getTid().equals(item.getTid()))) {
                    t.add(item);
                }
            }
            // Nếu giao dịch không rỗng sau khi giữ lại các mục của I1, thêm vào cây
            if(!t.isEmpty()) {
                t.sort(Comparator.comparingInt(item -> I1.indexOf(item.getTid())));
                insertTree(t, TPN_tree);
            }
        }


        // Trả về cây TPN đã xây dựng
        return TPN_tree;
    }

    // So sánh hai mục item1 và item2 dựa trên ExpSup trong UDB
    private int compareExpSup(Item<T1> item1, Item<T1> item2, List<List<Item<T1>>> UDB) {
        double expSup1 = calculateExpSup(item1, UDB);
        double expSup2 = calculateExpSup(item2, UDB);

        // Sắp xếp theo thứ tự giảm dần của ExpSup
        return Double.compare(expSup2, expSup1);
    }


    // Tính toán danh sách I1, chứa các mục có ExpSupi lớn hơn hoặc bằng d × |UDB|
    public List<Item<T1>> calculateI1(List<List<Item<T1>>> UDB, double d) {
        Map<T1, Double> itemSupport = new HashMap<>();

        // Tính hỗ trợ cho mỗi mục trong tất cả các giao dịch
        for(List<Item<T1>> transaction : UDB) {
            for(Item<T1> item : transaction) {
                T1 itemName = item.getTid();
                Double itemValue = item.getProb();

                // Cập nhật giá trị hỗ trợ cho mục
                itemSupport.put(itemName, itemSupport.getOrDefault(itemName, 0.0) + itemValue);
            }
        }

        // Lọc các mục có ExpSupi nhỏ hơn hoặc bằng d × |UDB|
        List<Item<T1>> I1 = new ArrayList<>();
        int transactionCount = UDB.size();
        for(Map.Entry<T1, Double> entry : itemSupport.entrySet()) {
            if(entry.getValue() >= d * transactionCount) {
                I1.add(new Item<>(entry.getKey(), entry.getValue()));
            }
        }

        // Sắp xếp theo thứ tự giảm dần của ExpSup
        I1.sort(Comparator.comparingDouble((Item<T1> item) -> item.getProb()).reversed());
        // Trả về danh sách I1
        return I1;
    }


    // Tính toán ExpSup của một mục trong UDB
    public double calculateExpSup(Item<T1> item, List<List<Item<T1>>> UDB) {
        double sumProb = 0.0;

        // Duyệt qua từng giao dịch trong UDB
        for(List<Item<T1>> transaction : UDB) {
            // Duyệt qua từng mục trong giao dịch
            for(Item<T1> transactionItem : transaction) {
                // Kiểm tra xem mục có tồn tại trong giao dịch không
                if(transactionItem.getTid().equals(item.getTid())) {
                    // Tăng tổng giá trị xác suất
                    sumProb += transactionItem.getProb();
                    break;// Break vòng lặp khi tìm thấy mục trong giao dịch
                }
            }
        }
        // Trả về tổng giá trị xác suất
        return sumProb;
    }

    // Chèn một giao dịch vào cây TPN
    private void insertTree(List<Item<T1>> t, TreeNode<T1> tree) {
        TreeNode<T1> currentNode = tree;

        // Duyệt qua mỗi mục trong giao dịch và cập nhật cây TPN
        for(Item<T1> i : t) {
            TreeNode<T1> child = currentNode.findChild(i.getTid());

            // Nếu mục đã tồn tại, cập nhật giá trị TpCap và pProxy
            if(child != null) {
                child.setTpCap(child.getTpCap() + TpCap(t, i));
                child.setpProxy(Math.max(TpProxy(t, i), child.getpProxy()));
                currentNode = child;
            } else {
                // Nếu mục chưa tồn tại, thêm mục mới vào cây
                currentNode.addChild(i.getTid(), TpCap(t, i), TpProxy(t, i));
                currentNode = currentNode.findChild(i.getTid());
            }
        }
    }

    // Tính toán giá trị TpCap cho một mục trong giao dịch
    private double TpCap(List<Item<T1>> transaction, Item<T1> item) {
        double tpCap = Double.NEGATIVE_INFINITY;

        for(Item<T1> t : transaction) {
            if(t.getTid().equals(item.getTid())) {
                break;
            }
            tpCap = Math.max(tpCap, t.getProb());
        }

        if(tpCap == Double.NEGATIVE_INFINITY) {
            return Double.POSITIVE_INFINITY;
        } else {
            return tpCap * item.getProb();
        }
    }

    // Tính toán giá trị TpProxy cho một mục trong giao dịch
    private double TpProxy(List<Item<T1>> transaction, Item<T1> item) {
        double tpProxy = 0.0;
        
        List<Double> p = new ArrayList<Double>();
        for(Item<T1> t : transaction) {
            if(t.getTid().equals(item.getTid())) {
                break;
            }
            p.add(t.getProb());
        }
        p.sort(Collections.reverseOrder());

        if(p.size() >= 2) {
            tpProxy = p.get(1);
        }
        return tpProxy;
    }

}
