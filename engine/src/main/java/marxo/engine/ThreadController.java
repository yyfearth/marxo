package marxo.engine;

public class ThreadController {
	public static void main(String[] args) {
		MessageLoop messageLoop = new MessageLoop();
		messageLoop.run();
	}

	public void start() {

	}
}

class MessageLoop implements Runnable {
	volatile boolean isStopping = false;

	boolean isStopping() {
		return isStopping;
	}

	void setStopping(boolean stopping) {
		isStopping = stopping;
	}

	@Override
	public void run() {
		while (!isStopping) {
			System.out.println("I am working");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}