package VCFregion;

import java.util.ArrayList;
import java.util.List;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.AccessionID;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.MultipleSequenceAlignment;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.template.AbstractSequence;
import org.biojava.nbio.core.sequence.template.Compound;
import org.biojava.nbio.phylo.ProgressListenerStub;
import org.biojava.nbio.phylo.TreeConstructionAlgorithm;
import org.biojava.nbio.phylo.TreeConstructor;
import org.biojava.nbio.phylo.TreeType;

public class HaploTree <C extends AbstractSequence<D>, D extends Compound>{

	List<HaplotypeSequence> HaploSeqs;

	public HaploTree(List<HaplotypeSequence> HaploSeqs) {
		this.HaploSeqs = HaploSeqs;
	}

	public void Seqs2MSA() {

		List<DNASequence> sequences = new ArrayList<DNASequence>();
		for (HaplotypeSequence hapSeq : HaploSeqs) {
			try {
				DNASequence haplo_1 = new DNASequence(hapSeq.HaploRef);
				DNASequence haplo_2 = new DNASequence(hapSeq.HaploAlt);
				haplo_1.setAccession(new AccessionID(hapSeq.SampleName+"_1"));
				haplo_2.setAccession(new AccessionID(hapSeq.SampleName+"_2"));

				sequences.add(haplo_1);
				sequences.add(haplo_2);
			} catch (CompoundNotFoundException e) {
				System.out.println(e);
			}
		}

		MultipleSequenceAlignment<DNASequence, NucleotideCompound> msa = new MultipleSequenceAlignment<DNASequence, NucleotideCompound>();

		for (DNASequence sequence : sequences) {
			msa.addAlignedSequence(sequence);
		}

	   // MultipleSequenceAlignment<C, D> multipleSequenceAlignment = new MultipleSequenceAlignment<C, D>();
	    //multipleSequenceAlignment = (MultipleSequenceAlignment<C, D>) msa;

		drawTree(msa);
	}
	

	public void drawTree(MultipleSequenceAlignment<DNASequence, NucleotideCompound> msa) {

		TreeConstructor<DNASequence, NucleotideCompound> treeConstructor = new TreeConstructor<DNASequence,NucleotideCompound>(msa,TreeType.NJ,TreeConstructionAlgorithm.PID, new ProgressListenerStub());
		System.out.println("jdsfsdjhk");


		try {
			treeConstructor.process();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("fedgrg");


		// String newick = treeConstructor.getNewickString(true, true); 
	}

}