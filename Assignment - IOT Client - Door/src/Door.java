import com.phidget22.*;

import java.io.IOException;

class Door
{
	private int id;
	private RCServo servo;

	Door(int id) throws PhidgetException
	{
		this.id = id;
		this.servo = new RCServo();
		this.servo.open(5000);
		this.servo.setTargetPosition(0);
		this.servo.setEngaged(true);
	}

	void open(int doorId) throws IOException
	{
		if(doorId != this.id) {
			return;
		}
		System.out.println("Opening door...");
		try {
			this.servo.setTargetPosition(180);
			Client.server.open(this.id, true);
		}
		catch(PhidgetException e) {
			Client.server.open(this.id, false);
		}
	}

	void close(int doorId) throws IOException
	{
		if(doorId != this.id) {
			return;
		}
		System.out.println("Closing door...");
		try {
			this.servo.setTargetPosition(0);
			Client.server.close(this.id, true);
		}
		catch(PhidgetException e) {
			Client.server.close(this.id, false);
		}
	}

	void disconnect() throws PhidgetException
	{
		servo.close();
	}
}
