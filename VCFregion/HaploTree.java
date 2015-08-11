package VCFregion;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.MultipleSequenceAlignment;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.phylo.ProgressListenerStub;
import org.biojava.nbio.phylo.TreeConstructionAlgorithm;
import org.biojava.nbio.phylo.TreeConstructor;
import org.biojava.nbio.phylo.TreeType;

public class HaploTree{
	
	List<HaplotypeSequence>HaploSeqs;
	
	public HaploTree(List<HaplotypeSequence>HaploSeqs){
		this.HaploSeqs = HaploSeqs;
	}
	
	public void Seqs2MSA(){
		
		List<Sequence> sequences = new ArrayList<Sequence>();
		
		for(HaplotypeSequence hapSeq : HaploSeqs){

			try {
				Sequence haplo_1 = DNATools.createDNASequence(hapSeq.HaploRef, hapSeq.SampleName);
				Sequence haplo_2 = DNATools.createDNASequence(hapSeq.HaploAlt, hapSeq.SampleName);
				sequences.add(haplo_1);
				sequences.add(haplo_2);
			} catch (IllegalSymbolException e) {
				e.printStackTrace();
			}
		}
				
		System.out.println(sequences.size());
		
		MultipleSequenceAlignment msa = new MultipleSequenceAlignment();

	}
	
	public void drawTree(){
		//TreeConstructor<DNASequence, NucleotideCompound> treeConstructor = new TreeConstructor<DNASequence, NucleotideCompound>(multipleSequenceAlignment, TreeType.NJ, TreeConstructionAlgorithm.PID, new ProgressListenerStub());
		//treeConstructor.process();
		//String newick = treeConstructor.getNewickString(true, true);
	}
	


}