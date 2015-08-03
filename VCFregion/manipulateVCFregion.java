package VCFregion;

import java.util.ArrayList;
import org.broad.igv.feature.RegionOfInterest;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class manipulateVCFregion{
	
	VariantContext variantContext;
	static String VCFfile;

	public String getPaths(){
		return VCFfile;
	}
	
	public void setPaths(String filesIn){
		VCFfile = filesIn;
		System.out.println(VCFfile);
	}

	public VCFFileReader readVCFfile(){
		java.io.File inVCF = new java.io.File(VCFfile);
		VCFFileReader vcfData = new VCFFileReader(inVCF, false);
		vcfData.close();
		return vcfData;
	}
	
	@SuppressWarnings("deprecation")
	public void getROI(RegionOfInterest reg){
		int roiStart = reg.getStart();
		int roiEnd = reg.getEnd();
		String chr = reg.getChr();
		
		System.out.println(roiStart);
		System.out.println(roiEnd);
		
		VCFFileReader vcfData = readVCFfile();
		
		//Queries for records within the region specified. For some reason I cannot use it ;-(
		//CloseableIterator<VariantContext> vcfRegion = vcfData.query(chr, roiStart, roiEnd);
		
		//The loop alternative to query method
		CloseableIterator<VariantContext> vcfIter = vcfData.iterator();
		ArrayList<VariantContext> vcfIterRegion = new ArrayList<VariantContext>(); // genotypes in chosen region
		 
		while(vcfIter.hasNext()){
			if(vcfIter.next().getStart() >= roiStart && 
					vcfIter.next().getStart() <= roiEnd){	
				System.out.println(vcfIter.next().getStart());
				vcfIterRegion.add(vcfIter.next());
			}
		}
	}
	
	public void saveRegionFile(int start, int end, int chr){
		
	}

	}
	