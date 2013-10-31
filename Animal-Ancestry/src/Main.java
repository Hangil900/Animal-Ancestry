/* Time spent: 18 hours */
/* we used the A1 solution given in piazza */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/** Class to run assignment A4 (Species Comparison GUI)  */

public class Main {
	/**
	 * Obtain an array of all Species in the folder, SpeciesData.
	 * From an array of all Species, choose a subset of Species to do the following:
	 * 
	 * (1) print out the DNA strings of the genes
	 * 
	 * (2) print out a comparison matrix of genes. 
	 * Each cell (i, j) in the comparison matrix contains the computed distance between genes Gi and Gj.
	 * 
	 * (3) for each species, print out the common name and its indices into allGenes
	 * 
	 * (4) print out a comparison matrix of species.
	 * Each cell (i, j) in the comparison matrix contains the computed distance between Species i and Species j.
	 * 
	 * ((1)-(4) not in action for A4)
	 * 
	 * (5) run the GUI
	 */
	public static void main(String[] args) throws IOException {
		// get all the species
		Species[] allSpecies = getSpecies();
		
		testSpecies(allSpecies);	
	}
	
	/** Given an array of Species, 
	 * (1) print out all the genes in the species
	 * (2) print out the distance between all the genes
	 * (3) print out all the indices of genes in each species
	 * (4) print out the species distance matrix 
	 * (5) run the GUI
	 * 
	 * ((1)-(4) commented out for A4)
	 * 
	 * I. Calculate everything first
	 * II. Then, print out the things mentioned above 
	 * (print statements commented out for A4)
	 * 
	 * @param: an array of Species (allSpecies)
	 * precondition: can't be null
	 */
	public static void testSpecies(Species[] allSpecies) {
		// CALCULATIONS
		// obtain all the genes
		Gene[] allGenes = Main.getGenes(allSpecies);
		
		// calculate the gene distance matrix
		int[][] geneDistance = distanceMatrix(allGenes);
		
		// do the pre-calculation and compute the species distance matrix
		for (int i = 0; i < allSpecies.length; i++){
			Species s = allSpecies[i];
			((MySpecies) s).precalculation(allGenes, geneDistance, i);
		}
		
		int[][] speciesMatrix = speciesDistanceMatrix(allSpecies);
		
		/**
		// PRINT
		// (1) Print all the genes
		printHeader(allGenes);
		
		// (2) print the gene distance matrix
		print(geneDistance, 'G');
		
		// (3) print out the indices of each species
		for (Species s: allSpecies) {
			int[] indices = ((MySpecies) s).getIndices();
			System.out.println(s.getName() + " " + Main.toString(indices));
		}
		
		// (4) print out the species distance matrix
		print(speciesMatrix, 'S');
		*/
		
		// (5) By creating an A4GUI object, run the GUI
		A4GUI a4gui = new A4GUI(allSpecies, speciesMatrix);
		a4gui.run();

	}
	
	
	 /** Return a sorted array of all Species in the folder, SpeciesData.*/ 
    public static Species[] getSpecies() throws IOException {
    	// obtain all the files in SpeciesData folder
    	File folder = new File("SpeciesData");
    	File[] allFiles = folder.listFiles();
    	
    	ArrayList<Species> speciesList = new ArrayList<Species>();
    	MySpeciesReader sReader = new MySpeciesReader();
    	
    	// for each File f in allFiles, make sure it's a .dat file
    	// if it's a .dat file, add it to the speciesList
    	for (File f: allFiles){
    		String name = f.getName();
    		if (name.endsWith(".dat") ){
    			Species s = sReader.readSpecies(f);
    			speciesList.add(s);
    		} 
    	}
    	
    	// convert the ArrayList of Species into a sorted array 
    	Species[] result = speciesList.toArray(new Species[speciesList.size()]);
    	Arrays.sort(result);
    	return result;
    }
    
    /** Return a distance matrix for species
     *  
     * @param: an array of Species (Species[] species)
     * precondition: can't be null
     * 
     * @return: a distance matrix between all the species in the given array
     */
    public static int[][] speciesDistanceMatrix(Species[] species){
    	int[][] matrix = new int[species.length][species.length];
    	
    	for (int i = 0; i < species.length; i++) {
			for (int j = i; j < species.length; j++) {
				if (species[i] instanceof MySpecies){
					int distance = ((MySpecies) species[i]).getDistance(species[j]);
					matrix[i][j] = distance;
					matrix[j][i] = distance;
					
				}
			}
		}
    	return matrix;
    }
	
	/** Compute and return a distance matrix for genes 
	 * 
	 * @param an array of Gene objects (can't be null)
	 * 
	 * @return a 2-dimensional array where each cell(i, j) contains a distance between
	 * Gene i and Gene j.
	 * 
	 */
	public static int[][] distanceMatrix(Gene[] genes){
		int size = genes.length;
		int[][] matrix = new int[size][size];
		Arrays.sort(genes);
		
		for (int i = 0; i < genes.length; i++) {
			for (int j = i; j < genes.length; j++) {
				if (genes[i] instanceof MyGene){
					int distance = ((MyGene) genes[i]).getDistance(genes[j]);
					matrix[i][j] = distance;
					matrix[j][i] = distance;
				}
			}
		}
		
		return matrix;
	}
	
	/** Return a sorted array of all genes (with no duplicates) in species 
	 * @param: an array of Species objects (can't be null)
	 * @return: a sorted array of Genes with no duplicates 
	 * */
    public static Gene[] getGenes(Species[] species) {
        HashSet<Gene> geneSet= new HashSet<Gene>();
        for (Species sp : species) {
            MyDNAParser p= new MyDNAParser(sp.getDNA());
            List<Gene> geneList= p.parse();
            for (Gene g : geneList) {
                geneSet.add(g);
            }
        }

        Gene[] gArray= getGenes(geneSet);

        return gArray;
    }
	

    /** Return a sorted array of genes in hs 
     * @param: Hashset of Genes (can't be null)
     * @return: a sorted array of genes 
     */
    public static Gene[] getGenes(HashSet<Gene> hs) {
        int n= hs.size();      // The number of genes
        Gene[] geneArray= new Gene[n];
        hs.toArray(geneArray);
        Arrays.sort(geneArray);
        return geneArray;
    }
    
    /** Print the Genes in "genes", one per line, as described in A2 handout. 
     * precondition: genes can't be null 
     */
    public static void printHeader(Gene[] genes) {
        for (int r= 0; r < genes.length; r= r+1) {
            System.out.println("G" + r + "=" + genes[r]);
        }
    }

    /** Print matrix m, with a header row, as per the A0 handout, with
        GS being 'G' or 'S' for gene or species 
       
        @param: matrix, m, and a character 
        precondition: 
        1) m can't be null
        2) GS is either 'G' or 'S'
       
        */
    public static void print(int[][] m, char GS) {
        // Print the matrix header
        String row= "";
        for (int r= 0; r < m.length; r= r+1) {
            if (r < 10)
                row= row + "    " + GS + r;
            else if (r < 100) row= row + "   " + GS + r;
            else row= row + "  " + GS + r;
        }
        System.out.println(row);

        // Print the matrix
        for (int r= 0; r < m.length; r= r+1) {
            row= "";
            for (int c= 0; c < m[r].length; c= c+1) {
                row= row + String.format("%6d", m[r][c]);
            }
            row= row + "     // " + GS + r;
            System.out.println(row);
        }
    }
    
    
	/**
	 * Return a list of elements of b, separated by ", " and delimited by [ and ].
	 * @param: b, int array
	 * precondition: can't be null
	 * 
	 * @return: a string representation of the array
	 */
	public static String toString(int[] b) {
		String res = "[";
		for (int k = 0; k < b.length; k = k + 1) {
			if (k > 0)
				res = res + ", ";
			res = res + b[k];
		}
		return res + "]";
	}
}

