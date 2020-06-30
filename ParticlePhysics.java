package Final;
/**
 * @author Cole Talbot & Daniel O'Connell
 * @author This is a component of the Particle Lab program for the Talbot_OConnell team.
 * @version Particle Lab D5
 */

/*
 * This class holds all of the functions for special case particles. The particles here do more than
 * just move around the map. 
 */
public class ParticlePhysics {
	public static final int VAPORIZATIONRATE = 10;
	public static final int DUPLICATIONRATE = 100;
	private static final int GENORATORSPEED = 5;
	private static final int VIRUSSPREAD = 100;
	private static int SUN = 0;
	/**
	 * Calls the entry point for this program.
	 * 
	 * @param args Null: Not used in this program.
	 */
	public static void main(String[] args) {
		System.err.println("Started program from ParticlePhysics\n");
		ParticleLab.main(args);
	}

	/**
	 * This is the logic for the bubbles. If there is water all around an empty particle then it will color that particle white.
	 * 
	 * @param r The row number that is being checked.
	 * @param c The column number that is being checked.
	 */
	public static boolean bubbles(int r, int c) {
		int countAroundBubble = 0;

		if(ParticleLab.particleGrid[r][c] == ParticleLab.EMPTY
				&& c > 0 && c < ParticleLab.NBR_COLS - 1
				&& r > 0 && r < ParticleLab.NBR_ROWS - 1) {
			for (int particleRow = - 1; particleRow < 2; particleRow++) {
				for(int particleCol = - 1; particleCol <= 1; particleCol++) {
					if (ParticleLab.particleGrid[r + particleRow][c + particleCol] <= ParticleLab.OIL
							&& ParticleLab.particleGrid[r + particleRow][c + particleCol] > ParticleLab.MUD) {
						countAroundBubble = countAroundBubble + 1;
					}
				}
			}
			if (countAroundBubble >= 6) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This is the logic for the sun. This is what allows the sun to move down or up according to gravity.
	 * 
	 * @param r The row number that is being checked.
	 * @param c The column number that is being checked.
	 */
	public static boolean sun(int r, int c) {
		int topOfBoard = 10;
		int sunFactor  = 10;
		int gravity    = 1;
		int colPos     = 3;
		int resetToTop = 0;
		int resetToBottom = ParticleLab.NBR_ROWS*sunFactor - sunFactor;

		if(!ParticleLab.worldGravity) {
			gravity = gravity*-1;
		}

		if(c == ParticleLab.NBR_COLS/colPos && r == SUN/sunFactor) {
			ParticlePhysics.SUN = ParticlePhysics.SUN + gravity;
			//			System.out.println(SUN);
			if(ParticleLab.worldGravity) {
				if(SUN >= ParticleLab.NBR_ROWS*sunFactor - sunFactor) {
					SUN = resetToTop;
				}
			}
			else if(SUN <= topOfBoard) {
				SUN = resetToBottom;
			}
			return true;
		}
		return false;
	}

	/**
	 * This is the logic for the generators. It will reproduce what ever particle is resting on top of it.
	 * 
	 * @param r The row number that is being checked.
	 * @param c The column number that is being checked.
	 */
	public static void generator(int r, int c) {
		int generatorSpeed = ParticleLab.getRandomNumber(0, GENORATORSPEED);
		int gravity = 1;
		int positionTop = -(ParticleLab.NBR_ROWS-1);
		int positionBottom = 1;

		if(!ParticleLab.worldGravity) {
			gravity = -1;
			positionTop = -1;
			positionBottom = ParticleLab.NBR_ROWS-1;
		}

		// check to see if the speed is correct to place a new particle
		// check to make sure we are not at the edges (top or bottom)
		// check to see that the particle at the random location is a generator particle
		// check to see if the particle above the generator is something that it can generate (sand and greater)
		// check to see if the particle above the generator is not empty
		if(generatorSpeed == 0 && r > 0 && r < ParticleLab.NBR_ROWS-1
				&& ParticleLab.particleGrid[r][c] == ParticleLab.GENERATOR 
				&& ParticleLab.particleGrid[r - gravity][c] >= ParticleLab.SAND
				&& ParticleLab.particleGrid[r - gravity][c] != ParticleLab.EMPTY) {
			//			System.out.println("inside generator");
			int object = ParticleLab.particleGrid[r - gravity][c];

			if (ParticleLab.particleGrid[r + gravity][c] > object) {
				ParticleLab.particleGrid[r + gravity][c] = object;
			}
		}
		if(generatorSpeed == 0 && r <= 0 
				&& ParticleLab.particleGrid[r][c] == ParticleLab.GENERATOR 
				&& ParticleLab.particleGrid[r - positionTop][c] >= ParticleLab.SAND
				&& ParticleLab.particleGrid[r - positionTop][c] != ParticleLab.EMPTY) {
			int object = ParticleLab.particleGrid[r - positionTop][c];
			if(ParticleLab.worldGravity) {
				ParticleLab.particleGrid[r + gravity][c] = object;
			}
			else {
				ParticleLab.particleGrid[ParticleLab.NBR_ROWS-1][c] = object;
			}

		}
		if(generatorSpeed == 0 && r >= ParticleLab.NBR_ROWS-1 
				&& ParticleLab.particleGrid[r][c] == ParticleLab.GENERATOR 
				&& ParticleLab.particleGrid[r - positionBottom][c] >= ParticleLab.SAND
				&& ParticleLab.particleGrid[r - positionBottom][c] != ParticleLab.EMPTY) {
			int object = ParticleLab.particleGrid[r - positionBottom][c];
			if(ParticleLab.worldGravity) {
				ParticleLab.particleGrid[0][c] = object;
			}
			else {
				ParticleLab.particleGrid[r-1][c] = object;
			}

		}

	}

	/**
	 * This is the destroyer logic. It will change any particle that is next to it into vapor.
	 * @param r The row number that is being checked.
	 * @param c The column number that is being checked.
	 */
	public static void destroyer(int r, int c) {
		int rStart = r;
		int cStart = c;
		int start = -1;
		int distanceFromCenter = 2;
		// all in one.
		if(ParticleLab.particleGrid[r][c] == ParticleLab.DESTROYER) {
			for(int i = start; i < distanceFromCenter; i++) {
				for(int b = start; b < distanceFromCenter; b++) {
					//top of the board
					if(r <= 0 && i > start) {
						r = rStart;
					}
					if(r <= 0 && i == start) {
						b = 0;
						r = ParticleLab.NBR_ROWS - 1;
					}
					//bottom of the board
					if(r >= ParticleLab.NBR_ROWS - 1 && i == 1) {
						r = -1;
					}
					//left of the board
					if(c <= 0 && b > start) {
						c = cStart;
					}
					if(c <= 0 && b == start) {
						b = 0;
						c = ParticleLab.NBR_COLS - 1;
					}
					//right of the board
					if(c >= ParticleLab.NBR_COLS - 1 && b == 1) {
						c = -1;
					}
					if(ParticleLab.particleGrid[r + i][c + b] > ParticleLab.METAL && ParticleLab.particleGrid[r + i][c + b] != ParticleLab.EMPTY) {
						ParticleLab.particleGrid[r + i][c + b] = ParticleLab.VAPOR;
					}
				}
			}
		}
	}

	/**
	 * This is the logic for vapor. The vapor will either duplicate or vaporize based on a random chance.
	 * @param r The row number that is being checked.
	 * @param c The column number that is being checked.
	 */
	public static void vapor(int r, int c, boolean worldGravity) 
	{
		int rStart = r;
		if(ParticleLab.particleGrid[r][c] == ParticleLab.VAPOR) 
		{
			int vaporize = ParticleLab.getRandomNumber(0,VAPORIZATIONRATE);
			int duplicate = ParticleLab.getRandomNumber(0,DUPLICATIONRATE);
			if(vaporize == 0) 
			{
				ParticleLab.particleGrid[r][c] = ParticleLab.EMPTY;
			}
			if(c - 1 > 0 && ParticleLab.particleGrid[r][c - 1] == ParticleLab.EMPTY && duplicate == 0) 
			{
				ParticleLab.particleGrid[r][c - 1] = ParticleLab.VAPOR;
			}
			if(c + 1 < ParticleLab.NBR_COLS && ParticleLab.particleGrid[r][c + 1] == ParticleLab.EMPTY && duplicate == 1) 
			{
				ParticleLab.particleGrid[r][c + 1] = ParticleLab.VAPOR;
			}

			int direction = -1;
			if(!worldGravity) {
				direction = 1;
			}
			if(r == 0 && worldGravity) {
				r = ParticleLab.NBR_ROWS;
			}
			if(r == ParticleLab.NBR_ROWS - 1 && !worldGravity) 
			{
				r = -1;

			}
			if(ParticleLab.particleGrid[r + direction][c] < ParticleLab.VAPOR) {
				ParticleLab.particleGrid[rStart][c] = ParticleLab.EMPTY;
			}
		}
	}
	/**
	 * This is the logic for ice. It will freeze ice next to itself or below itself as long as the ice isn't too thick.
	 * @param r The row number that is being checked.
	 * @param c The column number that is being checked.
	 */
	public static void ice(int r, int c) {
		if(ParticleLab.particleGrid[r][c] == ParticleLab.ICE) {
			// used to restart the rows and columns to their original numbers
			// used to describe how far out the ice can reach.
			int gravity = 1;
			if(!ParticleLab.worldGravity) {
				gravity = -1;
			}
			// Decides how fast the ice will spread
			int freezeSpeed = ParticleLab.getRandomNumber(0, 50);
			//current leveling Ice sheet
			//left of the board and change to the right of the board
			if(r > 0 && r < ParticleLab.NBR_ROWS - 1 && ParticleLab.particleGrid[r-gravity][c] == ParticleLab.WATER) {
				ParticleLab.particleGrid[r-gravity][c] = ParticleLab.ICE;
				ParticleLab.particleGrid[r][c] = ParticleLab.WATER;
			}
			if(ParticleLab.worldGravity && r == ParticleLab.NBR_ROWS - 1 && ParticleLab.particleGrid[r-gravity][c] == ParticleLab.WATER) {
				ParticleLab.particleGrid[r-gravity][c] = ParticleLab.ICE;
				ParticleLab.particleGrid[r][c] = ParticleLab.WATER;
			}
			if(!ParticleLab.worldGravity && r == 0 && ParticleLab.particleGrid[r-gravity][c] == ParticleLab.WATER) {
				ParticleLab.particleGrid[r-gravity][c] = ParticleLab.ICE;
				ParticleLab.particleGrid[r][c] = ParticleLab.WATER;
			}
			if(freezeSpeed == 0) {
				if(r > 0 && r < ParticleLab.NBR_ROWS - 1 && ParticleLab.particleGrid[r+gravity][c] == ParticleLab.WATER 
						&& ParticleLab.particleGrid[r-gravity][c] != ParticleLab.ICE 
						&& ParticleLab.particleGrid[r-gravity][c] != ParticleLab.WATER) {
					ParticleLab.particleGrid[r+gravity][c] = ParticleLab.ICE;
				}
				else if(r == 0 && ParticleLab.particleGrid[ParticleLab.NBR_ROWS - 1][c] == ParticleLab.WATER ) {
					ParticleLab.particleGrid[ParticleLab.NBR_ROWS - 1][c] = ParticleLab.ICE;  
				}
				else if(!ParticleLab.worldGravity && r == ParticleLab.NBR_ROWS - 1 && ParticleLab.particleGrid[0][c] == ParticleLab.WATER ) {
					ParticleLab.particleGrid[0][c] = ParticleLab.ICE;  
				}
			}

			if(freezeSpeed == 1) {
				if(c >= ParticleLab.NBR_COLS -1 )
				{
					if(ParticleLab.particleGrid[r][0] == ParticleLab.WATER )
					{
						ParticleLab.particleGrid[r][0] = ParticleLab.ICE;
					}
				}
				else if(ParticleLab.particleGrid[r][c+1] == ParticleLab.WATER ) {
					ParticleLab.particleGrid[r][c+1] = ParticleLab.ICE;
				}
			}
			if(freezeSpeed == 2) 
			{
				if(c <= 0)
				{
					if(ParticleLab.particleGrid[r][ParticleLab.NBR_COLS -1] == ParticleLab.WATER ) 
					{
						ParticleLab.particleGrid[r][ParticleLab.NBR_COLS -1] = ParticleLab.ICE;
					}
				}else if(ParticleLab.particleGrid[r][c-1] == ParticleLab.WATER ) {
					ParticleLab.particleGrid[r][c-1] = ParticleLab.ICE;
				}
			}
		}
	}
	/**
	 * searchArea -- This will search the area and return the if the object being looked for is there
	 * @param row -- This is the relevant row
	 * @param col -- This is the relevant col
	 * @param range -- This is the range of search
	 * @param object -- This is the partial being looked for
	 * @return boolean [][] -- This is a 2D array that has all of the valid search locations
	 */
	public static boolean[][] searchArea(int row,int col, int range, int object)
	{
		if(range%2 == 0)
		{
			range++;
		}
		boolean[][] searchArea = new boolean [range][range];
		for(int r = 0; r < range; r++)
		{
			for(int c = 0 ; c < range; c++)
			{
				
				if((row + (r-range/2) > ParticleLab.NBR_ROWS - 1) ||(col + (c-range/2) > ParticleLab.NBR_COLS - 1 )||(col + (c-range/2) < 0 )||(row + (r-range/2) < 0))
				{
					searchArea[r][c] = false;
				}
				else
				{
					if(ParticleLab.particleGrid[row + (r-range/2)][col + (c-range/2)] == object)
					{
						searchArea[r][c] = true;
					}
				}
			}
		}
		return searchArea;
	}

	/**
	 * grass -- This will test if water or mud is near by and will grow to a set height
	 * @param r The row number that is being checked.
	 * @param c The column number that is being checked.
	 * @return 1 If water is absorbed. 0 - If water is not absorbed.
	 */
	public static int grass(int r, int c) {
		int growRate = 1;
		int distanceFromCenter = 10;
		if(distanceFromCenter%2 == 0)
		{
			distanceFromCenter++;
		}
		if(ParticleLab.particleGrid[r][c] == ParticleLab.GRASS) {
			if(r == 0 || r == ParticleLab.NBR_ROWS - 1)
			{
				ParticleLab.particleGrid[r][c] = ParticleLab.EMPTY;
				return 0;
			}
			boolean[][] searchAreaMud = searchArea(r,c,distanceFromCenter,ParticleLab.MUD);
			boolean[][] searchAreaWater = searchArea(r,c,distanceFromCenter,ParticleLab.WATER);
			int move = 1;
			if(!ParticleLab.worldGravity)
			{
				move = -1;
			}
			for(int row = 0; row < distanceFromCenter; row++)
			{
				for(int col = 0; col < distanceFromCenter; col++)
				{
					if(searchAreaMud[row][col] || searchAreaWater[row][col])
					{
						if(ParticleLab.getRandomNumber(0,200) >= growRate)
						{
							return 0;
						}
						int newRow = r + (row-distanceFromCenter/2);
						int newCol = c + (col-distanceFromCenter/2);
						//Take Water or Mud
						if(r >= 0 && r <= ParticleLab.NBR_ROWS - 1)
						{
							//Valid Grow up
							if(ParticleLab.particleGrid[r+move][c] == ParticleLab.EMPTY)
							{
								ParticleLab.particleGrid[r][c] = ParticleLab.EMPTY;
								return 0;
							}
							if(r+5 <= ParticleLab.NBR_ROWS-1 && r-5 >= 0)
							{
								if(ParticleLab.particleGrid[r+move*5][c] == ParticleLab.GRASS)
								{
									return 0;
								}
							}
							if((ParticleLab.particleGrid[r-move][c] == ParticleLab.EMPTY || ParticleLab.particleGrid[r-move][c] == ParticleLab.WATER)&& (ParticleLab.particleGrid[r+move][c] == ParticleLab.GRASS || ParticleLab.particleGrid[r+move][c] == ParticleLab.MUD))
							{
								if(ParticleLab.particleGrid[newRow][newCol] == ParticleLab.MUD)
								{
									ParticleLab.particleGrid[newRow][newCol] = ParticleLab.DIRT;
								}
								else
								{
									ParticleLab.particleGrid[newRow][newCol] = ParticleLab.EMPTY;
								}
								ParticleLab.particleGrid[r-move][c] = ParticleLab.GRASS;
							}

						}
						return 0;
					}
				}
				if(r >  0 && r < ParticleLab.NBR_ROWS - 1) 
				{
					if(ParticleLab.particleGrid[r+move][c] == ParticleLab.EMPTY)
					{
						ParticleLab.particleGrid[r][c] = ParticleLab.EMPTY;
						return 0;
					}
				}
			}
		}
		return 0;
	}

	/**
	 * This is the logic for seeds in the world. If water lands near the seed will absorb that water and transform into mud.
	 * @param r The row number that is being checked.
	 * @param c The column number that is being checked.
	 * @return 1 If water is absorbed. 0 - If water is not absorbed.
	 */
	public static int seed(int r, int c) {
		int decayRate = 1;
		int distanceFromCenter = 3;
		int count = 0;
		int requiredMud = 2;
		if(distanceFromCenter%2 == 0)
		{
			distanceFromCenter++;
		}
		if(ParticleLab.particleGrid[r][c] == ParticleLab.SEED) {
			boolean[][] searchArea = searchArea(r,c,distanceFromCenter,ParticleLab.MUD);
			for(int row = 0; row < distanceFromCenter; row++)
			{
				for(int col = 0; col < distanceFromCenter; col++)
				{
					if(searchArea[row][col])
					{
						count++;
					}
					if(count >= requiredMud)
					{
						int newRow = r + (row-distanceFromCenter/2);
						int newCol = c + (col-distanceFromCenter/2);
						ParticleLab.particleGrid[newRow][newCol] = ParticleLab.DIRT;
						ParticleLab.particleGrid[r][c] = ParticleLab.GRASS;
						return 0;
					}
				}
			}
			if(ParticleLab.getRandomNumber(0,200) <= decayRate)
			{
				ParticleLab.particleGrid[r][c] = ParticleLab.EMPTY;
			}
		}
		return 0;
	}

	/**
	 * This is the logic to create mud in the world. If water lands near sand the sand will absorb that water and transform into mud.
	 * @param r The row number that is being checked.
	 * @param c The column number that is being checked.
	 * @return 1 If water is absorbed. 0 - If water is not absorbed.
	 */
	public static int mud(int r, int c) {
		int distanceFromCenter = 6;
		if(distanceFromCenter%2 == 0)
		{
			distanceFromCenter++;
		}
		if(ParticleLab.particleGrid[r][c] == ParticleLab.DIRT) {
			boolean[][] searchArea = searchArea(r,c,distanceFromCenter,ParticleLab.WATER);
			for(int row = 0; row < distanceFromCenter; row++)
			{
				for(int col = 0; col < distanceFromCenter; col++)
				{
					if(searchArea[row][col])
					{
						int newRow = r + (row-distanceFromCenter/2);
						int newCol = c + (col-distanceFromCenter/2);
						ParticleLab.particleGrid[newRow][newCol] = ParticleLab.EMPTY;
						ParticleLab.particleGrid[r][c] = ParticleLab.MUD;
						return 0;
					}
				}
			}
		}
		return 0;
	}

	public static void randomizeTheBoard(int[][]  particleGrid) {
		int random = ParticleLab.getRandomNumber(0, 2);
		if(random == 0) {
			System.out.println("Random 1");
			for (int r = 0; r < particleGrid.length; r++) {
				for (int c = 0; c < particleGrid[1].length; c++) {
					particleGrid[r][c] = ParticleLab.getRandomNumber(1, ParticleLab.TOTALNBROFPARTICLES);
				}
			}
		}
		if(random == 1) {
			System.out.println("Random 2");
			for (int r = 0; r < particleGrid.length; r++) {
				for (int c = 0; c < particleGrid[1].length; c++) {
					int rnd = ParticleLab.getRandomNumber(0, 1);
					if(rnd == 0) {
						particleGrid[r][c] = ParticleLab.getRandomNumber(1, ParticleLab.TOTALNBROFPARTICLES);
					}
					if(rnd == 1) {
						particleGrid[r][c] = ParticleLab.EMPTY;
					}
				}
			}
		}
		if(random == 2) {
			System.out.println("Random 3");
			for (int r = 0; r < particleGrid.length; r++) {
				for (int c = 0; c < particleGrid[1].length; c++) {
					int rnd = ParticleLab.getRandomNumber(0, 3);
					if(rnd == 0) {
						particleGrid[r][c] = ParticleLab.WATER;
					}
					if(rnd == 1) {
						particleGrid[r][c] = ParticleLab.EMPTY;
					}
					if(rnd == 2) {
						particleGrid[r][c] = ParticleLab.SAND;
					}
					if(rnd == 3) {
						particleGrid[r][c] = ParticleLab.METAL;
					}
				}
			}
		}	
	}
	public static void virus(int r, int c) {
		if(ParticleLab.particleGrid[r][c] == ParticleLab.VIRUS) {
			int randomSpread = ParticleLab.getRandomNumber(0, VIRUSSPREAD);
			if(randomSpread == 0) {
				int randomRow = ParticleLab.getRandomNumber(0, ParticleLab.NBR_ROWS - 1);
				int randomCol = ParticleLab.getRandomNumber(0, ParticleLab.NBR_COLS - 1);
				ParticleLab.particleGrid[randomRow][randomCol] = ParticleLab.VIRUS;
			}
		}
	}
}


