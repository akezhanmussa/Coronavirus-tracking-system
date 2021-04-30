package com.senior.server.domain;

import java.util.ArrayList;
import java.util.List;

public class LineSpace {
    public double current;
    private final double end;
    private final double step;
    private List<Double> elems;
    private List<Double> midList;

    public LineSpace(double start, double end, int totalCount) {
        this.current=start;
        this.end=end;
        this.step=(end - start) / totalCount;
        elems = new ArrayList<>();
        midList = new ArrayList<>();
        elems.add(current);
    }

    public boolean hasNext() {
        return current < end; //MAY stop floating point error
    }

    public Double getNext() {
        current+=step;
        return current;
    }

    public void process() {
        while(this.hasNext()) {
            elems.add(this.getNext());
        }

        for (int i = 0; i < elems.size() - 1; i++) {
            Double mid = (elems.get(i) + elems.get(i + 1)) / 2;
            midList.add(mid);
        }
    }

    public List<Double> getMidList() {
        return midList;
    }

    public void setMidList(List<Double> midList) {
        this.midList = midList;
    }

    public List<Double> getElems() {
        return elems;
    }

    public void setElems(List<Double> elems) {
        this.elems = elems;
    }

    public Integer getIndexFor(Double target) {
        for(int i = 0; i < elems.size() - 1; i++) {
            double leftElem = elems.get(i);
            double rightElem = elems.get(i + 1);
            if (leftElem < target && target <= rightElem) {
                return i;
            }
        }
        return -1;
    }
}
