import com.phidget22.*;

class RfidReader
{
	private RFID rfid;

	RfidReader() throws PhidgetException
	{
		this.rfid = new RFID();
		this.rfid.open(5000);
		this.rfid.setAntennaEnabled(true);
	}

	void addTagListener(RFIDTagListener listener)
	{
		this.rfid.addTagListener(listener);
	}

	void disconnect() throws PhidgetException
	{
		this.rfid.close();
	}
}
