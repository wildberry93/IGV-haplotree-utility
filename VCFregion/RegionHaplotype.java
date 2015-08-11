package VCFregion;
import main.Main;


public class RegionHaplotype extends manipulateVCFregion{
	String VCFregion;
	String outPath;
	
	public RegionHaplotype(String VCFregion){
		this.VCFregion = VCFregion;
		String testOut = "/Users/jagodajablonska/Desktop/treeTemp/out.gt";
		this.outPath = testOut;
	}
	
	public void runBeagle(){
		String[] args = {"gt="+VCFregion, "out="+outPath};
		Main.main(args);	
	}
	
	public void handleBeagleOutput(){
		//Remember to check if the file was created!

	}
	
	
}