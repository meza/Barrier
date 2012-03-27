package hu.meza.example;

class ExampleTimeout implements Runnable {

	private ExampleCoolDownStrategy cs;

	public ExampleTimeout(ExampleCoolDownStrategy cs) {
		this.cs = cs;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(5000);
			cs.off();
		} catch (InterruptedException e) {
		}
	}

}
