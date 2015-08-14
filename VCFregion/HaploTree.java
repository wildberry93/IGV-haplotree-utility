package VCFregion;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.AccessionID;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.MultipleSequenceAlignment;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.template.AbstractSequence;
import org.biojava.nbio.core.sequence.template.Compound;
import org.biojava.nbio.phylo.CheckTreeAccuracy;
import org.biojava.nbio.phylo.Comparison;
import org.biojava.nbio.phylo.ProgressListenerStub;
import org.biojava.nbio.phylo.ResidueProperties;
import org.biojava.nbio.phylo.ScoreMatrix;
import org.biojava.nbio.phylo.TreeConstructionAlgorithm;
import org.biojava.nbio.phylo.TreeConstructor;
import org.biojava.nbio.phylo.TreeType;
import org.forester.evoinference.distance.NeighborJoining;
import org.forester.evoinference.matrix.distance.BasicSymmetricalDistanceMatrix;
import org.forester.evoinference.matrix.distance.DistanceMatrix;
import org.forester.io.writers.PhylogenyWriter;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;

import net.sourceforge.olduvai.treejuxtaposer.TreeParser;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;

import org.forester.archaeopteryx.Archaeopteryx;

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
		System.out.println("tutaj!!!!");
		
		for (DNASequence sequence : sequences) {
			System.out.println(sequence.getAccession());
			msa.addAlignedSequence(sequence);
		}
		drawTree(msa);
	}
	
    public double[][] calculateDistanceMatrix(MultipleSequenceAlignment<DNASequence, NucleotideCompound> msa, TreeConstructionAlgorithm tca) {
       
        int numberOfSequences = msa.getSize();
        String[] sequenceString = new String[numberOfSequences];
        for (int i = 1; i < msa.getSize(); i++) {
            sequenceString[i] = msa.getAlignedSequence(i).getSequenceAsString();

        }

        double[][] distance = new double[numberOfSequences][numberOfSequences];

        int totalloopcount = (numberOfSequences / 2) * (numberOfSequences + 1);

        if (tca == TreeConstructionAlgorithm.PID) {
            int loopcount = 0;
            for (int i = 1; i < (numberOfSequences - 1); i++) {
        		System.out.println("jestem!!!!");

                for (int j = i; j < numberOfSequences; j++) {
                    loopcount++;
                    if (j == i) {
                        distance[i][i] = 0;
                    } else {
                        distance[i][j] = 100 - Comparison.PID(sequenceString[i], sequenceString[j]);

                        distance[j][i] = distance[i][j];
                    }
                }
            }
        } else {
            // Pairwise substitution score (with no gap penalties)
            ScoreMatrix pwmatrix = ResidueProperties.getScoreMatrix(tca.name());
            if (pwmatrix == null) {
                pwmatrix = ResidueProperties.getScoreMatrix(TreeConstructionAlgorithm.BLOSUM62.name());
            }
            int maxscore = 0;
            int end = sequenceString[0].length();
            int loopcount = 0;
            for (int i = 0; i < (numberOfSequences - 1); i++) {
                for (int j = i; j < numberOfSequences; j++) {
                    int score = 0;
                    loopcount++;
                    for (int k = 0; k < end; k++) {
                        try {
                            score += pwmatrix.getPairwiseScore(sequenceString[i].charAt(k), sequenceString[j].charAt(k));
                        } catch (Exception ex) {
                        	ex.printStackTrace();
                        }
                    }

                    distance[i][j] = (float) score;

                    if (score > maxscore) {
                        maxscore = score;
                    }
                }
            }

            for (int i = 0; i < (numberOfSequences - 1); i++) {
                for (int j = i; j < numberOfSequences; j++) {
                    distance[i][j] = (float) maxscore - distance[i][j];
                    distance[j][i] = distance[i][j];
                }
            }

        }
        return distance;
    }

	public void drawTree(MultipleSequenceAlignment<DNASequence, NucleotideCompound> msa) {
	    DistanceMatrix copyDistanceMatrix = null;
	    boolean verbose = false;
	    Phylogeny p = null;

		TreeConstructor<DNASequence, NucleotideCompound> treeConstructor = new TreeConstructor<DNASequence, NucleotideCompound>(
				msa, TreeType.NJ, TreeConstructionAlgorithm.PID, new ProgressListenerStub());
		BasicSymmetricalDistanceMatrix matrix = new BasicSymmetricalDistanceMatrix(msa.getSize());

		try {
			double[][] distances = calculateDistanceMatrix(msa, TreeConstructionAlgorithm.PID);

			for (int i = 1; i < matrix.getSize(); i++) {
				matrix.setIdentifier(i, msa.getAlignedSequence(i).getAccession().getID());
			}
			System.out.println("fedgrdwdwg");

			for (int col = 1; col < matrix.getSize(); col++) {
				for (int row = 0; row < matrix.getSize(); row++) {
					matrix.setValue(col, row, distances[col][row]);
				}
			}
			copyDistanceMatrix = CheckTreeAccuracy.copyMatrix(matrix);
			final List<Phylogeny> ps = new ArrayList<Phylogeny>();
	        final NeighborJoining nj = NeighborJoining.createInstance(verbose);
	        ps.add(nj.execute(matrix));
	        p = ps.get(0);
	        final PhylogenyWriter w = new PhylogenyWriter();
	        StringBuffer newickString = w.toNewHampshire(p, true, true);
	        String newick = newickString.toString();
	        System.out.println(newick);
	        
	        StringReader sr = new StringReader(newick);
	        BufferedReader br = new BufferedReader(sr);
	        
	        TreeParser tp = new TreeParser(br);
	        Tree t = tp.tokenize(0,"drzewko",null);
	        System.out.println(t.nodes);
	        
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		
	


		
		/*try {
			treeConstructor.process();
			String newick = treeConstructor.getNewickString(true, true);
			System.out.println(newick);
		} catch (Exception e) {
			e.printStackTrace();
		} */
		
		System.out.println("fedgrg");


	}

}