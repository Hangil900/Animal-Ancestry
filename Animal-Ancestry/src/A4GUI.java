import java.util.Arrays;
import java.util.Comparator;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import a4GUI.ComparisonGUI;

public class A4GUI extends ComparisonGUI{
	// an array of all the Species
	private Species[] allSpecies; 
	
	// species distance matrix
	private int[][] matrix; 
	
	// matrix of Species where row i stores an array that's sorted
	// based on how close all the other species are to species i.
	private Species[][] relatives; 
	
	// image and label for the farthest species
	protected JLabel farthestLabel; 
    protected JLabel farthestImage; 
    	
	
	/** An instance for n species. n > 0. */
	public A4GUI(int n){
		super(n);	
	}
	
	/**
	 * constructor with two parameters: 
	 * @param the array of Species (already sorted by name)  
	 * @param the species distance matrix
	 * 
	 * (1) call the super-constructor
	 * (2) set up the image for each cell
	 * (3) for each of the species, find the order of the closest Species
	 * (4) print out for each Species i, the order of the closeness of all the other Species
	 * ((4) is commented out for A4)
	 */
	public A4GUI(Species[] allSpecies, int[][] matrix){
		// call the super-constructor
		super(allSpecies.length);
		this.allSpecies = allSpecies;
		this.matrix = matrix;
		
		// set up the image for each species in each cell
		for (int i = 0; i < allSpecies.length; i++){
			String imageFile = "SpeciesData/" + allSpecies[i].getImageFilename();
			setCellImage(i, imageFile);
		}
		
		Species[][] relatives = new Species[allSpecies.length][allSpecies.length];
		
		// for each of the species, find the order of the closest Species
		for (int j = 0; j < allSpecies.length; j ++){
			Species[] closest = allSpecies.clone();
			
			// sort an array of allSpecies according to how close each species is to Species j
			Comparator c = new SpeciesComparator(matrix, j);
			Arrays.sort(closest, c);
			relatives[j] = closest;
		}
		
		// set the relatives field
		this.relatives = relatives;
		
		/**
		// PRINT RELATIVES
		// for each species i, 
		// print out all the other species in the order of the closeness to species i 
		for (int a=0; a<relatives.length;a++){
			for(int b=0; b<relatives[a].length; b++){
				if (b == 0) {
					System.out.printf("%24s: ", relatives[a][b].getName());
				}
				else{
					int selectedSP = ((MySpecies)relatives[a][0]).getIndex();
					int currentSP = ((MySpecies)relatives[a][b]).getIndex();
					System.out.printf("%24s %8d,", relatives[a][b].getName(), matrix[selectedSP][currentSP]);
				}

			}
			System.out.println("\n");
		}
		*/
	}
	
	
	/** Add to field comparisonBox the stuff that goes into the right panel, i.e.
    the label and image for the selected species, its closest species, and farthest species.
    */
	@Override
	public void fixComparisonBox(){
		// Add the label and the image for the selected Species to the Comparison Box
		comparisonBox.add(selectedLabel);
		comparisonBox.add(selectedImage);
		
		// Add the label and the image for the closest Species to the Comparison Box
		comparisonBox.add(closestRelatedLabel);
		comparisonBox.add(closestRelatedImage);
		
		// Add the label and the image for the farthest Species to the Comparison Box
		farthestLabel = new JLabel("Farthest relative: ");
		farthestImage = new JLabel();
		
		comparisonBox.add(farthestLabel);
		comparisonBox.add(farthestImage);

	}
	
	/** Set the name and image file of the current farthest species
     *  to n and f, respectively.
     *  These will appear under "Farthest relative:" at the right of the window.
     */
    public void setFarthestInfo(String n, String f) {
        farthestImage.setIcon(new ImageIcon(f));
        farthestLabel.setText("Farthest relative: " + n  + "  ");
    }
	
	/** Place the image for species number i, the image for its closest relative,
		and its farthest relative in the east panel. 
		Change the background colors of the species to
        indicate distance from species number i. */
	@Override
	public void onSelectCell(int  i){
		// SET THE SELECTED'S INFO (name and its image)
		String selectedName = allSpecies[i].getName();
		String selectedImageFile = "SpeciesData/" + allSpecies[i].getImageFilename();
		setSelectedInfo(selectedName, selectedImageFile);
		
		// SET THE CLOSEST'S INFO (name and its image)
		Species closest = relatives[i][1];
		String closestName = closest.getName();
		String closestImageFile = "SpeciesData/" + closest.getImageFilename();
		setClosestRelativeInfo(closestName, closestImageFile);
		
		// SET THE FARTHEST'S INFO (name and its image)
		Species farthest = relatives[i][relatives[i].length-1];
		String farthestName = farthest.getName();
		String farthestImageFile = "SpeciesData/" + farthest.getImageFilename();
		setFarthestInfo(farthestName, farthestImageFile);
		
		// SET UP THE COLORS FOR EACH CELL
		for (int k = 0; k < relatives[i].length; k++){
			// the index within the array (relatives[i]) indicates how close it is to Species i
			int closeness = k;
			int cellNumber = Arrays.binarySearch(allSpecies, relatives[i][k]);

			// Obtain the indices for the closest and the current Species
			int closestSP = ((MySpecies)relatives[i][1]).getIndex();
			int currentSP = ((MySpecies)relatives[i][k]).getIndex();
			int farthestSP = ((MySpecies)relatives[i][relatives[i].length-1]).getIndex();


			// Selected species (closeness = 0)
			// Color = BLUE
			if (closeness == 0) {
				try {
					setCellColor(i, 0, 0, 1);
				} catch (Exception e) {
				}
			}
			
			// Closest species
			// (i) closeness = 1 
			// (ii) if it has the same distance on the species distance 
			// matrix as the species with closeness of 1
			// Color = GREEN
			else if (closeness == 1 || matrix[i][closestSP] == matrix[i][currentSP]) {
				try {
					setCellColor(cellNumber, 0, 1, 0);
				} catch (Exception e) {
				}
			}
			
			// Farthest species
			// (i) closeness = length - 1
			// (ii) if it has the same distance on the species distance 
			// matrix as the species with closeness of (length - 1) 
			// Color = RED
			else if (closeness == relatives[i].length || matrix[i][farthestSP] == matrix[i][currentSP]){
				try {
					setCellColor(cellNumber, 1, 0, 0);
				} catch (Exception e) {
				}
			}

			// others (not selected nor closest nor farthest)
			else{
				// the hue is defined by the its order statistics
				// the further it is from the selected Species, the darker it is
				double hue = 1- (double) closeness / (allSpecies.length-1);

				try {
					setCellColor(cellNumber, hue, hue, hue);
				} catch (Exception e) {
				}
			}
		}
	}

}


/** Comparator to compare two Species according to its closeness to Species with an index i*/ 
class SpeciesComparator implements Comparator<Species>{
	// a species distance matrix (each cell(i, j) represents the distance between species i and j)
	private int[][] matrix; 
	// an index for the Species that we are comparing to.
	private int index;
	
	
	/**a constructor with two parameters:
	 * @param: a species distance matrix
	 * @param: an index for the Species that we are comparing to
	 * 
	 * set the matrix and index fields accordingly
	 */
	public SpeciesComparator(int[][] matrix, int index){
		this.matrix = matrix;
		this.index = index;
	}
	
	
	/** Given two Species, s1 and s2, it decides the ordering of two Species
	 * 
	 * One species is considered smaller, 
	 * if its distance to Species i is smaller than that of the other species
	 * 
	 * return >0 if s1 is bigger and <0 if s2 is bigger
	 */
	@Override
	public int compare(Species s1, Species s2) {
		int value1 = matrix[index][((MySpecies)s1).getIndex()];
		int value2 = matrix[index][((MySpecies)s2).getIndex()];
		return value1-value2;
	}
}

