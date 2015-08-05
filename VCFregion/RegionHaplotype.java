package VCFregion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.broad.igv.feature.RegionOfInterest;
import VCFregion.manipulateVCFregion;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class RegionHaplotype{
	File VCFregion;
	
	public RegionHaplotype(File VCFregion){
		this.VCFregion = VCFregion;
	}
	
	private void runBeagle(){
		
	}
	
}