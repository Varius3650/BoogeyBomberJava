package PE_WK_JSK;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import robocode.*;
import robocode.util.Utils;


public class Wanker extends AdvancedRobot 
{
    static int currentEnemyVelocity;
    static int aimingEnemyVelocity;
    double velocityToAimAt;
    boolean fired;
    Rectangle grid = new Rectangle(0, 0, 800, 600);
    double oldTime;
    int count;
    int averageCount;
    static double enemyVelocities[][] = new double[400][4];
    static double turn = 2;
    int turnDir = 1;
    int moveDir = 1;
    double oldEnemyHeading;
    double oldEnergy = 100;


    @Override
    public void run()
    {
        
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        do
        {
            turnRadarRightRadians(Double.POSITIVE_INFINITY);
        }
        while(true);
    }

	public void setEnergyColorBlinking(){
		if(getEnergy()%2== 0){
		 	setRadarColor(new Color (227, 200, 0));	
		 	setBulletColor(new Color (227, 200,0));
		 	}
		else{
			setRadarColor(new Color(0, 0, 0));
			setBulletColor(new Color (0, 0,0));
		}
	}
	public void setEnergyChangeColor()
	{
		 getEnergy();
		// setBulletColor(new Color (0, 0,0));
		 setGunColor(new Color(100, 118, 135));
		 setScanColor(new Color(109, 135, 100));
		    if(getEnergy()>=62){
		    	
		        
		        setBodyColor(new Color (96, 169,23));//
		        }
		    
		    if(getEnergy()<62 && getEnergy()>=41){
		    	
		        setBodyColor(new Color(255, 102, 0)); 
		        }
		    if(getEnergy()<=32){
		        setBodyColor(new Color(229, 20, 0)); 
		        }
		    if(getEnergy()<8){
		        setBodyColor(new Color(216, 0, 115)); 
		    }
	}
    @Override
    public void onScannedRobot(ScannedRobotEvent e)
    {
        double absBearing = e.getBearingRadians() + getHeadingRadians();
        Graphics2D g = getGraphics();
        turn += 0.2 * Math.random();
        if(turn > 8)
        {
            turn = 2;
        }

        if(oldEnergy - e.getEnergy() <= 3 && oldEnergy - e.getEnergy() >= 0.1)
        {
            if(Math.random()>.5)
            {
                turnDir*=-1;
            }
            if(Math.random()>.8)
            {
                moveDir*=-1;
            }
        }

        setMaxTurnRate(turn);
        setMaxVelocity(12 - turn);
        setAhead(90*moveDir);
        setTurnLeft(90*turnDir);
        oldEnergy = e.getEnergy();

        if(e.getVelocity() < -2)
        {
            currentEnemyVelocity = 0;
        }
        else if(e.getVelocity() > 2)
        {
            currentEnemyVelocity = 1;
        }
        else if(e.getVelocity() <= 2&&e.getVelocity() >= -2)
        {
            if(currentEnemyVelocity == 0)
            {
                currentEnemyVelocity = 2;
            }
            else if(currentEnemyVelocity == 1)
            {
                    currentEnemyVelocity = 3;
            }
        }
        if(getTime() - oldTime > e.getDistance() / 12.8 && fired == true)
        {
            aimingEnemyVelocity=currentEnemyVelocity;
        }
        else
        {
            fired = false;
        }

        enemyVelocities[count][aimingEnemyVelocity] = e.getVelocity();
        count++;

        if(count==400)
        {
            count=0;
        }

        averageCount = 0;
        velocityToAimAt = 0;

        while(averageCount < 400)
        {
            velocityToAimAt += enemyVelocities[averageCount][currentEnemyVelocity];
            averageCount++;
        }

        velocityToAimAt /= 400;

        double bulletPower = Math.min( 2.4, Math.min( e.getEnergy()/3.5, getEnergy()/9));
        double myX = getX();
        double myY = getY();
        double enemyX = getX() + e.getDistance() * Math.sin(absBearing);
        double enemyY = getY() + e.getDistance() * Math.cos(absBearing);
        double enemyHeading = e.getHeadingRadians();
        double enemyHeadingChange = enemyHeading - oldEnemyHeading;
        oldEnemyHeading = enemyHeading;
        double deltaTime = 0;
        double battleFieldHeight = getBattleFieldHeight();
        double battleFieldWidth = getBattleFieldWidth();
        double predictedX = enemyX, predictedY = enemyY;

        while((++deltaTime) * (20.0 - 3.0 * bulletPower) < Point2D.Double.distance( myX, myY, predictedX, predictedY))
        {      
            predictedX += Math.sin(enemyHeading) * velocityToAimAt;
            predictedY += Math.cos(enemyHeading) * velocityToAimAt;
            enemyHeading += enemyHeadingChange;
            g.setColor(Color.red);
            g.fillOval((int)predictedX - 2,(int)predictedY - 2, 4, 4);

            if( predictedX < 18.0 || predictedY < 18.0 || predictedX > battleFieldWidth - 18.0 || predictedY > battleFieldHeight - 18.0) 
            {   
                predictedX = Math.min(Math.max(18.0, predictedX), battleFieldWidth - 18.0); 
                predictedY = Math.min(Math.max(18.0, predictedY), battleFieldHeight - 18.0);
                break;
            }
        }

        double theta = Utils.normalAbsoluteAngle(Math.atan2(predictedX - getX(), predictedY - getY()));
        setTurnRadarRightRadians(Utils.normalRelativeAngle(absBearing - getRadarHeadingRadians())*2);
        setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));

        if(getGunHeat() == 0)
        {
            fire(bulletPower);
            fired = true;
        }


    }
}
/*
import robocode.*;
import robocode.util.Utils;
import PE_WK_JSK.Coordinates;

public class BoogeyBomber extends AdvancedRobot
{
	private static final int dist = 50;

	public static final int COORDINATES = 2000;
	double radarFactor = 2;
	boolean spottedEnemy = false;
	
	public void run()
	{
		while (true) {
			if (!spottedEnemy) {
				setTurnRadarLeft(180);
			}
			scan();
			move();
			execute();
		}
	}
	
	void pointGun(ScannedRobotEvent e)
	{
		if(e.getDistance() <= COORDINATES) {
			Coordinates coordinates = getCurrentCoordinates(e);
			getTurnDirection(coordinates, e);
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
	
	public void onScannedRobot(ScannedRobotEvent e)
	{
		spottedEnemy = true;
//		pointGun(e);
		setFire(1);
		double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
		setTurnRadarRightRadians(radarFactor * Utils.normalRelativeAngle(radarTurn));
		// save Enemy status to filesystem
		saveOnScannedRobot(e);
	}
	
	public void onHitRobot(HitRobotEvent e)
	{
		double turnGunAmt = Utils.normalRelativeAngleDegrees(e.getBearing() + getHeading() - getGunHeading());
		setTurnGunRight(turnGunAmt);
		setFire(3);
	}

}
