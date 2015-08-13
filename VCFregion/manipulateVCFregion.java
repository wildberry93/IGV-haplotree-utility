package VCFregion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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

	// Test paths to output files
	String testOut = "/Users/jagodajablonska/Desktop/treeTemp/out.gt.vcf.gz";
	String testPath = "/Users/jagodajablonska/Desktop/treeTemp/sample1.vcf";
	private List<HaplotypeSequence> haploSeqs = new ArrayList<HaplotypeSequence>();
	String tempDir = "/Users/jagodajablonska/Desktop/treeTemp/";

	public String getPaths() {
		return VCFfile;
	}

	public void setPaths(String filesIn) {
		VCFfile = filesIn;
		System.out.println(VCFfile);
	}

	public VCFFileReader readVCFfile(String inFile) {
		java.io.File inVCF = new java.io.File(inFile);
		VCFFileReader vcfData = new VCFFileReader(inVCF, false);
		vcfData.close();
		return vcfData;
	}

	public List<VariantContext> getVariantContext(VCFFileReader vcfData) {
		// Queries for records within the region specified. For some reason I cannot use it ;-(
		// CloseableIterator<VariantContext> vcfRegion = vcfData.query(chr,roiStart, roiEnd);

		// The loop alternative to query method
		CloseableIterator<VariantContext> vcfIter = vcfData.iterator();
		List<VariantContext> IterCopy = new ArrayList<VariantContext>(); 

		while (vcfIter.hasNext())
			IterCopy.add(vcfIter.next());

		return IterCopy;
	}

	public void getROI(RegionOfInterest reg){
		int roiStart = reg.getStart();
		int roiEnd = reg.getEnd();
		// String chr = reg.getChr();

		VCFFileReader vcfData = readVCFfile(VCFfile);
		ArrayList<VariantContext> vcfIterRegion = new ArrayList<VariantContext>();
		List<VariantContext> IterCopy = getVariantContext(vcfData);
		
		SAMSequenceDictionary refDict = vcfData.getFileHeader().getSequenceDictionary(); 
		VCFHeader vcfHeader = vcfData.getFileHeader();

		System.out.println("start: " + roiStart);
		System.out.println("end: " + roiEnd);

		for (VariantContext vc : IterCopy) {
			if (vc.getStart() >= roiStart && vc.getStart() <= roiEnd) {
				vcfIterRegion.add(vc);
			}
		}

		System.out.println("size" + vcfIterRegion.size());

		for (VariantContext vc : vcfIterRegion) {
			System.out.println(vc.getGenotypes().get(2).getGenotypeString());
		}

		try {
			saveRegionFile(vcfIterRegion, refDict, vcfHeader);
		} catch (Exception e) {
			System.out.println(e);
		}

		System.out.println("finished1!");

		RegionHaplotype haplo = new RegionHaplotype(testPath);
		haplo.runBeagle();

		getSequences(testOut);
		
		System.out.println("finished22!");

		
		HaploTree tree = new HaploTree(haploSeqs);
		
		tree.Seqs2MSA();
		
		try {
			deleteTemporaryFiles();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveRegionFile(ArrayList<VariantContext> vcfIterRegion, SAMSequenceDictionary refDict,
			VCFHeader vcfHeader) {

		// Build WriterBuilder
		VariantContextWriterBuilder writerBuilder = new VariantContextWriterBuilder();
		writerBuilder.setReferenceDictionary(refDict);
		writerBuilder.setOption(Options.INDEX_ON_THE_FLY);
		writerBuilder.setBuffer(8192);
		writerBuilder.setOutputFile(testPath);
		VariantContextWriter vcWriter = writerBuilder.build();

		// Write file
		vcWriter.writeHeader(vcfHeader);

		for (VariantContext vc : vcfIterRegion) {
			vcWriter.add(vc);
		}
		vcWriter.close();
	}

	public void getSequences(String phasingOut) {
		VCFFileReader vcfPhasingData = readVCFfile(phasingOut);
		List<VariantContext> PhasingVC = getVariantContext(vcfPhasingData);

		int numbSamples = PhasingVC.get(0).getNSamples(); // get the number of individuals

		// Get haplotypes for every sample
		for (int i = 0; i < numbSamples; i++) {
			String HaploAlt = "";
			String HaploRef = "";
			String SampleName = "";
			for (VariantContext vc : PhasingVC) {
				SampleName = vc.getGenotypes().get(i).getSampleName();
				HaploAlt = HaploAlt.concat(vc.getGenotypes().get(i).getAlleles().get(0).getBaseString());
				HaploRef = HaploRef.concat(vc.getGenotypes().get(i).getAlleles().get(1).getBaseString());
			}

			HaplotypeSequence HapSeq = new HaplotypeSequence(HaploRef, HaploAlt, SampleName);
			haploSeqs.add(HapSeq);

		}	

	}

	private void deleteTemporaryFiles() throws IOException {
		File dir = new File(tempDir);

		for (File file : dir.listFiles()) {
			try {
				Files.delete(file.toPath());
			} catch (NoSuchFileException x) {
				System.err.format("%s: no such" + " file or directory%n", file.toPath());
			}
		}
	}
}
