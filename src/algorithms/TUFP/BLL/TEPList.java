package algorithms.TUFP.BLL;

/**
 * TEP-List: - TID của các giao dịch                                    -   Type: int
 *           - Xác suất tồn tại tương ứng (Existential Probability)     -   Type: double
 * */
public class TEPList {
    private int tid;
    private double prob;

    public TEPList(int tid, double prob) {
        this.tid = tid;
        this.prob = prob;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public double getProb() {
        return prob;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }
}
