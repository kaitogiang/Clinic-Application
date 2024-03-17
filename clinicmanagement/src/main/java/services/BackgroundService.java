package services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class BackgroundService<T> extends Service<T> {
	
	//Hàm chứa hành động cần truyền vô để chạy ngầm
	private final Runnable action;
	//Số giây bị delay
	private final long delayInMilis;
	
	public BackgroundService(Runnable action, long delayInSecond) {
		this.action = action;
		this.delayInMilis = delayInSecond * 1000;
	}
	
	@Override
	protected Task<T> createTask() {
		return new Task<T>() {
			@Override
			protected T call() throws Exception {
				while(!isCancelled()) {
					try {
						//Thực hiện hành động được truyền vào
						action.run();
						System.out.println("Gọi service");
						Thread.sleep(delayInMilis);
					} catch (InterruptedException e) {
						if (isCancelled()) {
							break;
						}
					}
				}
				return null;
			}
		};
	}
}
