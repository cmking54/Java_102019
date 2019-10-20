public class ThreadDemo {
  public static void main(String[] args) throws Exception {

    Thread t1 = new Thread(new Runnable() {
      public void run() {
        for (int i = 0; i < 5; i++) {
          System.out.println("Hi");
          try { Thread.sleep(500); } catch (Exception e) {}
        }
      }
    }, "Hi Thread");

    Thread t2 = new Thread(() -> {
      for (int i = 0; i < 5; i++) {
        System.out.println("Hello");
        try { Thread.sleep(500); } catch (Exception e) {}
      }
    }, "Hello Thread");

    System.out.println(t1.getName());
    System.out.println(t2.getName());
    t1.setPriority(Thread.MIN_PRIORITY); // 1
    t2.setPriority(Thread.MAX_PRIORITY); // 10
    System.out.println(t1.getPriority());
    System.out.println(t2.getPriority());
    t1.start();
    t2.start();
    System.out.println(t1.isAlive());
    t1.join();
    t2.join();
    System.out.println(t1.isAlive());
    System.out.println("Bye");


    // Synch
    Counter c = new Counter();
    t1 = new Thread(() -> {
      for (int i = 0; i < 1000; i++) {
        c.increment();
      }
    });
    t2 = new Thread(() -> {
      for (int i = 0; i < 1000; i++) {
        c.increment();
      }
    });
    t1.start();
    t2.start();
    t1.join();
    t2.join();
    System.out.println("Count " + c.count);
    //interthread
    IntWrapper intWrapper = new IntWrapper();
    new Producer(intWrapper);
    new Consumer(intWrapper);

  }
}
class Counter {
  int count;
  public synchronized void increment() {
    count++;
  }
}
class IntWrapper {
  int value;
  boolean valueSet = false;
  synchronized void getValue() {
    while (!valueSet) { try { wait(); } catch (Exception e) {} }
    System.out.println("Gotten Value " + value);
    valueSet = false;
    notify();
  }
  synchronized void setValue(int value) {
    while (valueSet) { try { wait(); } catch (Exception e) {} }
    valueSet = true;
    this.value = value;
    System.out.println("Sat Value " + value);
    notify();
  }
}
class Producer implements Runnable {
  IntWrapper intWrapper;
  Producer(IntWrapper intWrapper) {
    this.intWrapper = intWrapper;
    new Thread(this, "Producer").start();
  }
  public void run() {
    int i = 0;
    while (true) {
      intWrapper.setValue(i++);
      try { Thread.sleep(1000);} catch (Exception e) {}
    }
  }
}
class Consumer implements Runnable {
  IntWrapper intWrapper;
  Producer(IntWrapper intWrapper) {
    this.intWrapper = intWrapper;
    new Thread(this, "Consumer").start();

  }
  public void run() {
    int i = 0;
    while (true) {
      intWrapper.getValue();
      try { Thread.sleep(1000);} catch (Exception e) {}
    }
  }
}
