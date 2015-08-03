package VCFregion;

import java.io.File;
import java.util.Collection;
import java.util.List;

//import javax.media.jai.ROI;

import org.broad.igv.Globals;
import org.broad.igv.util.ResourceLocator;
import org.broad.igv.feature.RegionOfInterest;
import org.broad.igv.feature.genome.Genome;
import org.broad.igv.feature.genome.GenomeManager;
import org.broad.igv.ui.IGV;
import org.broad.igv.util.LongRunningTask;
import org.broad.igv.util.NamedRunnable;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class manipulateVCFregion{
	
	VariantContext variantContext;
	static String VCFfile;
	//RegionOfInterest reg;
	
	//public manipulateVCFregion(){}
	
	//public manipulateVCFregion(RegionOfInterest roi) {
	//	this.roi = roi;
	//}

	public String getPaths(){
		return VCFfile;
	}
	
	public void setPaths(String filesIn){
		this.VCFfile = filesIn;
		System.out.println(VCFfile);
	}

	public void getROI(RegionOfInterest reg){
		int roiStart = reg.getStart();
		int roiEnd = reg.getEnd();
		
		System.out.println(roiStart);
		System.out.println(roiEnd);
		
		//vcf variant objects for all variants from start to end
		java.io.File inVCF = new java.io.File(VCFfile);
		VCFFileReader vcfData = new VCFFileReader(inVCF, false);
		
		//VariantContext vc = new VariantContext(name, snpLoc, Arrays.asList(Aref, T));
		//VCFVariant(vc, roi.getChr());
		vcfData.close();

		System.out.println(vcfData);

	}
	
	public void saveRegionFile(int start, int end, int chr){
		
	}

	}
	