package model;

import java.util.List;

public interface Model<T> {

  /**
   * 模型训练方法
   * 
   * @param attrList
   *          数组第一个元素是预测标签，剩余为特征
   */
  public void train(List<double[]> attrList);

  public T predict();

}
