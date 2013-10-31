import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class MySpecies extends Species implements Comparable<MySpecies>{
    private String name; // The species' common name
    private String latinName; // The species scientific name
    private int weight; // The species weight. > 0
    private String collector; // The collector --first person to collect the species
    private String dna;      // The species' dna
    private String imageFilename; // Name of the file that contains the image
    private HashSet<Gene> genes; // If not null, contains the set of genes for dna
    private Gene[] allGene; // A sorted array allGenes of all genes that occur in any Species object
    private int[] indices; // A sorted array of indices into allGenes of genes that occur in this Species instance
    private int[][] geneDistance; // the distance matrix between the genes in allGene
    private int index; // index of this Species in respect to allSpeceis (set during precalculation) 

    /** Default constructor. Does nothing. */
    public MySpecies() {
    }

    /** Set the Species' common name to n */
    public void setName(String n) {
        name= n;
    }

    /** Return the Species' common name */
    public  String getName() {
        return name;
    }

    /** Set the Species' scientific name to n */
    public void setLatinName(String n) {
        latinName= n;
    }

    /** Return the Species' scientific name */
    public String getLatinName() {
        return latinName;
    }

    /** Set the Species' weight to w.
      Precondition: w > 0 */
    public  void setWeight(int w) {
        assert w > 0;
        weight= w;
    }

    /** Return the Species' weight */
    public int getWeight() {
        return weight;
    }

    /** Set the Species' collector to c */
    public void setCollector(String c) {
        collector= c;
    }

    /** Return the Species' collector */
    public String getCollector() {
        return collector;
    }

    /** Set the Species' DNA to dna */
    public void setDNA(String dna) {
        this.dna= dna;
        genes= null;
    }

    /** Return the Species' DNA */
    public String getDNA() {
        return dna;
    }

    /** Set the filename to point to imageFilename */
    public void setImageFilename(String imageFilename) {
        this.imageFilename= imageFilename;
    }

    /** Return the filename pointing to the Species' image */
    public String getImageFilename() {
        return imageFilename;
    }
    
    /** Return the Species' index in the allSpecies array*/
    public int getIndex(){
    	return index;
    }

    /**
     * Get the Species' genome. A genome is the set of genes parsed from raw
     * DNA. The return value of getGenome SHOULD NOT contain duplicate genes,
     * even if genes are duplicated in the raw DNA.
     * 
     * You can parse the genome when setDNA is called ("eager"), or when
     * getGenome is called ("lazy").
     * 
     * If the latter, avoid parsing the genome every time getGenome is called.
     * 
     * @return A duplicate-free set of genes found in this Species' DNA.
     */
    public Collection<Gene> getGenome() {
        if (genes != null) {
            return genes;
        }
        MyDNAParser p= new MyDNAParser();
        p.setDNA(dna);
        List<Gene> list= p.parse();
        genes= new HashSet<Gene>(list);
        return genes;
    }
    
    /** Return the indices of the Species*/
    public int[] getIndices(){
    	return indices;
    } 
    
    /** Return the gene distance matrix of the Species*/
    public int[][] getGeneDistance(){
    	return geneDistance;
    } 
    
    /** return a number < 0 if this Species is smaller than Species s1, 0 if same,
    and a number > 0 if greater.
    
    Whether a Species is smaller or greater than another species is determined by their common name 
    (lexicographical ordering of their common name) */
    @Override
    public int compareTo(MySpecies s1) {
    	return name.compareTo(s1.getName());
    }
    
    /** Within this MySpecies instance, set the following values:
     * (1) allGenes
     * (2) indices of the genes
     * (3) geneDistance fields
     * (4) the index of the Species within the allSpecies array
     * in order to make calculating distance between species more efficient later
     * 
     * precondition: 
     * 1. allGenes is sorted
     * 2. allGenes, geneDistance can't be null
     * 
     * @param: Gene[] allGene, int[][] geneDistance
     */
    public void precalculation(Gene[] allGenes, int[][] geneDistance, int i){
    	// set allGene and geneDistance fields
    	this.allGene = allGenes;
    	this.geneDistance = geneDistance;
    	
    	// find the indices of the genes and set the indices field accordingly
    	Collection genome = this.getGenome();
		int[] indices = new int[genome.size()];
		
		// for each gene in genome, find its index using binarySearch
		// and add the index to indices
		Iterator target = genome.iterator();
		for (int j=0; j < indices.length; j++){
			indices[j] = Arrays.binarySearch(allGenes, target.next());
		}
		
		// sort the indices and set the indices field
		Arrays.sort(indices);
    	this.indices = indices;
    	
    	// set the Species' index within the allSpecies array
    	this.index = i;
    }
    
    
    /** Return the distance between this and Species s
     * 
     * (1) compare this to s, getting a distance da
     * (2) compare s to this, getting a distance db
     * (3) the distance between these two species is the average of da and db
     * 
     * @param: Species s
     * precondition: can't be null
     * 
     * @return: distance between this and s
     */
    public int getDistance(Species s){
    	int da = distanceHelper(this, s);
    	int db = distanceHelper(s, this);
    	int distance = (da + db)/2;
    	return distance;
    }
    
    /**
	 * Given two Species instances (A and B), 
	 * obtain the distance from A to B (compare A to B)
	 * 
	 * For each gene in A, compute the distance to the closest gene in B; 
	 * the distance from A to B is the sum of those distances.
	 * 
	 * @param Species A, Species B 
	 * precondition: can't be null
	 * 
	 * @return distance from A to B
	 * 
	 */
    private static int distanceHelper(Species A, Species B){
    	int[][] m = ((MySpecies)A).getGeneDistance();
    	
    	int distance = 0;
    	int[] thisIndices = ((MySpecies)A).getIndices();
    	int[] otherIndices = ((MySpecies)B).getIndices();
    
    	// for each gene in thisIndices, find the closest gene in otherIndices
    	for (int thisIndex: thisIndices){
    		int min = m[thisIndex][otherIndices[0]];
    		
    		for(int j = 1; j < otherIndices.length; j++ ){
    			min = Math.min(min, m[thisIndex][otherIndices[j]]);
    		}
    		
    		// add up all the minimum distances
    		distance += min;
    	}
    	
    	return distance;
    }

    /** return representation of this instance. Contains all
      attributes --but for DNA, only the first 30 characters */
    public @Override String  toString()  {
        String d= getDNA();
        if (d != null) {
            d= d.substring(0, Math.min(d.length(),30));
        } 
        return "Name=\"" + getName() + "\"" +
        "\nLatinName=\"" + getLatinName() + "\"" +
        "\nImageFilename=\"" + getImageFilename() + "\"" +
        "\nWeight=" + getWeight() +
        "\nCollected-by=\"" + getCollector() + "\"" +
        "\nDNA=\"" + d + "\"";
    }

}
