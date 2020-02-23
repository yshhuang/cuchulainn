package top.yshhuang.cuchulainn;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yshhuang@foxmail.com
 * @date 2020-02-22 09:33
 */
@Data
public class MultiMatrix {
    private Number[] data;
    private Integer[] shape;

    public MultiMatrix(Integer shapeLength) {
        this.shape = new Integer[shapeLength];
    }

    public MultiMatrix(Number[] data,Integer[] shape) {
        //TODO data.length=shape[0]*shape[1]……
        this.data = data;
        this.shape = shape;
    }

    public static MultiMatrix zeros(Integer[] shape) {
        int total = 1;
        for (int i = 0; i < shape.length; i++) {
            total *= shape[i];
        }
        Number[] data = new Number[total];
        for (int i = 0; i < data.length; i++) {
            data[i] = 0;
        }
        return new MultiMatrix(data,shape);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (shape.length == 1) {
            sb.append("[");
            for (int i = 0; i < data.length - 1; i++) {
                sb.append(data[i]).append(",");
            }
            sb.append(data[data.length - 1]).append("]");
        } else {
            sb.append("[");
            int newTotal = data.length / shape[0];
            Integer[] newShape = sub(shape,1,shape.length);
            for (int i = 0; i < shape[0] - 1; i++) {
                Number[] newData = sub(data,i * newTotal,(i + 1) * newTotal);
                MultiMatrix mm = new MultiMatrix(newData,newShape);
                sb.append(mm.toString()).append(",\n");
            }
            Number[] newData = sub(data,(shape[0] - 1) * newTotal,shape[0] * newTotal);
            MultiMatrix mm = new MultiMatrix(newData,newShape);
            sb.append(mm.toString()).append("]");
        }
        return sb.toString();
    }

    private Integer[] sub(Integer[] data,int start,int end) {

        Integer[] sub = new Integer[end - start];
        for (int i = 0; i < sub.length; i++) {
            sub[i] = data[i + start];
        }
        return sub;
    }

    private Number[] sub(Number[] data,int start,int end) {
        Number[] sub = new Number[end - start];
        for (int i = 0; i < sub.length; i++) {
            sub[i] = data[i + start];
        }
        return sub;
    }

    public MultiMatrix sub(Integer[][] range) {
        MultiMatrix multiMatrix = new MultiMatrix(shape.length);
        if (range.length != shape.length) {
            //TODO custom exception
            throw new RuntimeException("range.length must equals shape.length");
        }
        for (int i = 0; i < shape.length; i++) {
            if (range[i].length != 2) {
                throw new RuntimeException("range[i].length must equals 2");
            } else if (range[i][1] <= range[i][0]) {
                throw new RuntimeException("range[i][1] must grater than range[i][0]");
            } else {
                multiMatrix.shape[i] = range[i][1] - range[i][0];
            }
        }
        int total = 1;
        for (int i = 0; i < multiMatrix.shape.length; i++) {
            total *= multiMatrix.shape[i];
        }
        multiMatrix.data = new Number[total];
        if (shape.length == 1) {
            for (int i = 0; i < multiMatrix.shape[0]; i++) {
                multiMatrix.data[i] = data[i + range[0][0]];
            }
        } else {
            //TODO 递归计算
            Integer[] newShape = new Integer[shape.length - 1];
            for (int i = 0; i < newShape.length; i++) {
                newShape[i] = shape[i + 1];
            }
            Integer[][] newRange = new Integer[range.length - 1][range[0].length];
            for (int i = 0; i < newRange.length; i++) {
                newRange[i] = range[i + 1];
            }
            int rowNumber = data.length / shape[0];
            for (int i = range[0][0]; i < range[0][1]; i++) {
                int start = i * rowNumber;
                int end = (i + 1) * rowNumber;
                Number[] newData = new Number[end - start];
                for (int j = 0; j < newData.length; j++) {
                    newData[j] = data[j + start];
                }
                MultiMatrix mm = new MultiMatrix(newData,newShape).sub(newRange);
                for (int j = 0; j < mm.data.length; j++) {
                    multiMatrix.data[(i - range[0][0]) * (range[1][1]-range[1][0]) + j] = mm.data[j];
                }
            }
        }
        return multiMatrix;
    }

    public static void main(String[] args) {
        MultiMatrix mm = MultiMatrix.zeros(new Integer[]{4,4});
        System.out.println(mm.sub(new Integer[][]{{0,4},{0,3}}).toString());
    }
}
