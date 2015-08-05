package ru.linachan.asgard;

public class AsgardSelectable implements Comparable<AsgardSelectable> {
    private int __id = 0;
    public String[] __ignored = new String[] { "__id", "__ignored" };

    @Override
    public int compareTo(AsgardSelectable o) {
        if (this.queue_id() > o.queue_id()) {
            return 1;
        } else if (this.queue_id() < o.queue_id()) {
            return -1;
        } else {
            return 0;
        }
    }
    public int queue_id() { return __id; }
    public void queue_id(int id) { this.__id = id; }
}
