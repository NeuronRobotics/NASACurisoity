//Your code here
import com.neuronrobotics.sdk.addons.kinematics.AbstractLink
import com.neuronrobotics.sdk.addons.kinematics.DHLink
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR

import Jama.Matrix;
import javafx.scene.transform.*;

return new com.neuronrobotics.sdk.addons.kinematics.IDriveEngine (){
	public ArrayList<DHParameterKinematics> getAllDHChains(MobileBase source) {
		ArrayList<DHParameterKinematics> copy = new ArrayList<DHParameterKinematics>();

		for (DHParameterKinematics l : source.getSteerable()) {
			copy.add(l);
		}
		for (DHParameterKinematics l : source.getDrivable()) {
			copy.add(l);
		}
		return copy;
	}
	@Override
	public void DriveArc(MobileBase source, TransformNR newPose, double seconds) {
		newPose = newPose.inverse()
		ArrayList<DHParameterKinematics> wheels = getAllDHChains( source)
		ArrayList<DHParameterKinematics> steerable = source.getSteerable();
		
		for(int i=0;i<wheels.size();i++){
			// Get the current pose of the robots base
			TransformNR global= source.getFiducialToGlobalTransform();
			// set a new one if null
			if(global==null){
				global=new TransformNR()
				source.setGlobalToFiducialTransform(global)
			}
			global=global.times(newPose);// new global pose
			DHParameterKinematics thisWheel =wheels.get(i)
			// get the pose of this wheel
			TransformNR wheelStarting = thisWheel.getRobotToFiducialTransform();
			Matrix btt =  wheelStarting.getMatrixTransform();
			Matrix ftb = global.getMatrixTransform();// our new target
			Matrix mForward = ftb.times(btt)
			TransformNR inc =new TransformNR( mForward);// this wheels new increment
			TransformNR vect =new TransformNR(btt.inverse().times(mForward));// this wheels new increment
			double xyplaneDistance = Math.sqrt(
										Math.pow(vect.getX(),2)+
										Math.pow(vect.getY(),2)
									)
			if(Math.abs(xyplaneDistance)<0.01){
					xyplaneDistance=0;				
			}
			double steer =90-Math.toDegrees( Math.atan2(vect.getX(),vect.getY()))
			boolean reverseWheel = false
			if(steer>90){
				steer=steer-180
				reverseWheel=true;
			}
			if(steer<-90){
				steer=steer+180
				reverseWheel=true;
			}
			ArrayList<DHLink> dhLinks = thisWheel.getChain().getLinks()
			
			int wheelIndex =thisWheel.getNumberOfLinks()==3?1:0;
			
			if(steerable.contains(thisWheel)){
				//println "\n\n"+i+" XY plane distance "+xyplaneDistance
				//println "Steer angle "+steer
				try{
					double[] joints=thisWheel.getCurrentJointSpaceVector();
					double delta = joints[wheelIndex]-steer;
					joints[wheelIndex]=steer;
					double bestTime = thisWheel.getBestTime(joints)
					if(delta>1)
					 println "Speed for steering link "+(delta/bestTime)+" degrees per second"
					thisWheel.setDesiredJointAxisValue(wheelIndex,steer,bestTime)
				}catch(Exception e){
					e.printStackTrace(System.out)
				}
				wheelIndex=wheelIndex+1;
			}
			DHLink dh = dhLinks.get(wheelIndex)
			// Hardware to engineering units configuration
			LinkConfiguration conf = thisWheel.getLinkConfiguration(wheelIndex);
			// Engineering units to kinematics link (limits and hardware type abstraction)
			AbstractLink abstractLink = thisWheel.getAbstractLink(wheelIndex);// Transform used by the UI to render the location of the object
			// Transform used by the UI to render the location of the object
			Affine manipulator = dh.getListener();
			double radiusOfWheel = dh.getR()
			double theta=0
			if(Math.abs(xyplaneDistance)>0.01){
				theta=Math.toDegrees(xyplaneDistance/radiusOfWheel)*(reverseWheel?-1:1)
			}
			double currentWheel= thisWheel.getCurrentJointSpaceVector()[wheelIndex]
			try{
				thisWheel.setDesiredJointAxisValue(wheelIndex,theta+currentWheel,seconds);
			}catch(Exception e){
					e.printStackTrace(System.out)
			}
		}
	}
	@Override
	public void DriveVelocityStraight(MobileBase source, double cmPerSecond) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void DriveVelocityArc(MobileBase source, double degreesPerSecond,
			double cmRadius) {
		// TODO Auto-generated method stub
		
	}
}
