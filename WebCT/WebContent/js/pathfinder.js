


/** ********************** Path Finder *********************** */

// create the path finder UI on the page
function CreatePathFinder() {
	insertIntoPlayerSelect();
	insertIntoGoalSelect();
}

// Calculate player's path
function findPath() {
	var playerId = jQuery("#playerSelect").val();
	var goalId = jQuery("#goalSelect").val();
	var maxSteps = parseInt(jQuery("#numStepsPathFinder").val());

	var x = parseInt(game.getPlayerPositionX(playerId));
	var y = parseInt(game.getPlayerPositionY(playerId));

	var gX = parseInt(game.getGoalPositionX(goalId));
	var gY = parseInt(game.getGoalPositionY(goalId));

	var path = this.factorial2(x, y, gX, gY, "", 0, maxSteps);
	// var path=this.factorial2(2,1,2,3,"",0,4);
	// alert(path);
	var paths = path.split("&");

	// order
	var l1;
	var l2;
	var temp;
	// alert(paths.length);
	for ( var i = 0; i < paths.length; i++)
		for ( var j = 0; j < paths.length; j++) {
			l1 = paths[i].split("|").length;
			l2 = paths[j].split("|").length;

			if ((i != j) && (l1 < l2)) {
				temp = paths[i];
				paths[i] = paths[j];
				paths[j] = temp;

			}

		}

	// remove table from container
	if (document.getElementById('pathFindeTable')) {
		var container1 = document.getElementById("pathFinderContainer");
		var tbl1 = document.getElementById("pathFindeTable");
		container1.removeChild(tbl1);
	}

	// append new tbl
	createTable();
	var numOfMoves;
	var missing;
	var require;
	appendRowIntoTable("", "moves", "requichips", "missing chips");
	for ( var j = 0; j < paths.length; j++) {
		// get num of moves
		numOfMoves = paths[j].split("|").length - 1;
		// get requid chip
		require = this.getRequireChips(paths[j]);
		// get missing chips
		missing = this.getMissingChips(paths[j]);
		if (numOfMoves > 0)
			appendRowIntoTable(paths[j], numOfMoves, require, missing);
	}

};
// end findpath function

// assign goals to players
function getPlayersGoal() {
	// alert();
	removeAllOption("goalSelect");
	// appendOptionLast("Choose Goal",0,"goalSelect");
	var dropdownIndex = document.getElementById('playerSelect').selectedIndex;
	var goalType;
	var playerName = document.getElementById('playerSelect')[dropdownIndex].text;
	var playerId = document.getElementById('playerSelect')[dropdownIndex].value;
	var playerSerial = game.getPlayerSerial(playerId);
	var sumOfAppend = 0;
	//for ( var j = 0; j < game.players[playerSerial].goalsId.length; j++) {
	for ( var j = 0; j < game.goals.length; j++) {
		goalType = game.goals[j].type;
		sumOfAppend++;
		appendOptionLast(goalType, goalType, "goalSelect", '');

	}
	
	
};
// end function

// Fill players into playersSelect DropDownList
function insertIntoPlayerSelect()
{
	
	
	var playerId;
	var playerName;
	removeAllOption("playerSelect");
	playerId = game.getMe();
	playerName = game.getPlayerName(playerId);
	appendOptionLast(playerName, playerId, "playerSelect", 'img/' + game.getPlayerIcon(playerId));
	
	
	for ( var i = 0; i < playerNumTotal; i++) {
		playerId = game.getPlayerId(i);
		if(game.getisPlayerMe(playerId) != "true")
		{
			var found = false;
			for(var j =0 ; j < game.numOfGolals &&!found ; j++)
			{
				if(game.goals[j].type%10 == playerId)
				{
					playerName = game.getPlayerName(playerId);
					appendOptionLast(playerName, playerId, "playerSelect", 'img/' + game.getPlayerIcon(playerId));
					found =true;
				}
			}
			
		}
	}
	
	$("#playerSelect").msDropDown();
	
	
	//gil
	//    var playersIconsDropDown = document.getElementById('playerSelect_title');
	//	
	//	playersIconsDropDown.style.width = '120px';
	//	playersIconsDropDown.style.height = '30px';
	//	
	//gilend
	
}
// End of insertIntoPlayerSelect function

// Fill goals into goalSelect DropDownList
function insertIntoGoalSelect()
{
	
	removeAllOption("goalSelect");
	// appendOptionLast("Choose Goal",0,"goalSelect");
	var dropdownIndex = document.getElementById('playerSelect').selectedIndex;
	var goalType;
	//var playerName = document.getElementById('playerSelect')[dropdownIndex].text;
	var playerId = document.getElementById('playerSelect')[dropdownIndex].value;
	//var playerSerial=game.getPlayerSerial(playerId); 
	var goalNum=0;
	//alert("insertIntoGoalSelect -revelation game.numOfGolals "+game.numOfGolals);
	
	for(var j =0 ; j < game.numOfGolals; j++)
	{
		goalType=game.goals[j].type;
		
		//gil
		//alert(" goalType " +goalType);
		//gilend
		
			goalNum++;
			appendOptionLast(goalNum,goalType,"goalSelect", '');
						
	}	
	
	
}
// End of insertIntoGoalSelect function

// Finds all paths from initial location to goal according to max number of
// steps entered
function factorial2(x,y,gX,gY,path,steps,maxSteps)
{
	if ((x==gX)&&(y==gY))
	{
		
		var duplicate=this.hasDuplicate(path);
		if (duplicate==false)
			return path+x+","+y+"&";
		if (duplicate==true)
			return "";
	
	}
    if (steps >= maxSteps)
    {
        // return path+"<br>";
		return "";
    }
	else if ((x<0)||(y<0)||(x>=numOfRows)||(y>=numOfColumns))
	{
		return "";
	}
    else
	{
	return this.factorial2(x+1,y,gX,gY,path+""+x+','+y+"|",steps+1,maxSteps)+this.factorial2(x,y+1,gX,gY,path+""+x+','+y+"|",steps+1,maxSteps)+this.factorial2(x-1,y,gX,gY,path+""+x+','+y+"|",steps+1,maxSteps)+this.factorial2(x,y-1,gX,gY,path+""+x+','+y+"|",steps+1,maxSteps);
	}
}
// End of function

// check if a path already exists
function hasDuplicate(str)
{
	// var str="0,0|1,0|2,0|0,0|1,0|2,0|0,0|1,0|0,0|1,0|0,0";
	// check for duplicate if has not send
		var hasD=false;
		var px1; 
		var py1; 
		var co1;
		var px2;
		var py2;
		var co2;
		var ar=str.split("|");
		for (var i=0;i<ar.length-1;i++)
		{
			co1=ar[i].split(",");
			px1=co1[0];
			py1=co1[1];
			for (var j=0;j<ar.length-1;j++)
				{	
					co2=ar[j].split(",");
					px2=co2[0];
					py2=co2[1];
					/*
					 * document.write("i="+i+" "+"j="+j+"<br>");
					 * document.write("x="+px2+" "+"x="+px1+"<br>");
					 * document.write("y="+py2+" "+"y="+py1+"<br>");
					 * document.write("<br>");
					 */
					if ((px1==px2)&&(py1==py2)&&(i!=j))
					{
						return true;
					}
				}
		}
		return hasD;
}
// End of function

// creates a table to contain and present the paths to the goal
function createTable()
{
	var container=document.getElementById("pathFinderContainer");
    var tbl     = document.createElement("table");
	 tbl.id="pathFindeTable";
	 tbl.setAttribute("border", "2");
	 container.appendChild(tbl);
}
// end of function

// Append row with a path to the table
function appendRowIntoTable(path,moves,missing,required)
{
		
  // creates a <table> element and a <tbody> element
  var tbl     = document.getElementById("pathFindeTable");
  var tblBody = document.createElement("tbody");
  var row = document.createElement("tr");
  // row.setAttribute('id',path);
	
	// show path on board
	if (moves!="moves")
	{
		// on Mouse Over Event
		row.onmouseover = function ()
		{ 
			var paths=path.split("|");
			var p;
			var x;
			var y;
			for (var k=0;k<paths.length;k++)
			{
				// current div
				p=paths[k].split(",");
				x=p[0];
				y=p[1];
				var div3=document.getElementById("DivRow" + x+"Column"+y);
				div3.className =  "cssDDContainer2";
				// specify the arrow class
						// privious div
						var xPriv;
						var yPriv;
						var xPrivPriv;
						var yPrivPriv;
						if (k>0)
						{
							pPriv=paths[k-1].split(",");
							xPriv=pPriv[0];
							yPriv=pPriv[1];
						}
						if(k>2)
						{ 
							pPrivPriv=paths[k-2].split(",");
							xPrivPriv=pPrivPriv[0];
							yPrivPriv=pPrivPriv[1];
						}
						if ((xPriv==x)&&(y>yPriv))
						{
								var div3=document.getElementById("DivRow" + xPriv+"Column"+yPriv);
								div3.className =  "rigth";
						}
						else if((xPriv==x)&&(y<yPriv))
						{
							var div3=document.getElementById("DivRow" + xPriv+"Column"+yPriv);
								div3.className =  "left";
						}
						else if((xPriv>x)&&(y==yPriv))
						{
						
							var div3=document.getElementById("DivRow" + xPriv+"Column"+yPriv);
								div3.className =  "up";
						}
						else if((xPriv<x)&&(y==yPriv))
						{
							// alert("x="+x+"y="+y+"xpriv="+xPriv+"ypriv="+yPriv+"xprivpriv="+xPrivPriv+"yprivpriv="+yPrivPriv);
							var div3=document.getElementById("DivRow" + xPriv+"Column"+yPriv);
								div3.className =  "down";
						}
						else
						{
						var div3=document.getElementById("DivRow" + x+"Column"+y);
						div3.className =  "cssDDContainer2";
						}
						// alert("k="+parseInt(k+1)+"pa="+paths.length);
				// end
			}
		};
		
		// on ouse Out event
		row.onmouseout = function ()
		{ 
			var paths=path.split("|");
			var p;
			var x;
			var y;
			for (var k=0;k<paths.length;k++)
			{
				p=paths[k].split(",");
				x=p[0];
				y=p[1];
				var div3=document.getElementById("DivRow" + x+"Column"+y);
				div3.className =  "cssDDContainer";
			}
		};
	}
	// end show path on board
	
	
	// column 1
  var cell = document.createElement("td");
	var cellText = document.createTextNode(moves);
  cell.appendChild(cellText);
  row.appendChild(cell);
	
	// column 2
  var cell = document.createElement("td");
	cell.innerHTML=missing;
	// var cellText = document.createTextNode(missing);
  // cell.appendChild(cellText);
  row.appendChild(cell);
	
	// column 3
  var cell = document.createElement("td");
	cell.innerHTML=required;
  row.appendChild(cell);
	
  tblBody.appendChild(row);
  tbl.appendChild(tblBody);
	
 
}
// End of appendRowIntoTable function

// Gets the required chips needed for path
function getRequireChips(path)
{

	var px;
	var py;
	var pos=path.split("|");
	var cor;
	var col;
	var arr=new Array(game.numOfColors);
	// init arra
	for (var k=0;k<game.numOfColors;k++)
	{
		arr[k]=0;
	}
	// start from "1" because the start position
	for(var j=1;j<pos.length;j++)
	{
		cor=pos[j].split(",");
		px=cor[0];
		py=cor[1];
		if ((px!="")&&(py!=""))
		{
			col=game.getColorCell(px,py);
			for (var i=0;i<game.numOfColors;i++)
						if (game.colors[i].name==col)
							{
								arr[i]=arr[i]+1;
							}
		}
	}

	// return the str with color like <table><tr><td style="background-color
	// :#FFF62D;">1</td><td style="background-color
	// :#BF7FFF;">3</td></tr></table>
	var req;
	var str="<table><tr>";
	for (var i=0;i<game.numOfColors;i++)
							{
								if ((game.colors[i].name=="#BFFF7F")||(game.colors[i].name=="#FF7F7F"))
									req="<font style='color:black'>"+arr[i]+"</font>";
								else
									req=arr[i];
								str=str+'<td style="background-color :#'+game.colors[i].name+';">'+req+'</td>';
							}
		str=str+"</tr></table>";
		return str;
}
// End of function - return the require chips

// Calculates the missing chips the player needs for a path to the goal
function getMissingChips(path)
{

	var px;
	var py;
	var pos=path.split("|");
	var cor;
	var col;
	var arr=new Array(game.numOfColors);
	
	// init arra
	for (var k=0;k<game.numOfColors;k++)
	{
		arr[k]=0;
	}
	// start from "1" because the start position
	for(var j=1;j<pos.length;j++)
	{
		cor=pos[j].split(",");
		px=cor[0];
		py=cor[1];
		if ((px!="")&&(py!=""))
		{
			col=game.getColorCell(px,py);
			for (var i=0;i<game.numOfColors;i++)
						if (game.colors[i].name==col)
							{
								arr[i]=arr[i]+1;
							}
		}
	}
	
	var dropdownIndex = document.getElementById('playerSelect').selectedIndex;
	var colorName;
	var colorQuan;
	var playerId = document.getElementById('playerSelect')[dropdownIndex].value;
	var playerSerial=game.getPlayerSerial(playerId); 
	// substract
	for(var i=0;i<game.numOfColors;i++)
	{
		colorName=game.players[playerSerial].cards[i].color;
		colorQuan=game.players[playerSerial].cards[i].sum;
		for (var j=0;j<game.numOfColors;j++)
						if (game.colors[j].name==colorName)
							{
								arr[j]=arr[j]-parseInt(colorQuan);
						}
		
	}	
	
	
	for(var i=0;i<game.numOfColors;i++)
	{
		for (var i=0;i<game.numOfColors;i++)
						if (arr[i]<0)
							{
								arr[i]=0;
							}
		
	}	
	
	// return the str with color like <table><tr><td style="background-color
	// :#FFF62D;">1</td><td style="background-color
	// :#BF7FFF;">3</td></tr></table>
	var req;
	var str="<table><tr>";
	for (var i=0;i<game.numOfColors;i++)
							{
								if ((game.colors[i].name=="#BFFF7F")||(game.colors[i].name=="#FF7F7F"))
									req="<font style='color:black'>"+arr[i]+"</font>";
								else
									req=arr[i];
								str=str+'<td style="background-color :#'+game.colors[i].name+';">'+req+'</td>';
							}
		str=str+"</tr></table>";
		return str;
}
// End getMissingChips function


//updates the goals of the pathfinder after number of goals changes
function updatePathFinderGoals()
{
	insertIntoGoalSelect();
	
	// remove table from container
	if (document.getElementById('pathFindeTable')) {
		var container1 = document.getElementById("pathFinderContainer");
		var tbl1 = document.getElementById("pathFindeTable");
		container1.removeChild(tbl1);
	}

}


/** ********************** END of Path Finder *********************** */