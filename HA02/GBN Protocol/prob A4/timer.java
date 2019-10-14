import java.util.Timer;
import java.util.TimerTask;


public class timer {
	static packet pkt;
	static Object temp;
	static int n;
	static boolean hasEnded = false;
	static int secondsPassed = 0;
	Timer timerObj = new Timer();
	TimerTask task = new TimerTask()
	{
		public void run() {
			synchronized(temp)
			{
				secondsPassed++;		//number of seconds passed are incremented
				if(secondsPassed==n)
				{
					timerObj.cancel();
					hasEnded = true;
				}
			}
		}
	};
	
	public timer(packet pkt, int n)		//constructor for a specific packet
	{
		timer.pkt = pkt;
		temp = new Object();
	}
	
	public timer()			//null time object for initialization
	{
		timer.pkt = null;
		temp = null;
	}
	
	public void start()
	{
		timerObj.schedule(task, n*1000);		//to keep running until n seconds pass
	}

	public boolean isTimedOut() {
		synchronized(temp)
		{
			if(hasEnded)
			{
				pkt.setMessage(pkt.getSeqNum() + " Message Number "+pkt.getPktNum()+" "+pkt.getChecksum());			//corrupted packet is reset to be sent again
				return true;
			}
		}
		return false;
	}
	
	public void stop() {
		timerObj.cancel(); 			//cancels timer object
		hasEnded = true;
	}
	
	public packet getPacket() {
		return pkt;
	}
	
/*	public boolean isRunning()
	{
		if(hasEnded)			//to check if timer is running
			return false;
		else
			return true;
	}
	*/
}
