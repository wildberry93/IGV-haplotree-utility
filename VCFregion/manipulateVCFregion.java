package VCFregion;

import java.util.ArrayList;
import java.util.List;

import org.broad.igv.feature.RegionOfInterest;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.variantcontext.writer.*;

public class manipulateVCFregion {

	VariantContext variantContext;
	static String VCFfile;

	public String getPaths() {
		return VCFfile;
	}

	public void setPaths(String filesIn) {
		VCFfile = filesIn;
		System.out.println(VCFfile);
	}

	public VCFFileReader readVCFfile() {
		java.io.File inVCF = new java.io.File(VCFfile);
		VCFFileReader vcfData = new VCFFileReader(inVCF, false);
		vcfData.close();
		return vcfData;
	}

	public void getROI(RegionOfInterest reg) {
		int roiStart = reg.getStart();
		int roiEnd = reg.getEnd();
		String chr = reg.getChr();

		VCFFileReader vcfData = readVCFfile();

		// Queries for records within the region specified. For some reason I cannot use it ;-(
		// CloseableIterator<VariantContext> vcfRegion = vcfData.query(chr,roiStart, roiEnd);

		// The loop alternative to query method
		CloseableIterator<VariantContext> vcfIter = vcfData.iterator();

		ArrayList<VariantContext> vcfIterRegion = new ArrayList<VariantContext>(); 																			
		SAMSequenceDictionary refDict = vcfData.getFileHeader().getSequenceDictionary(); // sequence dict for required to save vcf 
		List<VariantContext> IterCopy = new ArrayList<VariantContext>(); // copy iterator to list so we can refer to the curr element
		VCFHeader vcfHeader = vcfData.getFileHeader();

		while (vcfIter.hasNext())
			IterCopy.add(vcfIter.next());

		System.out.println("start: " + roiStart);
		System.out.println("end: " + roiEnd);

		for (VariantContext vc : IterCopy) {
			if (vc.getStart() >= roiStart && vc.getStart() <= roiEnd) {
				vcfIterRegion.add(vc);
			}
		}

		System.out.println("size" + vcfIterRegion.size());

		for (VariantContext vc : vcfIterRegion) {
			System.out.println(vc.toString());
		}

		try {
			saveRegionFile(vcfIterRegion, refDict, vcfHeader);
		} catch (Exception e) {
			System.out.println(e);
		}

		System.out.println("finished!");
		
		String testPath = "/Users/jagodajablonska/Desktop/sample1.vcf";
        RegionHaplotype haplo = new RegionHaplotype(testPath);
        haplo.runBeagle();

	}

	public void saveRegionFile(ArrayList<VariantContext> vcfIterRegion, SAMSequenceDictionary refDict,
			VCFHeader vcfHeader) {

		// Build WriterBuilder
		VariantContextWriterBuilder writerBuilder = new VariantContextWriterBuilder();
		writerBuilder.setReferenceDictionary(refDict);
		writerBuilder.setOption(Options.INDEX_ON_THE_FLY);
		writerBuilder.setBuffer(8192);
		writerBuilder.setOutputFile("/Users/jagodajablonska/Desktop/sample1.vcf");
		VariantContextWriter vcWriter = writerBuilder.build();

		// Write file
		vcWriter.writeHeader(vcfHeader);

		for (VariantContext vc : vcfIterRegion) {
			vcWriter.add(vc);
		}
		vcWriter.close();
	}
}
