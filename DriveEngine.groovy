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
	public HashMap<DHParameterKinematics,MobileBase> getAllDHChains(MobileBase source) {
		HashMap<DHParameterKinematics,MobileBase> copy = new HashMap<>();
		copy.putAll(getSteerable(source))
		copy.putAll(getDrivable(source))
		return copy;
	}
	public HashMap<DHParameterKinematics,MobileBase> getSteerable(MobileBase source) {
		HashMap<DHParameterKinematics,MobileBase> copy = new HashMap<>();
		for(def k:source.getSteerable())
			copy.put(k,source);

		for(DHParameterKinematics k:source.getAllDHChains()) {
			for(int i=0;i<k.getNumberOfLinks();i++) {
				if(k.getFollowerMobileBase(i)!=null)
					copy.putAll(getSteerable(k.getFollowerMobileBase(i)))
			}
		}
		return copy;
	}
	public HashMap<DHParameterKinematics,MobileBase> getDrivable(MobileBase source) {
		HashMap<DHParameterKinematics,MobileBase> copy = new HashMap<>();
		for(def k:source.getDrivable())
			copy.put(k,source);
		for(DHParameterKinematics k:source.getAllDHChains()) {
			for(int i=0;i<k.getNumberOfLinks();i++) {
				if(k.getFollowerMobileBase(i)!=null)
					copy.putAll(getDrivable(k.getFollowerMobileBase(i)))
			}
		}
		return copy;
	}
	@Override
	public void DriveArc(MobileBase ogSource, TransformNR newPose, double seconds) {
		newPose = newPose.inverse()
		HashMap<DHParameterKinematics,MobileBase>  wheels = getAllDHChains( ogSource)
		HashMap<DHParameterKinematics,MobileBase> steerable = getSteerable(ogSource);
		//println "\n\n"
		for(DHParameterKinematics LimbWithWheel:wheels.keySet()){
			MobileBase wheelSource=wheels.get(LimbWithWheel);
			// Get the current pose of the robots base
			TransformNR global= ogSource.getFiducialToGlobalTransform();
			// set a new one if null
			if(global==null){
				global=new TransformNR()
				ogSource.setGlobalToFiducialTransform(global)
			}
			global=global.times(newPose);// new global pose
			int wheelIndex =LimbWithWheel.getNumberOfLinks()>=3?1:0;
			ArrayList<TransformNR> tipChain = LimbWithWheel.getChain().getChain(LimbWithWheel.getCurrentJointSpaceTarget())
			// get the pose of this wheel
			TransformNR wheelStarting;
			if(wheelIndex==0)
				wheelStarting = wheelSource.forwardOffset(LimbWithWheel.getRobotToFiducialTransform());
			else
				wheelStarting= wheelSource.forwardOffset(tipChain.get(LimbWithWheel.getNumberOfLinks()-3))
			Matrix btt =  wheelStarting.getMatrixTransform();
			Matrix ftb = global.getMatrixTransform();// our new target
			Matrix mForward = ftb.times(btt)
			TransformNR inc =new TransformNR( mForward);// this wheels new increment
			//println thisWheel.getScriptingName()+" pose "+wheelStarting.getX()+" "+wheelStarting.getY()+" "+wheelStarting.getZ()
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
			ArrayList<DHLink> dhLinks = LimbWithWheel.getChain().getLinks()
			
			
			if(steerable.get(LimbWithWheel)!=null){
				//println "\n\n"+i+" XY plane distance "+xyplaneDistance
				//println LimbWithWheel.getScriptingName()+" Steer angle "+steer
				try{
					double[] joints=LimbWithWheel.getCurrentJointSpaceVector();
					double delta = joints[wheelIndex]-steer;
					joints[wheelIndex]=steer;
					double bestTime = LimbWithWheel.getBestTime(joints)
//					if(delta>1)
//					 println "Speed for steering link "+(delta/bestTime)+" degrees per second"
					LimbWithWheel.setDesiredJointAxisValue(wheelIndex,steer,bestTime)
				}catch(Exception e){
					e.printStackTrace(System.out)
				}
				wheelIndex+=1;
			}else {
			}
			DHLink dh = dhLinks.get(wheelIndex)
			// Hardware to engineering units configuration
			LinkConfiguration conf = LimbWithWheel.getLinkConfiguration(wheelIndex);
			// Engineering units to kinematics link (limits and hardware type abstraction)
			AbstractLink abstractLink = LimbWithWheel.getAbstractLink(wheelIndex);// Transform used by the UI to render the location of the object
			// Transform used by the UI to render the location of the object
			Affine manipulator = dh.getListener();
			double radiusOfWheel = dh.getR()
			double theta=0
			if(Math.abs(xyplaneDistance)>0.01){
				theta=Math.toDegrees(xyplaneDistance/radiusOfWheel)*(reverseWheel?-1:1)
			}
			double[] currVal=LimbWithWheel.getCurrentJointSpaceVector();
			try{
				currVal[wheelIndex]+=theta
				double best = LimbWithWheel.getBestTime(currVal);
				if(best>seconds)
					seconds=best;
				LimbWithWheel.setDesiredJointAxisValue(wheelIndex,currVal[wheelIndex],seconds);
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
