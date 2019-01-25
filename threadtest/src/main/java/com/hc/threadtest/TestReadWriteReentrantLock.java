package com.hc.threadtest;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

public class TestReadWriteReentrantLock {

  static {
    BasicConfigurator.configure();
  }

  /**
   * 日志
   */
  private static final Log LOG = LogFactory.getLog(TestReadWriteReentrantLock.class);

  public static void main(String[] args) throws RuntimeException {
    new TestReadWriteReentrantLock().test();
  }

  public void test() {
    final ReentrantReadWriteLock objectLock = new ReentrantReadWriteLock();

    Thread thread1 = new Thread() {
      public void run() {
        WriteLock writeLock = objectLock.writeLock();
        writeLock.lock();
        TestReadWriteReentrantLock.LOG.info("做了一些写操作的事情。。。。");
        writeLock.unlock();
      }
    };


    Thread thread2 = new Thread() {
      public void run() {
        WriteLock writeLock = objectLock.writeLock();
        writeLock.lock();
        TestReadWriteReentrantLock.LOG.info("做了另一些写操作的事情。。。。");
        writeLock.unlock();
      }
    };

    Thread thread3 = new Thread() {
      public void run() {
        ReadLock readLock = objectLock.readLock();
        readLock.lock();
        TestReadWriteReentrantLock.LOG.info("做了一些读操作的事情。。。。");
        readLock.unlock();
      }
    };

    // thread1、thread2、thread3在运行过程中，将按照我们之前描述的规律，相互作用
    thread1.start();
    thread2.start();
    // 您可以使用thread1.interrupt()指令对ReentrantLock的影像。
    // 您可以发现，thread1在加锁后并不会抛出interruptException异常
    // 至少在我们这种使用方式下，不会抛出异常
    // thread1.interrupt();
    thread3.start();
  }
}
