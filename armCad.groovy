
import com.neuronrobotics.bowlerstudio.creature.ICadGenerator;
import com.neuronrobotics.bowlerstudio.creature.CreatureLab;
import org.apache.commons.io.IOUtils;

import com.neuronrobotics.bowlerstudio.vitamins.*;
import com.neuronrobotics.sdk.addons.kinematics.AbstractLink
import com.neuronrobotics.sdk.addons.kinematics.DHLink
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration
import com.neuronrobotics.sdk.addons.kinematics.MobileBase

import java.nio.file.Paths;

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.FileUtil;
import com.neuronrobotics.bowlerstudio.vitamins.*;
import javafx.scene.transform.*;
println "Loading STL file"
// Load an STL file from a git repo
// Loading a local file also works here

ICadGenerator cadGen =new ICadGenerator(){
	@Override 
	public ArrayList<CSG> generateCad(DHParameterKinematics d, int linkIndex) {
		ArrayList<DHLink> dhLinks = d.getChain().getLinks()
		ArrayList<CSG> parts = new ArrayList<>();

		DHLink dh = dhLinks.get(linkIndex)
		// Hardware to engineering units configuration
		LinkConfiguration conf = d.getLinkConfiguration(linkIndex);
		// Engineering units to kinematics link (limits and hardware type abstraction)
		AbstractLink abstractLink = d.getAbstractLink(linkIndex);// Transform used by the UI to render the location of the object
		// Transform used by the UI to render the location of the object
		Affine manipulator = dh.getListener();
		if(linkIndex==0){
			File mount = ScriptingEngine.fileFromGit(
			"https://github.com/NeuronRobotics/NASACurisoity.git",
			"STL/swivel-bracket.STL");
			CSG mountCSG  = Vitamins.get(mount)
			.rotz(-90)
			.roty(-90)
			mountCSG.setManipulator(d.getRootListener());
			//parts.add(mountCSG)
			
			File swivel = ScriptingEngine.fileFromGit(
			"https://github.com/NeuronRobotics/NASACurisoity.git",
			"STL/swivel.STL");
			CSG swivelCSG  = Vitamins.get(swivel)
				.rotz(90)
				.roty(-90)
				.roty(180)
				.movex(-dh.getR())
				.movey(dh.getD())
			swivelCSG.setManipulator(manipulator);
			parts.add(swivelCSG)
		}
		if(linkIndex==1){		
			File swivel = ScriptingEngine.fileFromGit(
			"https://github.com/NeuronRobotics/NASACurisoity.git",
			"STL/upper-arm.STL");
			CSG swivelCSG  = Vitamins.get(swivel)
			
			.movex(-dh.getR())
					.movez(-dh.getD())
					
			swivelCSG.setManipulator(manipulator);
			parts.add(swivelCSG)
		}
		if(linkIndex==2){		
			File swivel = ScriptingEngine.fileFromGit(
			"https://github.com/NeuronRobotics/NASACurisoity.git",
			"STL/lower-arm.STL");
			CSG swivelCSG  = Vitamins.get(swivel)
			.rotx(180)
			.movex(-dh.getR())
					.movez(-dh.getD())
			swivelCSG.setManipulator(manipulator);
			parts.add(swivelCSG)
		}
		if(linkIndex==3){		
			File swivel = ScriptingEngine.fileFromGit(
			"https://github.com/NeuronRobotics/NASACurisoity.git",
			"STL/mahli-apxs.STL");
			CSG swivelCSG  = Vitamins.get(swivel)
			.movex(-dh.getR())
					.movez(-dh.getD())
			swivelCSG.setManipulator(manipulator);
			parts.add(swivelCSG)
		}
		for(int i=0;i<parts.size();i++){
			parts.get(i).setColor(javafx.scene.paint.Color.WHITE)
		}
		return parts
	}
	@Override 
	public ArrayList<CSG> generateBody(MobileBase b ) {return new ArrayList<>();}
}

return cadGen