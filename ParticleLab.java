package Final;
/**
 *  Particle Project
 */
import java.awt.*;

public class ParticleLab{
	static final int NBR_ROWS  = 160;  //180 
	static final int NBR_COLS  = 180;  //180 
	static final int CELL_SIZE = 800;  //800

	static final String FILE_NAME     = "ParticleLabFile.txt";         //This is the name of the input file.
	static final String NEW_FILE_NAME = "ParticleLabFileTesting.txt";  //This is the name of the file you are saving.

	//add constants for particle types here
	public static final int DESTROYER = 0;
	public static final int GENERATOR = 1;
	public static final int METAL     = 2;
	public static final int SAND      = 3;
	public static final int DIRT      = 4;
	public static final int MUD       = 5;
	public static final int WATER     = 6;
	public static final int ICE       = 7;
	public static final int OIL       = 8;
	public static final int VAPOR     = 9;
	public static final int EMPTY     = 10;
	public static final int SEED      = 11;
	public static final int VIRUS     = 12;
	public static final int GRASS     = 13;

	public static final int GRAVITY   = 13;
	public static final int SAVEFILE  = 14;
	public static final int LOADFILE  = 15;
	public static final int RESET     = 16;
	public static final int RANDOMIZE = 17;

	public static final int NBR_MENU_ITEMS = 18;
	public static final int TOTALNBROFPARTICLES = 11;

	public static final int NORMAL_GRAVITY   =  1;
	public static final int INVERT_GRAVITY   = -1;

	public static final int NON_MOVING_PARTICLE = 5;

	//liquid, gravity, spread, density, wrap
	public static final int[] DESTROYER_INFO = {NON_MOVING_PARTICLE,0,0,5,1};
	public static final int[] GENORATOR_INFO = {NON_MOVING_PARTICLE,0,0,5,1};
	public static final int[] METAL_INFO     = {NON_MOVING_PARTICLE,0,0,5,1};
	public static final int[] SAND_INFO      = {1,NORMAL_GRAVITY,50,3,1};
	public static final int[] SEED_INFO      = {1,NORMAL_GRAVITY,50,3,1};
	public static final int[] DIRT_INFO      = {1,NORMAL_GRAVITY,50,3,0};
	public static final int[] MUD_INFO       = {1,NORMAL_GRAVITY,5,3,0};
	public static final int[] WATER_INFO     = {0,NORMAL_GRAVITY,40,2,1};
	public static final int[] ICE_INFO       = {1,NORMAL_GRAVITY,10,2,1};
	public static final int[] OIL_INFO       = {0,NORMAL_GRAVITY,70,1,1};
	public static final int[] VAPOR_INFO     = {0,INVERT_GRAVITY,20,1,1};
	public static final int[] EMPTY_INFO     = {NON_MOVING_PARTICLE,0,0,0,1};
	public static final int[] VIRUS_INFO     = {NON_MOVING_PARTICLE,0,0,0,1};
	public static final int[] GRASS_INFO     = {NON_MOVING_PARTICLE,0,0,5,1};

	public static final int[][] OBJECT_INFO = {DESTROYER_INFO, GENORATOR_INFO, METAL_INFO, SAND_INFO, DIRT_INFO, MUD_INFO, WATER_INFO, ICE_INFO, OIL_INFO, VAPOR_INFO, EMPTY_INFO, SEED_INFO, VIRUS_INFO, GRASS_INFO};

	public static boolean worldGravity = true;

	public static final int DOWN       = 0;
	public static final int DOWN_WARP  = 1;
	public static final int LEFT       = 2;
	public static final int RIGHT      = 3;
	public static final int LEFT_WARP  = 4;
	public static final int RIGHT_WARP = 5;

	public static Color sand   = new Color(255, 255, 102);
	public static Color sky    = new Color(182, 241, 255);
	public static Color purple = new Color(152, 47, 163);
	public static Color green  = new Color(29, 100, 29);
	public static Color grass  = new Color(19, 143, 10);
	public static Color sun    = new Color(255, 155, 48);
	public static Color ice    = new Color(66, 242, 245);
	public static Color vapor  = new Color(179, 175, 175);
	public static Color mud    = new Color(101, 67, 33);
	public static Color dirt   = new Color(181, 101, 29);
	public static Color seed   = new Color(138, 255, 99);
	public static Color oil    = new Color(4, 20, 69);
	public static Color metal  = new Color(148, 145, 138);
	public static final Color[] OBJECT_COLOR = {purple, green, metal, sand, dirt, mud, Color.blue, ice, oil, vapor, Color.black, seed,Color.red,grass};
	//do not add any more global fields

	public static int[][] particleGrid;
	private LabDisplay display;

	//---------------------------------------------------------------------------------------------------------
	/**
	 * Calls the entry point for this program.
	 * 
	 * @param args Null: Not used in this program.
	 */
	public static void main(String[] args){
		System.out.println("================= Starting Program =================");
		System.out.println("ROWS: " + NBR_ROWS + "\nCOLS: " + NBR_COLS + "\nCELL_SIZE: " + CELL_SIZE + "\n");
		ParticleLab lab = new ParticleLab(NBR_ROWS, NBR_COLS);  //creates the object
		lab.run();
	}

	/**SandLab constructor - ran when the above lab object is created
	 * @param numRows The number of rows in the main text file.
	 * @param numCols The number of columns in the main text file. 
	 */ 
	public ParticleLab(int numRows, int numCols){
		String[] names = new String[NBR_MENU_ITEMS]; 

		names[DESTROYER]= "Destroyer";
		names[GENERATOR]= "Generator";
		names[METAL]    = "Metal";
		names[SAND]     = "Sand";
		names[SEED]     = "Seed";
		names[DIRT]     = "Dirt";
		names[MUD]      = "Mud";
		names[WATER]    = "Water";
		names[ICE]      = "Ice";
		names[OIL]      = "Oil";
		names[VAPOR]    = "Vapor";
		names[EMPTY]    = "Erase";
		names[VIRUS]    = "Virus";
		names[GRAVITY]  = "Gravity";
		names[SAVEFILE] = "SaveFile";
		names[LOADFILE] = "LoadFile";
		names[RESET]    = "Reset";
		names[RANDOMIZE]= "Randomize";

		display      = new LabDisplay("SandLab", numRows, numCols, names);  //uses the LabDisplay.class file 
		particleGrid = new int[numRows][numCols];


		if (FILE_NAME != "") {  
			System.out.println("Attempting to load: " + FILE_NAME);
			particleGrid = ParticleLabFiles.readFile(FILE_NAME);   
		} 
	}

	/** locationClicked -- called when the user clicks on a location using the given tool
	 * @param row -- This is the y cord on the grid
	 * @param col -- This is the x cord on the grid
	 * @param tool -- This is the tool the user has selected*/
	private void locationClicked(int row, int col, int tool)
	{
		if (tool >= GRAVITY) 
		{
			if(tool == SAVEFILE)
			{
				ParticleLabFiles.writeFile(particleGrid, NEW_FILE_NAME);
			}
			else if(tool == LOADFILE) {
				System.out.println("Attempting to load: " + NEW_FILE_NAME);
				particleGrid = ParticleLabFiles.readFile(NEW_FILE_NAME);
			}
			else if (tool == RESET) {
				for (int r = 0; r < particleGrid.length; r++) {
					for (int c = 0; c < particleGrid[1].length; c++) {
						particleGrid[r][c] = EMPTY;
					}
				}
			}
			else if(tool == RANDOMIZE) {
				ParticlePhysics.randomizeTheBoard(particleGrid);
			}
			else
			{
				if(worldGravity)
				{
					worldGravity = false;
				}
				else
				{
					worldGravity = true;	
				}
			}
		}
		else if(particleGrid[row][col] <= EMPTY || tool == EMPTY)
		{
			particleGrid[row][col] = tool;
		} 
	}

	/** updateDisplay -- Examines each element of the 2D particleGrid and paints a color onto the display*/
	public void updateDisplay() {
		for (int r = 0; r < NBR_ROWS; r++) {
			for (int c = 0; c < NBR_COLS; c++) {
				if (particleGrid[r][c] != EMPTY) {
					display.setColor(r, c, OBJECT_COLOR[particleGrid[r][c]]);

					if(particleGrid[r][c] == DESTROYER) {
						int destroyerR = getRandomNumber(150,255);
						int destroyerG = getRandomNumber(2,94);
						int destroyerB = getRandomNumber(148,255);
						display.setColor(r, c, new Color(destroyerR, destroyerG, destroyerB));

					}
				} 
				else if(ParticlePhysics.bubbles(r, c)) {
					display.setColor(r, c, Color.white);
				}

				else if(particleGrid[r][c] == EMPTY){
					int num = NBR_ROWS - r;
					display.setColor(r, c, new Color(num, num + 90, 255));
				}
				//TODO: add sun rays to the sun
				if(ParticlePhysics.sun(r, c)) {
					for(int i = 0; i < 10; i++) {
						for(int b = 0; b < 10; b++) {
							if(r > 8 && particleGrid[r-i][c-b] == EMPTY) {
								int sunR = getRandomNumber(245, 245);
								int sunG = getRandomNumber(239, 164);
								int sunB = getRandomNumber(66, 66);
								display.setColor(r-i, c-b, new Color(sunR, sunG, sunB));
							}
						}
					}	
				}
			}
		}
	}

	/**wrapUpOrDown -- This will test if wrapping down or up is valid
	 * @param row -- This is the y cord on the grid
	 * @param col -- This is the x cord on the grid
	 * @param particleDensity -- This is the particles density value
	 * @param gravity -- This is used to test which way the particle will move
	 * @return boolean -- This will return true if it can wrap down
	 */
	public static boolean wrapUpOrDown(int row,int col,int particleDensity,boolean gravity)
	{
		if(gravity)
		{
			if(row >= (NBR_ROWS-1))
			{	
				if(OBJECT_INFO[particleGrid[0][col]][3] < particleDensity && OBJECT_INFO[particleGrid[0][col]][0] != 1)
				{
					return true;
				}
			}
		}
		else
		{
			if(row < 1)
			{
				if(OBJECT_INFO[particleGrid[NBR_ROWS -1][col]][3] < particleDensity && OBJECT_INFO[particleGrid[NBR_ROWS -1][col]][0] != 1)
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * moveRight -- This will test if the particle can move right
	 * @param row -- This is the y cord on the grid
	 * @param col -- This is the x cord on the grid
	 * @param liquid -- This is an int value that tests to see how much the value needs to spread
	 * @param particleDensity -- This is the particles density value
	 * @return boolean -- This will return true if the particle can move right
	 */
	public static boolean moveRight(int row, int col,int liquid,int particleDensity)
	{
		if(onTopOfGenerator(row,col))
		{
			return false;
		}
		if(col != NBR_COLS-1)
		{
			//Just Right
			if(OBJECT_INFO[particleGrid[row+liquid][col+1]][3] < particleDensity && OBJECT_INFO[particleGrid[row][col+1]][3] < particleDensity
					&& OBJECT_INFO[particleGrid[row+liquid][col+1]][0] != 1 && OBJECT_INFO[particleGrid[row][col+1]][0] != 1)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * moveLeft -- This will see if the particle can move left
	 * @param row -- This is the y cord on the grid
	 * @param col -- This is the x cord on the grid
	 * @param liquid -- This is an int value that tests to see how much the value needs to spread
	 * @param particleDensity -- This is the particles density value
	 * @return boolean -- This will return true if the particle can move left
	 */
	public static boolean moveLeft(int row, int col,int liquid,int particleDensity)
	{
		if(onTopOfGenerator(row,col))
		{
			return false;
		}
		if(col != 0)
		{
			if(OBJECT_INFO[particleGrid[row+liquid][col-1]][3] < particleDensity && OBJECT_INFO[particleGrid[row][col-1]][3] < particleDensity
					&& OBJECT_INFO[particleGrid[row+liquid][col-1]][0] != 1 && OBJECT_INFO[particleGrid[row][col-1]][0] != 1)
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * onTopOfGenerator -- This will test if the particle is on top of a generator
	 * @param row -- This is the y cord on the grid
	 * @param col -- This is the x cord on the grid
	 * @return boolean -- This will return true if the particle is on top of a generator
	 */
	public static boolean onTopOfGenerator(int row,int col)
	{
		if(worldGravity)
		{
			if(row == NBR_ROWS-1)
			{
				if(particleGrid[1][col] == GENERATOR)
				{
					return true;
				}
			}
			else
			{
				if(particleGrid[row+1][col] == GENERATOR)
				{
					return true;
				}
			}
		}
		else
		{
			if(row == 0)
			{
				if(particleGrid[NBR_ROWS-1][col] == GENERATOR)
				{
					return true;
				}
			}
			else
			{
				if(particleGrid[row-1][col] == GENERATOR)
				{
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * wrapRight -- This will see if the particle can wrap right
	 * @param row -- This is the y cord on the grid
	 * @param col -- This is the x cord on the grid
	 * @param liquid -- This is an int value that tests to see how much the value needs to spread
	 * @param particleDensity -- This is the particles density value
	 * @return boolean -- This will return true if the particle can wrap right
	 */
	public static boolean wrapRight(int row, int col,int liquid,int particleDensity)
	{
		if(onTopOfGenerator(row,col))
		{
			return false;
		}
		if(col == NBR_COLS-1)
		{
			//Right Warp
			if(OBJECT_INFO[particleGrid[row+liquid][0]][3] < particleDensity && OBJECT_INFO[particleGrid[row][0]][3] < particleDensity
					&& OBJECT_INFO[particleGrid[row+liquid][0]][0] != 1 && OBJECT_INFO[particleGrid[row][0]][0] != 1)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * wrapLeft -- This will see if the particle can wrap left
	 * @param row -- This is the y cord on the grid
	 * @param col -- This is the x cord on the grid
	 * @param liquid -- This is an int value that tests to see how much the value needs to spread
	 * @param particleDensity -- This is the particles density value
	 * @return boolean -- Will return true if the particle can wrap left
	 */
	public static boolean wrapLeft(int row, int col,int liquid,int particleDensity)
	{
		if(onTopOfGenerator(row,col))
		{
			return false;
		}
		if(col == 0)
		{
			//Warp Left
			if(OBJECT_INFO[particleGrid[row+liquid][NBR_COLS-1]][3] < particleDensity && OBJECT_INFO[particleGrid[row][NBR_COLS-1]][3] < particleDensity
					&& OBJECT_INFO[particleGrid[row+liquid][NBR_COLS-1]][0] != 1 && OBJECT_INFO[particleGrid[row][NBR_COLS-1]][0] != 1)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * moveUpOrDown -- Test if the particle can move up or down
	 * @param row -- This is the y cord on the grid
	 * @param col -- This is the x cord on the grid
	 * @param particleDensity -- This is the particles density value
	 * @param gravity -- This is used to test which way the particle will move
	 * @return boolean -- Return true if the particle can move
	 */
	public static boolean moveUpOrDown(int row,int col,int particleDensity,boolean gravity)
	{
		if(gravity)
		{
			if(!(row >= (NBR_ROWS-1)))
			{
				//Object bellow you has a smaller density and the object bellow you is a solid
				if(OBJECT_INFO[particleGrid[row+1][col]][3] < particleDensity && OBJECT_INFO[particleGrid[row+1][col]][0] != 1)
				{
					//Move Down
					return true;
				}
			}
		}
		else
		{
			if(!(row < 1))
			{
				if(OBJECT_INFO[particleGrid[row-1][col]][3] < particleDensity && OBJECT_INFO[particleGrid[row-1][col]][0] != 1)
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * particalValidation -- This will return a boolean array of all the valid locations the particle can move to.
	 * @param row -- This is the y cord on the grid
	 * @param col -- This is the x cord on the grid
	 * @param liquid -- This is an int value that tests to see how much the value needs to spread
	 * @param gravity -- This is used to test which way the particle will move
	 * @return boolean[] -- This will return the valid locations the particle can move to
	 */
	public static boolean[] particalValidation(int row, int col,int liquid,boolean gravity)
	{
		boolean[] validMoveLocations = {false,false,false,false,false,false};

		int object = particleGrid[row][col];

		boolean objectWrap = false;

		//if the object wraps
		objectWrap = OBJECT_INFO[object][4] == 1;

		//Is the object able to move.
		boolean move = true;

		//Object density
		int particleDensity = OBJECT_INFO[object][3];

		//Object Info
		int[] particleInfo = OBJECT_INFO[object]; 

		//Particle can move, if not quit out
		if(particleInfo[0] != NON_MOVING_PARTICLE)
		{
			if(objectWrap)
			{
				validMoveLocations[DOWN_WARP] = wrapUpOrDown(row,col,particleDensity,gravity);
			}
			move = !(row >= (NBR_ROWS-1));

			if(!gravity)
			{
				move = !(row < 1);
			}

			if(move)
			{
				validMoveLocations[DOWN] = moveUpOrDown(row,col,particleDensity,gravity);
				validMoveLocations[LEFT_WARP] = wrapLeft(row,col,liquid,particleDensity);
				validMoveLocations[LEFT] = moveLeft(row,col,liquid,particleDensity);	
				validMoveLocations[RIGHT_WARP] = wrapRight(row,col,liquid,particleDensity);
				validMoveLocations[RIGHT] = moveRight(row,col,liquid,particleDensity);
				for(int i = LEFT; i < RIGHT_WARP; i++)
				{
					if(validMoveLocations[i])
					{
						validMoveLocations[i] = getRandomNumber(0,100) < OBJECT_INFO[object][2];
					}
				}
			}
		}
		return validMoveLocations;
	}

	/**
	 * particalMove -- This will pick a random place to move to.
	 * @param validMoveLocations -- These are the valid move locations
	 * @return int -- This will be the location the paricle can move to
	 */
	public int particalMove(boolean[] validMoveLocations)
	{
		int NumOfMoveLocations = 0;
		for(int i = 0; i <validMoveLocations.length; i++ )
		{
			if(validMoveLocations[i])
			{
				NumOfMoveLocations++;
			}
		}
		int[] newValidMove = new int[NumOfMoveLocations];

		int count = 0;

		for(int i = 0; i < validMoveLocations.length;i++)
		{
			if(validMoveLocations[i])
			{
				newValidMove[count] = i;
				count++;
			}

		}
		if(NumOfMoveLocations > 0)
		{
			return newValidMove[(getRandomNumber(0,count-1))];
		}
		return -1;
	}

	/** Step - called repeatedly, causes one random particle to maybe do something.*/
	public void step(){
		int randomRow = getRandomNumber(0,NBR_ROWS-1);
		int randomCol = getRandomNumber(0,NBR_COLS-1);

		ParticlePhysics.vapor(randomRow, randomCol, worldGravity);

		int object = particleGrid[randomRow][randomCol];

		int replaceObject;

		int liquid = OBJECT_INFO[object][0];

		int gravity = OBJECT_INFO[object][1];

		//This will invert the gravity based on the worldGravity
		if(!worldGravity)
		{
			gravity = gravity * INVERT_GRAVITY;
		}
		//This will invert the gravity based on the partials gravity.
		if(gravity == INVERT_GRAVITY)
		{
			liquid = liquid*INVERT_GRAVITY;
		}
		boolean localGravity = gravity == NORMAL_GRAVITY;

		//This will calculate which way to move and or to warp
		switch(particalMove(particalValidation(randomRow,randomCol,liquid,localGravity)))
		{
		//Left
		case LEFT:
			replaceObject = particleGrid[randomRow+liquid][randomCol-1];
			particleGrid[randomRow][randomCol] = replaceObject;
			particleGrid[randomRow+liquid][randomCol-1] = object;
			break;
			//Down
		case DOWN:
			replaceObject = particleGrid[randomRow+gravity][randomCol];
			particleGrid[randomRow][randomCol] = replaceObject;
			particleGrid[randomRow+gravity][randomCol] = object;
			break;

			//Right
		case RIGHT:
			replaceObject = particleGrid[randomRow+liquid][randomCol+1];
			particleGrid[randomRow][randomCol] = replaceObject;
			particleGrid[randomRow+liquid][randomCol+1] = object;
			break;

			//Warp Left
		case LEFT_WARP:
			replaceObject = particleGrid[randomRow+liquid][NBR_COLS-1];
			particleGrid[randomRow][randomCol] = replaceObject;
			particleGrid[randomRow+liquid][NBR_COLS-1] = object;
			break;
			//Warp Down
		case DOWN_WARP:
			if(localGravity)
			{
				replaceObject = particleGrid[0][randomCol];
				particleGrid[0][randomCol] = object;
			}
			else
			{
				replaceObject = particleGrid[NBR_ROWS-1][randomCol];
				particleGrid[NBR_ROWS-1][randomCol] = object;	
			}
			particleGrid[randomRow][randomCol] = replaceObject;
			break;
			//Warp Right
		case RIGHT_WARP:
			replaceObject = particleGrid[randomRow+liquid][0];
			particleGrid[randomRow][randomCol] = replaceObject;
			particleGrid[randomRow+liquid][0] = object;
			break;

		}
		ParticlePhysics.generator(randomRow, randomCol);
		ParticlePhysics.destroyer(randomRow, randomCol);
		ParticlePhysics.ice(randomRow, randomCol);
		ParticlePhysics.mud(randomRow, randomCol);
		ParticlePhysics.seed(randomRow, randomCol);
		ParticlePhysics.grass(randomRow, randomCol);
		ParticlePhysics.virus(randomRow, randomCol);
	}

	////////////////////////////////////////////////////
	//DO NOT modify anything below here!!! /////////////
	////////////////////////////////////////////////////

	public void run(){
		while (true){
			for (int i = 0; i < display.getSpeed(); i++){
				step();
			}
			updateDisplay();
			display.repaint();
			display.pause(1);  //wait for redrawing and for mouse   
			int[] mouseLoc = display.getMouseLocation();
			if (mouseLoc != null)  //test if mouse clicked
				locationClicked(mouseLoc[0], mouseLoc[1], display.getTool());
		}
	}

	public static int getRandomNumber (int low, int high){
		return (int)(Math.random() * ((high+1) - low)) + low;
	}

	public static int getNbrRows() {return NBR_ROWS;}
	public static int getNbrCols() {return NBR_COLS;}
	public static int getCellSize(){return CELL_SIZE;}
}