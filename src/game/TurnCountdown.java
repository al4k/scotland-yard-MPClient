package game;

import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.callback.TextOutputCallback;

import net.RemoteControllable;

public class TurnCountdown extends TimerTask {

	int timeInSeconds = 0;
	Timer timer = null;
	RemoteControllable remoteControllable = null;
	int currentPlayer = 0;
	private boolean turnOver = false;
	
	
	public TurnCountdown(RemoteControllable rc, int currentPlayer) {
		remoteControllable = rc;
		this.currentPlayer = currentPlayer;
		this.turnOver = false;
	}
	
	@Override
	public void run() {
		if(turnOver)
		{
			stop();
			
			TextOutput.printDebug("You have run out of time. Turn Over!!!\n");
			return;
		}
		
		
		double timeLeft = 0.0;
		synchronized (remoteControllable) {
			if(remoteControllable.getServerGameOver())
			{
				stop();
				return;
			}
			timeLeft = remoteControllable.getServerTurnOver(currentPlayer);
		}
		
		
		// print out the message
		if(timeLeft >= 0.0)
		{
			TextOutput.printDebug("Estimated Time Left: " + timeLeft + "s\n");
			TextOutput.setCountDownTime(timeLeft);
		}
		
		if(timeLeft <= 0.0)
			turnOver = true;
	}
	
	public boolean turnOver()
	{
		return this.turnOver;
	}
	
	public void stop()
	{
		TextOutput.setCountDownTime(-1);
		timer.cancel();
		timer.purge();
	}
	
	public void start()
	{
		timer = new Timer();
		timer.scheduleAtFixedRate(this, 0, 500);
	}

}
