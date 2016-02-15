import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;
import bwta.Polygon;
import java.util.ArrayList;
import java.util.List;

public class TestBot1 extends DefaultBWListener {
    private Mirror mirror = new Mirror();
    private Game game;
    private Player self;
    //need worker supply & army supply
    //on scout get unit composition

    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }
    @Override
    public void onUnitCreate(Unit unit) {
        //System.out.println("New unit " + unit.getType());
    	if (unit.getType() == UnitType.Terran_SCV){
        	counter.scvnum +=1;

        }
    	if (unit.getType() == UnitType.Terran_Marine){
        	counter.marinenum +=1;
        }
    }//will these incrememnt for enemy players units aswell if they are terran???
    @Override
    public void onUnitDestroy(Unit unit) {
        //System.out.println("New unit " + unit.getType());
    	if (unit.getType() == UnitType.Terran_SCV){
        	counter.scvnum -=1;
        }
    	if (unit.getType() == UnitType.Terran_Marine){
        	counter.marinenum -=1;
        }
    }
    public class Orders {
    	
    	public Orders(String ordername, UnitType type){
    		System.out.println("Order Name: " + ordername + "Building Name: " + type);
    		building = type;
    		name = ordername;
    		//System.out.println();
    		
    		if(ordername == "supply"){
    			minerals = 100;
    		}
    		if(ordername == "barracks"){
    			minerals = 150;
    		}
    		if(ordername == "refinery"){
    			minerals = 150;
    		}
    		System.out.println("Type" + type + "Minerals" + minerals);
    	}

    	String name;
    	//int priorety;
    	//int number;
    	//int unitid;
    	Unit unit;
    	UnitType building;
    	int wait = 0; 
    	int started = 0;
    	int minerals;
    	int gas;
    	int finished = 0;
    }
    public class Commander{
    	
    	public void giveorder(List<Orders> orderlist){
    		
    		for(Orders order : orderlist){
    			
    			Unit scv = null;
    			for (Unit myUnit : self.getUnits()) {
    	          
    	            if (myUnit.getType().isWorker() && myUnit.isGatheringMinerals()) {
    	                scv = myUnit;
    	                break;
    	            }
    			}
    			//if the order is finished, instead of removing it which seems
    			//to not work, I'll just break when a flag for finished is raised
    			if(order.finished == 1 && order.unit.isGatheringMinerals()){
    				System.out.println("skip finished!");
    				continue;
    			}
    				
    			//first it checks if that order is being construced and if so it sets wait to zero
    			if(order.started == 1 && order.unit.isConstructing()){
    				order.wait = 0;
    				System.out.println("Wait == 0");
    				order.finished = 1;
    				break;
    				//continue;	
    			}		

    			if(order.wait == 1){
    				System.out.println("Wait == 1");
    				
    				break;
    				}
    			if(self.minerals()< order.minerals && order.started == 0){
    				//check if we have enough minerals to build and that we haven't alread
    				//started to build, break if not
    				System.out.println("No Minerals");
    				break;	
    			}

    			if(order.started == 0){
    				System.out.println("Started == 0");
    				//will set started to 1 as soon as it is looking for a place to build?
    				//will set wait to 0 once it starts constructing
    				order.started = 1;//set started to 1
    				order.wait = 1; //also set wait to 1 which lasts until it isConstructing
    				order.unit = scv;//set the unit to the scv, wont be changed
    				System.out.println("order.building: " + order.building);
    				//start the building
        			TilePosition buildTile = //gets the building type from order.type
            			getBuildTile(order.unit, order.building, self.getStartLocation());
        			
        			System.out.println("BuildTile: " + buildTile);
        			System.out.println("BuildTile: " + buildTile);
            			//and, if found, send the worker to build it (and leave others alone - break;)
            		if (buildTile != null) {
            			order.unit.build(buildTile, order.building);
            			}
    				}
    		}
    		
    	}
    }
    
 
    
    public class General{
    	//setup a base/expo
    	public void callcommander(){
    		//from list of commanders? and/or build orders
    		//and build positions
    	}
    	//set attackcommander and force
    }	//defensive buildings build positions
    public class Commander2{
    	int num = 0;
    	Unit scv = null;
    	Orders order = null;
    	List<Orders> orderlist;
    	
    	//constructor
    	public Commander2(List<Orders> list){
    		orderlist = list;
    	}
    	
    	//I need to make a function to get build orders strategies
    	//meaning orders for initial build and then orders or a tree 
    	//of orders to go through based on new needs
    	
    	
    	public void getscv(){
    		if(scv == null){
	    		for (Unit myUnit : self.getUnits()) {
		           
	    			//should always pick a free scv
	    			//and only if scv is null
		            if (myUnit.getType() == UnitType.Terran_SCV && myUnit.isGatheringMinerals() 
		            		&& counter.scout != null && counter.scout.getID() != myUnit.getID()){
		            	scv = myUnit;
		            	System.out.println("SCV: " + scv);
		            	break;
		            }
	    		}
    		}
    	}
    	
    	public void getorder(){
    		
    		if(orderlist.get(num).finished == 1 && num < orderlist.size()){
    			order = null;
    			num+=1;
    			System.out.println("Num: " + num);
    		}
    		
    		if(orderlist.get(num).started == 0 && orderlist.get(num).minerals < self.minerals()){
    			order = orderlist.get(num);
    			order.unit = scv;
    		}

    		
    		//System.out.println("Order: " + order);
    	}
    	
    	public void checkorderprogress(){
    		//System.out.println("Checking Order Progress");
    		//the unit null checks are because of a trouble shooting issue
    		//I could probably fix it better by incrementing the num variable on it's own
    		//before the other functions
    		if(order != null && order.unit != null){
	    		if(order.unit.getOrder() == Order.ConstructingBuilding){
	    			order.finished = 1;
	    			//so once the scv is building in the position, another scv can start
	    			scv = null;
	    			//getscv();
	    			//must get new scv or else the next give order
	    			//wont work
	    			//must set to null or else getscv wont work
	    			//System.out.println("Finished");
	    		}	
	    		//if the order failed
	    		else if (order.unit != null && order.unit.isGatheringMinerals())
	    		{
	    			order.started = 0;
	    		}
    		}
    		//System.out.println("Finished checking...");
    	}
    	
    	public void giveorder(){
    		//System.out.println("Gave Order");
    		if(order != null && order.started == 0){
	    		order.unit = scv;
				order.started = 1;//set started to 1
				//System.out.println("order.building: " + order.building);
				System.out.println("Gave Order");
	
				TilePosition buildTile = 
	    			getBuildTile(order.unit, order.building, self.getStartLocation());
				System.out.println("buildtile done");
	
	    		if (buildTile != null) {
	    			order.unit.build(buildTile, order.building);  
	    			System.out.println("found solid location");
	    		}	
    		}
    	}
    	
    	public void doallofthethings(){
    		
    		if(num < orderlist.size()){
	    		getorder();
	    		//System.out.println("Doing all of the things1");
	    		getscv();
	    		//System.out.println("Doing all of the things2");
	    		checkorderprogress();
	    		//System.out.println("Doing all of the things3");
	    		giveorder();
	    		//System.out.println("Doing all of the things4");
    		}
    		else{
    			
    			System.out.println("num" + num);
    			System.out.println("order list" + orderlist);
    			System.out.println("order" + order); //has garbage at 15?
    			//System.out.println("order.unit" + order.unit); //goes to null
    			
    		}
    	}
    }
    public class AttackCommander{
    	
    	//general sends troops? (scout, marines, etc)
    	//move to attack position
    	//attack
    	//check isattackframe
    	//retreat to base
    	//retreat to back of lines
    	//retreat to setpoint
    }
    public class Counter{
    	
    	public Counter(int mineralsavailable, int mineralsinuse){
    		
    	}
    	
    	int mineralsavailable;
    	int mineralsinuse;
    	int scvnum;
    	int marinenum;
    	int attack = 0;
    	Unit scout;
		public int scouted = 0;
		public int test;
		public int attacked;
    }
    
	Counter counter = new Counter(0,0);
    public List<Orders> orderslist = new ArrayList<Orders>();
    public List<Orders> buildorderA = new ArrayList<Orders>();
    public List<Orders> buildorderB = new ArrayList<Orders>();
    Commander commander = new Commander();
    Commander2 commander2 = new Commander2(buildorderA);
    Commander2 commander3 = new Commander2(buildorderB);
    
    public BaseLocation mystartbase;
    public BaseLocation expansion;
    
    public Position enemystartbase;
    public TilePosition esb;
    
    public List<TilePosition> bunkerarea = new ArrayList<TilePosition>();
    public List<TilePosition> buildable = new ArrayList<TilePosition>();
    public List<TilePosition> marineradius = new ArrayList<TilePosition>();
    
    public TilePosition bestbunker = null;
    public Chokepoint choke;
    public TilePosition choketile;
    
    public List<Polygon> unwalk = new ArrayList<Polygon>();
    public List<Region> regions = new ArrayList<Region>();
    
    public List<Unit> inradiuslist = new ArrayList<Unit>();
    public Unit enemymainunit;
    public Unit mymainunit;
    
    /*private void checkfordupes(Region r, List<Region> l){
    	int dupes = 0;
    	for(Region reg: l){
    		if(reg == r){
    			dupes++;
    			break;
    		}
    	}
    	if(dupes == 0 && r != null){
    		l.add(r);
    	}
    }*/
    /*public void scanmap(){
    	for(int m = 1; m<game.mapHeight(); m++){
    		for(int n = 1; n<game.mapWidth(); n++){
    			Region checkregion = game.getRegionAt(new Position(m*32,n*32));
    			System.out.println(""+checkregion + n + m);
    			checkfordupes(checkregion, regions);
    		}
    	}
    }*/
    
    
    public void bunkertactics(){
    	//choketile = counter.scout.getTilePosition();
    	for (Unit myUnit : self.getUnits())
	    	if (myUnit.getType() == UnitType.Terran_SCV){
	        		counter.scout = myUnit;
	        		break;
	        	}
    	
        for(int n = -7; n < 8; n++){
        	for (int m = 7; m >-8; m--){
        		TilePosition nct = new TilePosition(choketile.getX()+n,choketile.getY()+m);
        		bunkerarea.add(nct);
        	}
        	
        }
		//counter.scout.move(mystartbase.getPosition());
		//counter.test = 1;
		
		for (TilePosition bp : bunkerarea) {
        	
			if(bp.getDistance(choketile)<=WeaponType.Gauss_Rifle.maxRange()/32*2){
				marineradius.add(bp);

        		if(game.canBuildHere(counter.scout, bp, UnitType.Terran_Bunker) == true){
        			buildable.add(bp);
	        		if(bestbunker==null){
		        		bestbunker=bp;
		        		
	        		}
	        		else if(BWTA.getGroundDistance(bp, esb) > BWTA.getGroundDistance(bestbunker, esb)){
	        			bestbunker=bp;
	        			
	        			}	
        			}
        	}	
		}
		counter.scout = null;
    }

    @Override
    public void onStart() {
        game = mirror.getGame();
        self = game.self();
        game.setLocalSpeed(20);
        //I could do this dynamically by instantiating one order at a time
        //to commander2's order variable in it's getorder() function
        //I can still use num and say if(num == 1, 4, 6 etc) {
        // order = new Orders("supply", UnitType.Terran_Supply_Depot); }
        //also I can simply number each building a different number
        //and change that order based on need
        
        //A way to simplify things would be to just make 5 or 6 of
        //each unit training building type, 1 of each auxilary type
        //and make supply as needed or a bit more than needed.
        Orders supply = new Orders("supply", UnitType.Terran_Supply_Depot);
        Orders supply2 = new Orders("supply", UnitType.Terran_Supply_Depot);
        Orders supply3 = new Orders("supply", UnitType.Terran_Supply_Depot);
        Orders supply4 = new Orders("supply", UnitType.Terran_Supply_Depot);
        Orders supply5 = new Orders("supply", UnitType.Terran_Supply_Depot);
        Orders supply6 = new Orders("supply", UnitType.Terran_Supply_Depot);
        Orders supply7 = new Orders("supply", UnitType.Terran_Supply_Depot);
        Orders supply8 = new Orders("supply", UnitType.Terran_Supply_Depot);
        
        Orders barracks = new Orders("barracks", UnitType.Terran_Barracks);
        Orders barracks2 = new Orders("barracks", UnitType.Terran_Barracks);
        Orders barracks3 = new Orders("barracks", UnitType.Terran_Barracks);
        Orders barracks4 = new Orders("barracks", UnitType.Terran_Barracks);
        Orders barracks5 = new Orders("barracks", UnitType.Terran_Barracks);
        Orders barracks6 = new Orders("barracks", UnitType.Terran_Barracks);

        Orders refinery = new Orders("refinery", UnitType.Terran_Refinery);
        Orders refinery2 = new Orders("refinery", UnitType.Terran_Refinery);

        //enter build order items without duplicates since that didn't work
        buildorderA.add(supply);
        buildorderA.add(barracks);
        buildorderA.add(barracks2);
        buildorderA.add(supply2);
        buildorderA.add(barracks3);
        buildorderA.add(barracks4);
        buildorderA.add(supply3);
        buildorderA.add(barracks5);
        buildorderA.add(barracks6);
        buildorderA.add(supply4);
        buildorderA.add(refinery);
        buildorderA.add(supply5);
        buildorderA.add(supply6);
        buildorderA.add(supply7);
        buildorderA.add(supply8);

        //Use BWTA to analyze map
        //This may take a few minutes if the map is processed first time!
        System.out.println("Analyzing map...");
        BWTA.readMap();
        BWTA.analyze();
        System.out.println("Map data ready");
        
        mystartbase = BWTA.getStartLocation(self);
        choke = BWTA.getNearestChokepoint(mystartbase.getPosition());
        choketile = new TilePosition(choke.getCenter().getX()/32,choke.getCenter().getY()/32);
        //unwalk = BWTA.getUnwalkablePolygons();
        //scanmap();
        //regions.add(game.getRegionAt(new Position(0,0)));
        for(Unit unit: self.getUnits())
        {
        	if(unit.getType() == UnitType.Terran_Command_Center)
        	{
        		mymainunit = unit;
        		break;
        	}
        }

        for (BaseLocation b : BWTA.getBaseLocations()) {
        	// If this is a possible start location,
        	if (b.isStartLocation()) {
        		if(game.isVisible(b.getTilePosition()) == false){
        			enemystartbase = b.getPosition();
        			esb = b.getTilePosition();
        			break;
        		}
        	}
        }
        bunkertactics();//after the above loop since it uses esb
        System.out.println("Enemy Base Location: " + enemystartbase.getX() + enemystartbase.getY() );
    }

	 public TilePosition getBuildTile(Unit builder, UnitType buildingType, TilePosition aroundTile) {
	 	TilePosition ret = null;
	 	int maxDist = 3;
	 	int stopDist = 40;
	 	
	 	// Refinery, Assimilator, Extractor
	 	if (buildingType.isRefinery()) {
	 		for (Unit n : game.neutral().getUnits()) {
	 			if ((n.getType() == UnitType.Resource_Vespene_Geyser) && 
	 					( Math.abs(n.getTilePosition().getX() - aroundTile.getX()) < stopDist ) &&
	 					( Math.abs(n.getTilePosition().getY() - aroundTile.getY()) < stopDist )
	 					) return n.getTilePosition();
	 		}
	 	}
	 	
	 	while ((maxDist < stopDist) && (ret == null)) {
	 		for (int i=aroundTile.getX()-maxDist; i<=aroundTile.getX()+maxDist; i++) {
	 			for (int j=aroundTile.getY()-maxDist; j<=aroundTile.getY()+maxDist; j++) {
	 				if (game.canBuildHere(builder, new TilePosition(i,j), buildingType, false)) {
	 					// units that are blocking the tile
	 					boolean unitsInWay = false;
	 					for (Unit u : game.getAllUnits()) {
	 						if (u.getID() == builder.getID()) continue;
	 						if ((Math.abs(u.getTilePosition().getX()-i) < 4) && (Math.abs(u.getTilePosition().getY()-j) < 4)) unitsInWay = true;
	 					}
	 					if (!unitsInWay) {
	 						return new TilePosition(i, j);
	 					}
	 					// creep for Zerg
	 					if (buildingType.requiresCreep()) {
	 						boolean creepMissing = false;
	 						for (int k=i; k<=i+buildingType.tileWidth(); k++) {
	 							for (int l=j; l<=j+buildingType.tileHeight(); l++) {
	 								if (!game.hasCreep(k, l)) creepMissing = true;
	 								break;
	 							}
	 						}
	 						if (creepMissing) continue; 
	 					}
	 				}
	 			}
	 		}
	 		maxDist += 2;
	 	}
	 	
	 	if (ret == null) game.printf("Unable to find suitable build position for "+buildingType.toString());
	 	return ret;
	 }
 
    @Override
    public void onFrame() {
        game.setTextSize(10);
        game.drawTextScreen(10, 10, "Playing as " + self.getName() + " - " + self.getRace());
        //System.out.println("looped again");
        Color red = new Color(255,0,0);
        Color yellow = new Color(255,255,0);
        Color blue = new Color(0,0,255);
        Color green = new Color(0,255,0); 
        Color craycray = new Color(45,255,100); 
        game.drawBox(2, enemystartbase.getX(), enemystartbase.getY(), enemystartbase.getX()+20, enemystartbase.getY()+20, red);
        
        
        
        for (TilePosition bp : bunkerarea) {
			game.drawBox(2, bp.getX()*32, bp.getY()*32, bp.getX()*32+32, bp.getY()*32+32, green);
        }
        for (TilePosition bp : marineradius) {
			game.drawBox(2, bp.getX()*32, bp.getY()*32, bp.getX()*32+32, bp.getY()*32+32, yellow);
        }
        for (TilePosition bp : buildable) {
			game.drawBox(2, bp.getX()*32, bp.getY()*32, bp.getX()*32+32, bp.getY()*32+32, blue);
        }
        if(bestbunker!=null){
        	game.drawBox(2, bestbunker.getX()*32, bestbunker.getY()*32, 
				bestbunker.getX()*32+32, bestbunker.getY()*32+32, red);
        }
        //System.out.println("right?");
       /* if(counter.test == 1){//only happens after scout goes and gets a tileposition
        	//System.out.println("right? 2" + bunkerarea);
	        for (TilePosition bp : bunkerarea) {
	        	
	        	//bp.makeValid();
	        	//if(game.canBuildHere(counter.scout, bp, UnitType.Terran_Bunker) == true){
	        	if(bp.getDistance(choketile)<=WeaponType.Gauss_Rifle.maxRange()/32*2){
	        		game.drawBox(2, bp.getX()*32, bp.getY()*32, bp.getX()*32+32, bp.getY()*32+32, yellow);
	        		//if can build here
	        		//get walking distance
	        		//if list is null add item
	        		//if not null if item>this item, remove item and add this
	        		//System.out.println("first");
	        		if(game.canBuildHere(counter.scout, bp, UnitType.Terran_Bunker) == true){
	        			game.drawBox(2, bp.getX()*32, bp.getY()*32, bp.getX()*32+32, bp.getY()*32+32, craycray);
	        			//double dist = BWTA.getGroundDistance(bp, choketile);
	        		
		        		if(bestbunker==null){
			        		bestbunker=bp;
			        		System.out.println("secondpointfive");
		        		}
		        		else if(BWTA.getGroundDistance(bp, choketile) < BWTA.getGroundDistance(bestbunker, choketile)){
		        			bestbunker=bp;
		        			System.out.println("third");
		        			game.drawBox(2, bestbunker.getX()*32, bestbunker.getY()*32, 
		        					bestbunker.getX()*32+32, bestbunker.getY()*32+32, red);
		        			}	//game.drawBox(2, bp.getX()*32, bp.getY()*32, bp.getX()*32+32, bp.getY()*32+32, red);
	        			}
	        		}	        	

	        }
	        if(bestbunker!=null){
	        	game.drawBox(2, bestbunker.getX()*32, bestbunker.getY()*32, 
					bestbunker.getX()*32+32, bestbunker.getY()*32+32, red);
	        }
        }*/
       /* for (Polygon uwp : unwalk) {
    		game.drawBox(2, uwp.getCenter().getX()*-4, uwp.getCenter().getY()*-4, 
    				uwp.getCenter().getX()*-4+32, uwp.getCenter().getY()*-4+32, blue);
    		System.out.println(uwp.getCenter());

        }*/
       /* for (Region rgn: regions) {
        	//if(rgn != null){
    		game.drawBoxMap(rgn.getBoundsLeft(), rgn.getBoundsTop(), rgn.getBoundsRight(), rgn.getBoundsBottom(), blue);
        	//}
        }
        for (Region rgn: regions) {
    		game.drawDotMap(rgn.getCenter().getX(), rgn.getCenter().getY(), blue);
        }*/
    		
        //System.out.println("scvnum: " + counter.scvnum);
        
        StringBuilder units = new StringBuilder("My units:\n");
		for (Unit myUnit : self.getUnits()) {
            //if there's enough minerals and supply, train an SCV
			
			if(commander2.order == null || commander2.order.finished == 1){
				
	            if (myUnit.getType() == UnitType.Terran_Command_Center && self.minerals() >= 50 
	            		&& self.supplyTotal() - self.supplyUsed() >= 2 && myUnit.isTraining() == false
	            		&& counter.scvnum <= 27) {
	                myUnit.train(UnitType.Terran_SCV);
	            }
	            
	            if (myUnit.getType() == UnitType.Terran_Barracks && self.minerals() >= 50 
	            		 && myUnit.isTraining() == false) {
	                myUnit.train(UnitType.Terran_Marine);
	            }
			}
            
            //if it's a drone and it's idle, send it to the closest mineral patch
            if ((myUnit.getType().isWorker() && myUnit.isIdle()) && (counter.scout == null 
            		|| (counter.scout != null && counter.scout.getID() != myUnit.getID()))) {
                Unit closestMineral = null;
                //find the closest mineral
                for (Unit neutralUnit : game.neutral().getUnits()) {
                    if (neutralUnit.getType().isMineralField()) {
                        if (closestMineral == null || myUnit.getDistance(neutralUnit) < myUnit.getDistance(closestMineral)) {
                            closestMineral = neutralUnit;
                        }
                    }
                }
                //if a mineral patch was found, send the drone to gather it
                if (closestMineral != null) {
                    myUnit.gather(closestMineral, false);
                }
            }

    	}//end of main loop

		commander2.doallofthethings();
		//System.out.println("Marines: " + counter.marinenum);
		
		if(counter.marinenum >= 25 && counter.attack == 0){//attack flag so no repeat command
			counter.attack = 1;
			for (Unit myUnit : self.getUnits()) {

			    if (myUnit.getType() == UnitType.Terran_Marine){
			    	myUnit.attack(enemystartbase);

			    }
			}
		}
		if(counter.marinenum <= 10 && counter.attack == 1){
			counter.attack = 0;
		}
		if(counter.scvnum >= 5 && counter.scout == null && counter.scouted == 0){
			for (Unit myUnit : self.getUnits()) {

			    if (myUnit.getType() == UnitType.Terran_SCV){
			    	myUnit.move(choke.getCenter());
			    	//counter.scvnum-=1;
			    	counter.scout = myUnit;
			    	counter.scouted  = 1;
			    	break;
			    }
			}
			
		}
		if(counter.scout != null && counter.scout.isIdle())	
		{
		//counter.attacked = 0;
			if(enemymainunit!=null)
			{
				counter.scout.attack(enemymainunit);	
			}
			else {
				counter.scout.attack(enemystartbase);	
			}
		}
		if(counter.scout != null && counter.scout.isAttackFrame())
		{
			//counter.attacked = 1;
			inradiuslist = counter.scout.getUnitsInRadius(500);
			
			
			
			for(Unit unit: inradiuslist)
			{
				if(unit.getType() == UnitType.Terran_Command_Center || unit.getType() == UnitType.Protoss_Nexus || unit.getType() == UnitType.Zerg_Hatchery)
				{
					enemymainunit = unit;
					break;
				}
			}
			System.out.println("The set: " + inradiuslist);
			System.out.println("The size: " + inradiuslist);
			System.out.println("Home:" + enemymainunit);
			counter.scout.move(mymainunit.getPosition());
		}
		/*if(counter.scout.getPosition() == choke.getCenter() )
		{
			
		
		}*/
		/*if(counter.test != 1 && counter.scout != null && counter.scout.isGatheringMinerals()){

			//counter.scout.holdPosition();
			//System.out.println("holdin2g");
			choketile = counter.scout.getTilePosition();
			
	        
	        for(int n = -7; n < 8; n++){
	        	for (int m = 7; m >-8; m--){
	        		TilePosition nct = new TilePosition(choketile.getX()+n,choketile.getY()+m);
	        		bunkerarea.add(nct);
	        	}
	        	
	        }
	        System.out.println(bunkerarea);
	        System.out.println("ChokeTile: " + choketile);
			//Position next = new Position(choke.getCenter().getX()+50,choke.getCenter().getY()+50);
			counter.scout.move(mystartbase.getPosition());
			//counter.scout = null;
			counter.test = 1;
		}
	/*	
		if(counter.scvnum >= 10 && counter.scout == null){
			for (Unit myUnit : self.getUnits()) {

			    if (myUnit.getType() == UnitType.Terran_SCV){
			    	myUnit.move(enemystartbase);
			    	counter.scvnum-=1;
			    	counter.scout = myUnit;
			    	break;
			    }
			}
		}
			    //because when it arrives it's idle and so it returns
		if(counter.scout!=null && counter.scout.isGatheringMinerals() ){
			
			if(self.minerals() >=150){
			TilePosition buildTile = 
	    			getBuildTile(counter.scout, UnitType.Terran_Barracks, esb);
				System.out.println("buildtile done");
	
	    		if (buildTile != null) {
	    			counter.scout.build(buildTile, UnitType.Terran_Barracks);  
	    			System.out.println("found solid location");
	    		}	
			}
			else	{counter.scout.holdPosition();}
		}
			    
			    //counter.scvnum-=1; if this stays here then as long as an scv
			    //is already scouting it will keep decrementing to zero, turning on the 
			    //flag, and sending the same scv, before counting up again
			    //at beginning of the main loop. but if I lose that scv
			    //it immediatly sends a new one as soon as it decrements below 10
			    //could be useful in a weird way? lol 
			*/
		

        game.drawTextScreen(10, 25, units.toString());
    }

    public static void main(String[] args) {
        new TestBot1().run();
    }
}