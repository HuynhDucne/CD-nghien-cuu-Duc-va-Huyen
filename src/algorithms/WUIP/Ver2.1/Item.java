public class Item<T1> {
    private T1 tid;
    private Double prob;

    public Item(T1 tid, Double prob) {
        this.tid = tid;
        this.prob = prob;
    }

    public T1 getTid() {
        return tid;
    }

    public void setTid(T1 tid) {
        this.tid = tid;
    }

    public Double getProb() {
        return prob;
    }

    public void setProb(Double prob) {
        this.prob = prob;
    }
}