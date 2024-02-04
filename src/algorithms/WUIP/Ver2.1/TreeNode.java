import java.util.ArrayList;
import java.util.List;

class TreeNode<T1> {
    T1 item_name;
    double TpCap;
    double pProxy;
    List<TreeNode<T1>> child_list;

    public TreeNode(T1 item_name, double TpCap, double pProxy) {
        this.item_name = item_name;
        this.TpCap = TpCap;
        this.pProxy = pProxy;
        this.child_list = new ArrayList<>();
    }

    public T1 getItem_name() {
        return item_name;
    }

    public void setItem_name(T1 item_name) {
        this.item_name = item_name;
    }

    public double getTpCap() {
        return TpCap;
    }

    public void setTpCap(double tpCap) {
        TpCap = tpCap;
    }

    public double getpProxy() {
        return pProxy;
    }

    public void setpProxy(double pProxy) {
        this.pProxy = pProxy;
    }

    public List<TreeNode<T1>> getChild_list() {
        return child_list;
    }

    public void setChild_list(List<TreeNode<T1>> child_list) {
        this.child_list = child_list;
    }

    public void addChild(T1 itemName, double tpCap, double proxy) {
        TreeNode<T1> newChild = new TreeNode<>(itemName, tpCap, proxy);
        this.child_list.add(newChild);
    }

    public TreeNode<T1> findChild(T1 itemName) {
        for(TreeNode<T1> child : child_list) {
            if(itemName.equals(child.item_name)) {
                return child;
            }
        }
        return null;
    }
}