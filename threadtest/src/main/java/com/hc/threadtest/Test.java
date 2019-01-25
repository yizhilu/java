package com.hc.threadtest;

import java.util.concurrent.PriorityBlockingQueue;

public class Test {
  public static void main(String[] args) throws InterruptedException {
    // ArrayBlockingQueue<Object> arrayQueue = new ArrayBlockingQueue<Object>(2);
    // // 插入第一个对象
    // arrayQueue.put(new Object());
    // System.out.println(1);
    // // 插入第二个对象
    // arrayQueue.put(new Object());
    // System.out.println(2);
    // // 插入第三个对象时，这个操作线程就会被阻塞。
    // arrayQueue.take();
    // arrayQueue.put(new Object());
    // System.out.println(3);
    PriorityBlockingQueue<TempObject> priorityQueue = new PriorityBlockingQueue<TempObject>();
    priorityQueue.put(new TempObject(-5));
    priorityQueue.put(new TempObject(5));
    priorityQueue.put(new TempObject(-1));
    priorityQueue.put(new TempObject(1));

    // 第一个元素是5
    // 实际上在还没有执行priorityQueue.poll()语句的时候，队列中的第二个元素不一定是1
    TempObject targetTempObject = priorityQueue.poll();
    System.out.println("tempObject.index = " + targetTempObject.getIndex());
    // 第二个元素是1
    targetTempObject = priorityQueue.poll();
    System.out.println("tempObject.index = " + targetTempObject.getIndex());
    // 第三个元素是-1
    targetTempObject = priorityQueue.poll();
    System.out.println("tempObject.index = " + targetTempObject.getIndex());
    // 第四个元素是-5
    targetTempObject = priorityQueue.poll();
    System.out.println("tempObject.index = " + targetTempObject.getIndex());


  }

  // 这个元素类，必须实现Comparable接口
  private static class TempObject implements Comparable<TempObject> {
    private int index;

    public TempObject(int index) {
      this.index = index;
    }

    /**
     * @return the index
     */
    public int getIndex() {
      return index;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(TempObject o) {
      return o.getIndex() - this.index;
    }
  }
}
