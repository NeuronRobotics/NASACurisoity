import com.neuronrobotics.bowlerstudio.creature.ICadGenerator;
import com.neuronrobotics.bowlerstudio.creature.CreatureLab;
import org.apache.commons.io.IOUtils;
import com.neuronrobotics.bowlerstudio.vitamins.*;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics
import com.neuronrobotics.sdk.addons.kinematics.MobileBase

import java.nio.file.Paths;

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.FileUtil;
import com.neuronrobotics.bowlerstudio.vitamins.*;
println "Loading STL file"
// Load an STL file from a git repo
// Loading a local file also works here

return new ICadGenerator(){
	@Override 
	public ArrayList<CSG> generateCad(DHParameterKinematics d, int linkIndex) {return new ArrayList<>()}
	@Override 
	public ArrayList<CSG> generateBody(MobileBase b ) {
		ArrayList<CSG> allCad=new ArrayList<>();
		double size =40;

		File mainBodyFile = ScriptingEngine.fileFromGit(
			"https://github.com/NeuronRobotics/NASACurisoity.git",
			"STL/body.STL");
		File USP1R_file = ScriptingEngine.fileFromGit(
			"https://github.com/NeuronRobotics/NASACurisoity.git",
			"STL/upper-suspension-p1-right.STL");
//		File LSP1R_file = ScriptingEngine.fileFromGit(
//			"https://github.com/NeuronRobotics/NASACurisoity.git",
//			"STL/lower-suspension-p1-right.STL");
		File USP2R_file= ScriptingEngine.fileFromGit(
			"https://github.com/NeuronRobotics/NASACurisoity.git",
			"STL/upper-suspension-p2-right.STL");
		
		File USP1L_file = ScriptingEngine.fileFromGit(
			"https://github.com/NeuronRobotics/NASACurisoity.git",
			"STL/upper-suspension-p1-left.STL");
//		File LSP1L_file= ScriptingEngine.fileFromGit(
//			"https://github.com/NeuronRobotics/NASACurisoity.git",
//			"STL/lower-suspension-p1-left.STL");
		File USP2L_file = ScriptingEngine.fileFromGit(
			"https://github.com/NeuronRobotics/NASACurisoity.git",
			"STL/upper-suspension-p2-left.STL");
			
		//changed
//		File LSP2R_file = ScriptingEngine.fileFromGit(
//			"https://github.com/NeuronRobotics/NASACurisoity.git",
//			"STL/lower-suspension-p2-right.STL");		
//		File LSP2L_file= ScriptingEngine.fileFromGit(
//			"https://github.com/NeuronRobotics/NASACurisoity.git",
//			"STL/lower-suspension-p2-left.STL");	
		/*
		double upperHeightAdjust = 2.175
		double lowerAdjustAngle = 4.6
		double UpperOffset = -78.5
		double heightOfUpperHinge = 27+upperHeightAdjust
		double offsetOfLowerBracket = 8.5
		double LowerOffset  = 65.25
		double mainWheelAlignemnt=-5
		double lowerHingeOffset = 1
		*/
		double widthOfBody =43
	
		// Load the .CSG from the disk and cache it in memory
		CSG body  = Vitamins.get(mainBodyFile)

		CSG USP1R  = Vitamins.get(USP1R_file).movey(widthOfBody)
		//CSG LSP1R  = Vitamins.get(LSP1R_file).movey(widthOfBody)
		CSG USP2R  = Vitamins.get(USP2R_file).movey(widthOfBody)
		//CSG LSP2R  = Vitamins.get(LSP2R_file).movey(widthOfBody)
		CSG USP1L  = Vitamins.get(USP1L_file).movey(-widthOfBody)
		//CSG LSP1L  = Vitamins.get(LSP1L_file).movey(-widthOfBody)
		CSG USP2L  = Vitamins.get(USP2L_file).movey(-widthOfBody)
		//CSG LSP2L  = Vitamins.get(LSP2L_file).movey(-widthOfBody)


/*
		FileUtil.write(Paths.get(USP1L_file.getAbsolutePath()),
							USP1L.toStlString());
		FileUtil.write(Paths.get(USP2L_file.getAbsolutePath()),
							USP2L.toStlString());
		FileUtil.write(Paths.get(LSP1L_file.getAbsolutePath()),
							LSP1L.toStlString());
		FileUtil.write(Paths.get(LSP2L_file.getAbsolutePath()),
							LSP2L.toStlString());
		//right					
		FileUtil.write(Paths.get(USP1R_file.getAbsolutePath()),
							USP1R.toStlString());
		FileUtil.write(Paths.get(USP2R_file.getAbsolutePath()),
							USP2R.toStlString());
		FileUtil.write(Paths.get(LSP1R_file.getAbsolutePath()),
							LSP1R.toStlString());
		FileUtil.write(Paths.get(LSP2R_file.getAbsolutePath()),
							LSP2R.toStlString());
*/
		def parts = [body ,USP1R,
			//LSP1R,
			USP2R,
			//LSP2R,
			USP1L,
			//LSP1L,
			USP2L,
			//LSP2L
			 ]
		
		for(int i=0;i<parts.size();i++){
			parts.get(i)
				.setColor(javafx.scene.paint.Color.GRAY)
				.setManipulator(b.getRootListener())
		}
		body.setColor(javafx.scene.paint.Color.WHITE)
		
		return parts;
	}
};