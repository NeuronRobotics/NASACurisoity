import com.neuronrobotics.bowlerstudio.creature.ICadGenerator;
import com.neuronrobotics.bowlerstudio.physics.TransformFactory
import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.bowlerstudio.creature.CreatureLab;
import org.apache.commons.io.IOUtils;
import com.neuronrobotics.bowlerstudio.vitamins.*;
import com.neuronrobotics.sdk.addons.kinematics.AbstractLink
import com.neuronrobotics.sdk.addons.kinematics.DHLink
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR

import java.nio.file.Paths;

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.FileUtil;
import eu.mihosoft.vrl.v3d.Transform
import javafx.scene.transform.Affine
println "Loading STL file"
// Load an STL file from a git repo
// Loading a local file also works here

return new ICadGenerator(){
	@Override 
	public ArrayList<CSG> generateCad(DHParameterKinematics d, int linkIndex) {
		ArrayList<DHLink> dhLinks = d.getChain().getLinks()
		ArrayList<CSG> allCad=new ArrayList<CSG>()
		
		DHLink dh = dhLinks.get(linkIndex)
		// Hardware to engineering units configuration
		LinkConfiguration conf = d.getLinkConfiguration(linkIndex);
		// Engineering units to kinematics link (limits and hardware type abstraction)
		AbstractLink abstractLink = d.getAbstractLink(linkIndex);// Transform used by the UI to render the location of the object
		// Transform used by the UI to render the location of the object
		Affine manipulator = dh.getListener();
		boolean right =d.getRobotToFiducialTransform().getY()>0;
		
		if (linkIndex==0 && d.getNumberOfLinks()==3){
			CSG steer;
			CSG rocker
			Transform stepTf = TransformFactory.nrToCSG(d.getDHStep(0).inverse())
								.apply(
									new Transform()
										.movex(-28)
										.movey(8.5*(right?-1:1))
										.movez(-29)
										.rotx(90));
			if(right) {
				Transform tf = stepTf
				
					steer = Vitamins.get(ScriptingEngine.fileFromGit(
				"https://github.com/NeuronRobotics/NASACurisoity.git",
				"STL/lower-suspension-p2-right.STL")).transformed(tf)
				rocker= Vitamins.get(ScriptingEngine.fileFromGit(
						"https://github.com/NeuronRobotics/NASACurisoity.git",
						"STL/lower-suspension-p1-right.STL")).transformed(tf)
	
			}else {
				Transform tf =stepTf
				
					steer= Vitamins.get(ScriptingEngine.fileFromGit(
					"https://github.com/NeuronRobotics/NASACurisoity.git",
					"STL/lower-suspension-p2-left.STL")).transformed(tf)
					rocker= Vitamins.get(ScriptingEngine.fileFromGit(
					"https://github.com/NeuronRobotics/NASACurisoity.git",
					"STL/lower-suspension-p1-left.STL")).transformed(tf)

			}
			def rockerParts=[steer,rocker]
			for(CSG s:rockerParts) {
				s.setManipulator(manipulator)
				allCad.add(s)
			}
	
		}
		int offset = d.getNumberOfLinks()==2?0:1;
		if (linkIndex==(offset)){
			
			File steer_file = ScriptingEngine.fileFromGit(
			"https://github.com/NeuronRobotics/NASACurisoity.git",
			"STL/steering-bracket.STL");
			CSG steer = Vitamins.get(steer_file)
							.roty(right?180:0)

			steer.setManipulator(manipulator)
			allCad.add(steer)
	
		}
		if (linkIndex==(offset+1)){
			File wheel_file = ScriptingEngine.fileFromGit(
			"https://github.com/NeuronRobotics/NASACurisoity.git",
			"STL/wheel.STL");
			File tire_file = ScriptingEngine.fileFromGit(
			"https://github.com/NeuronRobotics/NASACurisoity.git",
			"STL/tire.STL");
			/*
			CSG wheel = Vitamins.get(wheel_file)
			wheel=wheel			
					.movex(-wheel.getMaxX()/2)
					.movey(-wheel.getMaxY()/2)
					.movez(-wheel.getMaxZ()/2)
					.rotx(90)
			wheel.setManipulator(manipulator)
			
			allCad.add(wheel)
			*/
			CSG tire = Vitamins.get(tire_file)
					.movex(-dh.getR())
					.movez(-dh.getD())
			tire.setManipulator(manipulator)

			allCad.add(tire)
		}
		for(int i=0;i<allCad.size();i++){
			allCad.get(i).setColor(javafx.scene.paint.Color.GRAY)
		}
		return allCad;
	}
	@Override 
	public ArrayList<CSG> generateBody(MobileBase b ) {return new ArrayList<>();}
};
