package benchmark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import model.Model;

public class Benchmark {

  private Model model;

  private Map<Double, Integer> labelMap = new HashMap<Double, Integer>();

  private Map<Double, Integer> correctMap = new HashMap<Double, Integer>();
  private Map<Double, Integer> incorrectMap = new HashMap<Double, Integer>();

  public Benchmark(Model model) {
    this.model = model;
  }

  protected List<double[]> loadTestData() throws IOException {
    return loadData("test.data");
  }

  protected List<double[]> loadTrainData() throws IOException {
    return loadData("train.data");
  }

  /**
   * 获取预测标签和特征
   * 
   * @return 返回的数组列表，数组第一个元素是预测标签，剩余为特征
   * @throws IOException
   */
  protected List<double[]> loadData(String filePath) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filePath));
    List<double[]> attrList = new ArrayList<double[]>();
    String line = null;
    while ((line = br.readLine()) != null) {
      String[] dims = line.split("\t");
      double[] attrs = new double[dims.length];
      // 抽取特征
      for (int i = 0; i < dims.length; i++) {
        attrs[i] = Double.parseDouble(dims[i]);
      }
      attrList.add(attrs);
    }
    br.close();
    return attrList;
  }

  private void train() {
    try {
      model.train(loadTrainData());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 主流程
   * 
   * @throws IOException
   */
  public void run() throws IOException {
    train();
    for (double[] testData : loadTestData()) {
      // 保存label数量
      hinc(labelMap, testData[0]);
      double res = (double) model.predict();
      if (res == testData[0]) {
        hinc(correctMap, res);
      } else {
        hinc(incorrectMap, res);
      }
    }
    for (double key : calcRecall().keySet()) {
      System.out.println("========================class " + key
          + "========================");
      System.out.println("recall: " + calcRecall());
      System.out.println("precision: " + calcPrecision());
      System.out.println("F1: " + calcF1());
    }
  }

  private void hinc(Map<Double, Integer> map, double key) {
    if (map.get(key) == null) {
      map.put(key, 0);
    } else {
      int labelNum = map.get(key);
      map.put(key, labelNum + 1);
    }
  }

  private Map<Double, Double> calcRecall() {
    Map<Double, Double> recallMap = new HashMap<Double, Double>();
    Iterator<Double> iterator = correctMap.keySet().iterator();
    while (iterator.hasNext()) {
      double key = correctMap.keySet().iterator().next();
      double num = correctMap.get(key) + incorrectMap.get(key);
      double sum = labelMap.get(key);
      recallMap.put(key, num / sum);
    }
    return recallMap;
  }

  private Map<Double, Double> calcPrecision() {
    Map<Double, Double> precisionMap = new HashMap<Double, Double>();
    Iterator<Double> iterator = correctMap.keySet().iterator();
    while (iterator.hasNext()) {
      double key = correctMap.keySet().iterator().next();
      double num = correctMap.get(key);
      double sum = correctMap.get(key) + incorrectMap.get(key);
      precisionMap.put(key, num / sum);
    }
    return precisionMap;
  }

  private Map<Double, Double> calcF1() {
    Map<Double, Double> f1Map = new HashMap<Double, Double>();
    for (double key : calcRecall().keySet()) {
      f1Map.put(key, 2 * calcPrecision().get(key) * calcRecall().get(key)
          / (calcPrecision().get(key) + calcRecall().get(key)));
    }
    return f1Map;
  }
}
