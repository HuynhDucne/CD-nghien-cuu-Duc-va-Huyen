package algorithms.TUFP;

/**
 * TEP-List: - TID của các giao dịch                                    -   Type: T1
 *           - Xác suất tồn tại tương ứng (Existential Probability)     -   Type: T2
 * */
public class TEPList<T1, T2> {
    private T1 tid;
    private T2 prob;

    public TEPList(T1 tid, T2 prob) {
        this.tid = tid;
        this.prob = prob;
    }

    public T1 getTid() {
        return tid;
    }

    public void setTid(T1 tid) {
        this.tid = tid;
    }

    public T2 getProb() {
        return prob;
    }

    public void setProb(T2 prob) {
        this.prob = prob;
    }
}
