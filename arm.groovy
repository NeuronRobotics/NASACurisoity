import java.nio.file.Paths;

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.FileUtil;
import com.neuronrobotics.bowlerstudio.vitamins.*;

String filename = "STL/swivel-bracket.STL"
// Load an STL file from a git repo
// Loading a local file also works here
File servoFile = ScriptingEngine.fileFromGit(
	"https://github.com/NeuronRobotics/NASACurisoity.git",
	filename);
// Load the .CSG from the disk and cache it in memory
double offset=6.5
CSG servo  = Vitamins.get(servoFile)
			.toZMax()
			.movez(offset)
			.movex(-offset)
			.movey(-11)

String outfile =servoFile.getAbsolutePath()+"_moved.STL";
FileUtil.write(Paths.get(outfile),
		servo.toStlString());
println "STL EXPORT to \n"+outfile+" from \n"+servoFile

return servo;