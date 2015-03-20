package PE_WK_JSK;

import robocode.*;
import robocode.util.Utils;
import PE_WK_JSK.Coordinates;

public class BoogeyBomber extends AdvancedRobot
{
	private static final int dist = 50;

	public static final int COORDINATES = 200;
	double radarFactor = 2;
	
	public void Run()
	{
		while (true) {
			scan();
			move();
			execute();
		}
	}
	
	void pointGun(ScannedRobotEvent e)
	{
		if(e.getDistance() <= COORDINATES) {
			Coordinates coordinates = getCurrentCoordinates(e);
			this.getTurnDirection(coordinates, e);
		}
	}
	
	void getTurnDirection(Coordinates enemyCoordinates, ScannedRobotEvent e)
	{
		double enemyAngle = java.lang.Math.asin(e.getDistance() / (enemyCoordinates.x - getX()));
		double relativeAngle = java.lang.Math.abs(getGunHeading() - enemyAngle);
		if (relativeAngle > 180) {
			if ((e.getHeading() > 180 && getGunHeading() > 180) || (e.getHeading() < 180 && getGunHeading() < 180)) {
				turnGunLeft(relativeAngle);
				return;
			}
			if (e.getHeading() > 180 && getGunHeading() < 180) {
				turnGunLeft(getGunHeading() + 360 - relativeAngle);
				return;
			}
		} else {
			if ((e.getHeading() > 180 && getHeading() > 180) || (e.getHeading() < 180 && getHeading() < 180)) {
				turnGunRight(relativeAngle);
				return;
			}
			if (e.getHeading() < 180 && getHeading() > 180) {
				turnGunRight(360 - getGunHeading() + enemyAngle);
				return;
			}
		}
	}

	Coordinates getCurrentCoordinates(ScannedRobotEvent e)
	{
		double c = e.getDistance();
		double b = c * java.lang.Math.sin(90) / java.lang.Math.sin(getRadarHeading());
		double a = java.lang.Math.sqrt(java.lang.Math.pow(c, 2) - java.lang.Math.pow(b, 2));
		if (getRadarHeading() > 90 && getRadarHeading() < 270) {
			a = java.lang.Math.abs(a);
		}
		if(getRadarHeading() >180) {
			b = java.lang.Math.abs(b);
		}
		double[] coordinates = new double[2];
		return new Coordinates(b + getX(), a + getY());
	}
	
	protected void saveOnScannedRobot(ScannedRobotEvent e) {
		
	}
	
	protected void move()
	{
		boolean wantToTurn = true;
		if (!wouldHitWall()) {
			setAhead(dist);
			wantToTurn = true;
		} else {
			wantToTurn = true;
		}
		if (wantToTurn) {
			setTurnLeft(10);
		}
	}
	
	public boolean wouldHitWall()
	{
		double saveDist = dist + 10;
		// Facing North
		if (getHeading() > 270 || getHeading() < 90)
		{
			if (getY() + saveDist > getBattleFieldHeight())
			{
				// Too close to wall
				return true;
			}
		}
		// Facing East
		if (getHeading() < 180 && getHeading() > 0)
		{
			if (getX() + saveDist > getBattleFieldWidth())
			{
				return true;
			}
		}
		// Facing South
		if (getHeading() < 270 && getHeading() > 90)
		{
			if (getY() - saveDist < 0)
			{
				return true;
			}
		}
		// Facing West
		if (getHeading() > 180 && getHeading() < 360)
		{
			if (getX() - saveDist < 0)
			{
				return true;
			}
		}
		return false;
	}
	
	public void OnScannedRobot(ScannedRobotEvent e)
	{
		pointGun(e);
		setFire(1);
		double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
		setTurnRadarRightRadians(radarFactor * Utils.normalRelativeAngle(radarTurn));
		// save Enemy status to filesystem
		saveOnScannedRobot(e);
	}
	
	public void OnHitRobot(HitRobotEvent e)
	{
		double turnGunAmt = Utils.normalRelativeAngleDegrees(e.getBearing() + getHeading() - getGunHeading());
		setTurnGunRight(turnGunAmt);
		setFire(3);
	}

}
