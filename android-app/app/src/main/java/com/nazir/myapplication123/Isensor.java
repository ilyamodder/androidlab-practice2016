package com.nazir.myapplication123;

public interface Isensor {
	
	public boolean isSupport();
	
	public void on(int speed);
	
	public void off();

	public float getMaximumRange();
}
