// times
var numTurns;
var CommunicationPhaseTimeConst;
var RevelationPhaseTimeConst;
var MovementPhaseTimeConst;
var FeedBackPhaseTimeConst;

// for the history grid
var recId;

// set temp times
var CommunicationPhaseTime;
var RevelationPhaseTime;
var MovementPhaseTime;
var FeedBackPhaseTime;

// customize board size
var numOfRows;
var numOfColumns;

// player num MUST be because IE8 and fire
var playerNumTotal;

// get num of colors
var colorNumTotal;

// number of game phases
var numOfPhases;

// card in ID historyProArea
var historyProAreaCard;

// the current time displayed on the progress bar
var pValue;
// the current phase of the game
var currentPhase;
// the current phase index in phases json array
var currentPhaseIndex;
var lastPhaseIndex;
// the time of the current phase of the game
var currentPhaseTime;
// revelation chips flag (true/false)
var isRevelationEnabled;
// my role
var lastRole;
//indicate if a phase was changed
var phaseChanged;
// revelation goal flag (true/false) indicating whether goal revelation phase occured
var goalRevelationPhaseEnded;
// if the goal revelation submitted
var isGoalRevelationSubmitted;
//indicate whether there is a pending proposal and its message id
//id = -1 indicates that there is no pending message
var pendingProposalMsgID;

var clock;
var score;

function Init() {
	// times
	numTurns = 3;
	// CommunicationPhaseTimeConst = game.CommunicationPhaseTimeConst;
	// RevelationPhaseTimeConst = game.RevelationPhaseTimeConst;
	// MovementPhaseTimeConst = game.MovementPhaseTimeConst;
	// FeedBackPhaseTimeConst = game.FeedBackPhaseTimeConst;

	// for the history grid
	recId = 100;

	// set temp times
	// CommunicationPhaseTime = CommunicationPhaseTimeConst;
	// RevelationPhaseTime = RevelationPhaseTimeConst;
	// MovementPhaseTime = MovementPhaseTimeConst;
	// FeedBackPhaseTime = FeedBackPhaseTimeConst;

	// customize board size
	numOfRows = game.getSizeRows();
	numOfColumns = game.getSizeCols();

	// player num MUST be because IE8 and fire
	playerNumTotal = game.getNumOfPlayer();

	//gil
	//alert("Init - playerNumTotal = game.getNumOfPlayer();");
	//gilend
	
	
	// get num of colors
	colorNumTotal = game.getNumOfColors();

	// number of game phases
	numOfPhases = game.numOfPhases;
	
	// card in ID historyProArea
	historyProAreaCard = "history";

	// the current time displayed on the progress bar
	pValue = 0;
	// the current phase of the game
	currentPhase = null;
	// the current phase index in phases json array
	currentPhaseIndex = 0;
	// the time of the current phase of the game
	currentPhaseTime = 0;
	
	// set revelation flag
	isRevelationEnabled = game.isRevelationEnabled;	
	
	// set role
	lastRole = game.role;
	
	//set goal revelation phase flag
	goalRevelationPhaseEnded = false;
	
	phaseChanged = true;
	//set pending proposal flag
	pendingProposalMsgID = -1;
	clock = 0;
	score = 1000;
	
//	document.getElementById("user").innerHTML = user;
	UpdateServer();
	
	
}

// parse query string from url
function getQueryVariable(variable) {
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for ( var i = 0; i < vars.length; i++) {
		var pair = vars[i].split("=");
		if (pair[0] == variable) {
			return pair[1];
		}
	}
}
// END parse query string from url

// CREATE TABLE GAME BOARD
function createGameBoard() {
	
	//alert("Inside createGameBoard");
	
	// create table
	var drops = new Array();
	var theTable = document.getElementById("GameBoardTable");
	for ( var i = 0; i < numOfRows; i++) {
		var newRow = theTable.insertRow(theTable.rows.length);
		newRow.id = newRow.uniqueID;
		var newCell;
		for ( var j = 0; j < numOfColumns; j++) {
			// create column
			newCell = newRow.insertCell(j);
			newCell.id = "CellRow" + i + "Column" + j;
			newCell.innerText = i+", "+j;
			newCell.bgColor = "white";
			newCell.ondblclick = move; //function() { alert(i+'-'+j);move(i,j); };
			// create div
			var newDiv = document.createElement("div");
			newDiv.className = "cssDDContainer";
			newDiv.id = "DivRow" + i + "Column" + j; // id of new div
			
			var cell = document.getElementById("CellRow" + i + "Column" + j);
			cell.appendChild(newDiv);
		}
	}

	var playerContainer;
	var playerId;
	for ( var i = 0; i < playerNumTotal; i++) {
		playerId = game.getPlayerId(i);
		var newDiv = document.createElement("div");
		playerContainer = document.getElementById("DivRow"
				+ game.getPlayerPositionX(playerId) + "Column"
				+ game.getPlayerPositionY(playerId));
		// newDiv.className = "cssDDItem";
		newDiv.id = "player" + playerId;
		
		
		if (game.getisPlayerMe(playerId) == "true"){
			newDiv.innerHTML =playerId+ '<img src="img/' + game.getBigPlayerIcon(playerId) + '"/>';
		}
		else
		{
			newDiv.innerHTML =playerId+ '<img src="img/' + game.getPlayerIcon(playerId) + '"/>';
		}
		playerContainer.appendChild(newDiv);
		var divElem = document.getElementById("player" + playerId);
		// create a div empty that can be drag
		// if (game.getisPlayerMe(playerId) == "true")
		// divElem.dd = new Ext.dd.DDProxy("player" + playerId);
	}
}
// END CREATE GAME BOARD

function move(e)
	{
	var target = e.target || e.srcElement;
	
	var posx = target.id.substring(6, 7);
	var posy = target.id.substring(13, 14);
	//alert(posx+posy);
	var stringJ = "{\"posx\" : \"" + posx + "\", \"posy\" : \"" + posy + "\"}";

		jQuery.ajax({
			type : "post",
			url : "move.jsp",
			data : "json=" + stringJ,
			success : function(msg) {
		   // alert(msg);
			}
		});

	}
// ADD COLORS TO BOARD
function addColors() {
	for ( var i = 0; i < game.borderColors.length; i++) {
		var posX = game.borderColors[i].posX;
		var posY = game.borderColors[i].posY;
		
		// jQuery("#DivRow" + posX + "Column" + posY).removeClass();
		jQuery("#DivRow" + posX + "Column" + posY).css({
			'background-color' : '#' + game.borderColors[i].color
		});
	}
}
// END ADD COLORS TO BOARD


// CREATE GOALS
function createGoals() {
	
	/*
	//alert("Inside  createGoals");
	var goalSquare;
	for ( var i = 0; i < game.numOfGolals; i++) {
		var newDiv = document.createElement("div");
		newDiv.innerHTML = '<img src="img/flag2.png">';
		goalSquare = document.getElementById("DivRow" + game.goals[i].posX
				+ "Column" + game.goals[i].posY);
		goalSquare.appendChild(newDiv);
		
		//alert("Goal " + "DivRow" + game.goals[i].posX	+ "Column" + game.goals[i].posY);
	}
	*/
	////////////////gil
	var playerID = game.getMe();
	var goalSquare;
	
	if (game.numOfGolals ==1 && game.role >0 )  //Bargain Only and WebCTRevelation
	{
		 var newDiv = document.createElement("div");
		 newDiv.innerHTML = '<img src="img/goal.gif">';
		 goalSquare = document.getElementById("DivRow" + game.goals[0].posX
                 + "Column" + game.goals[0].posY);
		 goalSquare.appendChild(newDiv); 
	}
	else  //Signaling
	{
		
	    for ( var i = 0; i < game.numOfGolals; i++) 
	    {
	    	var newDiv = document.createElement("div");
          
	    	if(game.goals[i].type == playerID) //my goal
	    	{

	    			newDiv.innerHTML = '<img src="img/goalA.gif">';
	    		
	    		
	    	}
	    	else  //opponent's goal
	    	{
	    		if(game.goals[i].type == -1 && game.role >0)

	    			newDiv.innerHTML = '<img src="img/goal.gif">';
	    		
	    		if(game.goals[i].type > -1 && game.role >0)

	    			newDiv.innerHTML = '<img src="img/goalB.gif">';
	    		
	    	}
	    	 goalSquare = document.getElementById("DivRow" + game.goals[i].posX
                     + "Column" + game.goals[i].posY);
	    	 goalSquare.appendChild(newDiv);     
           
	    }//for
		
		
		
		
	}//else
	
	
	
	
	////////////////gilend
	
	
	
	
	/*
	var playerID = game.getMe();
    var possibleGoals = false;
    for( var i = 0; i < game.numOfGolals && possibleGoals == false; i++)
    {
            if(game.goals[i].type >= 10) //&& game.goals[i].type%10 == playerID)
            {
                    possibleGoals = true;
            }
    }
    
    var goalSquare;
    for ( var i = 0; i < game.numOfGolals; i++) {
            if(possibleGoals == false || game.goals[i].type >= 10)
            {
                    var newDiv = document.createElement("div");
                    if(possibleGoals == false){ //bargain only
                            newDiv.innerHTML = '<img src="img/goal.gif">';
                    }
               else if(game.goals[i].type%10 != playerID){ // possiblity 50 rival
                            newDiv.innerHTML = '<img src="img/possibleG50.gif">';
                    }
               else if(game.goals[i].type >= 10 && game.goals[i].type <20){  //g 50 me
                       newDiv.innerHTML = '<img src="img/goalA_belief50.gif">';
               }
               else{ // possible 50 me
                       newDiv.innerHTML = '<img src="img/beliefMe50.gif">';
               }
                    goalSquare = document.getElementById("DivRow" + game.goals[i].posX
                                    + "Column" + game.goals[i].posY);
                    goalSquare.appendChild(newDiv);        
            }   
    }
    */
}
// END CREATE GOLAS

// update the progress bar
function updateProgressBar() {

	if (game.isEnded == true) {
		self.location = "ended.jsp";
	}
	
	if (phaseChanged == true) {			
		lastRole = game.role;		
		user = "Role: Team member";
		if (game.role == 1)
			user = "Role: Coordinator";
		if (game.role == 2)
			user = "Role: Flexible Coordinator";
		$("#user").html(user);
		if (currentPhase == null) {
			currentPhase = game.phases[currentPhaseIndex].name;
		}
		else
		{
			currentPhaseIndex = game.phasesIndex[currentPhase];			
		}		
	}
		// clear first row at proposals table
		//clearMessagesUI();
		
		switch (currentPhase) {
		case "Norm Phase":
				//chipRevelationArea();
				//clearProposalTableArea();
			if (phaseChanged) {
				if (game.role > 0) {
					loadNormGoalProposalsTable();					
				}
				if  (game.role == 2) {
					
					loadNormColorProposalsTable();
					loadNormGroupProposalsTable();
				}
				SetHeaderMsg('Setting up phase');	
			}
			break;
		case "Movement Phase":
			if (phaseChanged ) {
			// if communication phase -> build proposal area for players
			if (game.role > 0) {
			clearNormMessagesUI();
			}
			//UpdateServer();			
			
			loadProposalsTable();
			proposalArea();
			SetHeaderMsg("Moving and exchanging phase");
			}
			// notify			
			break;
		case "Feedback Phase":			
			SetHeaderMsg('The game has ended');
			break;
		default:			
			break;		
		}
	
	phaseChanged = false;
	
	//document.getElementById("TimeLeft").innerHTML = currentPhase.substring(0, currentPhase.length - 5) + ' - ' + secondsToTime(pValue);
	document.getElementById("TimeLeft").innerHTML = "clock: " + clock;
	//document.getElementById("divPhases").innerHTML = "score: " + score;

}

// END update the progress bar

// notification area
function SetHeaderMsg(textMsg) {
	$("#header").html(textMsg);
}

// clear message interface UI (first row in grid)
function clearMessagesUI() {
	removeElemFromDOM(document.getElementById('divTableMsgType'));
	removeElemFromDOM(document.getElementById('divTableSender'));
	removeElemFromDOM(document.getElementById('divTableReceiver'));
	removeElemFromDOM(document.getElementById('divTableSend'));
	removeElemFromDOM(document.getElementById('divTableReceive'));
	removeElemFromDOM(document.getElementById('divTableGoal'));
	removeElemFromDOM(document.getElementById('divTableMessage'));
	removeElemFromDOM(document.getElementById('divButtonPropose'));
	//removeElemFromDOM(document.getElementById('notifyContainer'));	
}
function clearNormMessagesUI() {
//	var e = document.getElementById('CNorms');
//	e.parentNode.removeChild(e);
//	var f = document.getElementById('GNorms');
//	e.parentNode.removeChild(f);
	removeElemFromDOM(document.getElementById('tblCNorms'),'div');
	removeElemFromDOM(document.getElementById('tblGNorms'),'div');
//	removeElemFromDOM(document.getElementById('divTableNMsgType'));
//	removeElemFromDOM(document.getElementById('divTableNReceiver'));
//	removeElemFromDOM(document.getElementById('divTableNGoal'));
//	removeElemFromDOM(document.getElementById('divTableNMessage'));
	removeElemFromDOM(document.getElementById('divButtonNPropose'),'div');
//	removeElemFromDOM(document.getElementById('divTableNCMsgType'));
//	removeElemFromDOM(document.getElementById('divTableNCSanction'));
//	removeElemFromDOM(document.getElementById('divTableNCReceiver'));
//	removeElemFromDOM(document.getElementById('divTableNCColor'));
//	removeElemFromDOM(document.getElementById('divTableNCNorm'));
//	removeElemFromDOM(document.getElementById('divButtonNCPropose'));
}
// END clear message interface UI (first row in grid)

function removeElemFromDOM(EId,parent) {
	$(EId).parents(parent).eq(1).remove();
//	$(EId).parentNode.removeChild(EId);
//	return(EObj=document.getElementById(EId))?EObj.parentNode.removeChild(EObj):false;
}

//Player Chips Area
function PlayerChips() {
	var theTable = document.getElementById("PlayerChips");
	
	for ( var i = 0; i < playerNumTotal; i++) {

		var newCell;

		// create chip row
		var newRow1 = theTable.insertRow(theTable.rows.length);

		// player name
		newCell = newRow1.insertCell(0);
		newCell.innerHTML = "Player: ";
		
		
		newCella = newRow1.insertCell(1);
		var playerId = game.getPlayerId(i);
		newCella.innerHTML = '<img src="img/' + game.players[i].icon + '"/> '+playerId;
		
		// create column
		var newCell1 = newRow1.insertCell(2);
		newCell1.id = "CellRowPoints" + i;
		
		// create div
		var newDiv1 = document.createElement("div");
		
		
		newDiv1.id = "playerId" + game.players[i].id + "Points";
		newDiv1.innerHTML = 1000;
		//newDiv.style.

		newCell1.appendChild(newDiv1);
		
		var newRow = theTable.insertRow(theTable.rows.length);
		for ( var j = 0; j < game.players[i].cards.length; j++) {

			// create column
			var newCell3 = newRow.insertCell(j);
			newCell3.id = "CellRowChips" + i + "ColumnChips" + j;
			newCell3.className = "containerForChip";
			
			// create div
			var newDiv = document.createElement("div");
			// isme
			var isme = game.players[i].isme;
			// color sum chips
			var sumChips;

			// if player isme or not revelation game then show server chips
			// else show revelation chips
			if (isme == "true" || isRevelationEnabled == "false") {
				sumChips = game.players[i].cards[j].sum;
			}
			else {
				//sumChips = game.players[i].RevelationChips[j].sum;
				sumChips = "-";
			}
			
			
			newDiv.id = "playerId" + game.players[i].id + "Color"
					+ game.players[i].cards[j].color;
			newDiv.innerHTML = sumChips;
			newDiv.style.background = "#" + game.players[i].cards[j].color;

			var cell = document.getElementById("CellRowChips" + i
					+ "ColumnChips" + j);

			cell.appendChild(newDiv);
			
		}
		
	}
	
	
}

//Reset the proposal table area before/after changes where made by goal revelation phase
function clearProposalTableArea(){
	document.getElementById('proposals').innerHTML = '';
	var tableProposals = document.createElement('table');
	tableProposals.setAttribute('id', 'tblProposals');	
	document.getElementById('proposals').appendChild(tableProposals);

}

function loadNormGroupProposalsTable() {	
	jQuery("#tblGNorms").jqGrid(
			{
				datatype : "local",
				height : 200,
				colNames : ['MsgType', 'Norm', 'Sanction',  'Response' ],
				colModel : [ {
					name : 'MsgType',
					index : 'MsgType',
					width : 90,
					sortable : false
				},{
					name : 'Norm',
					index : 'Norm',
					width : 480,
					sortable : false
				},{
					name : 'Sanction',
					index : 'Sanction',
					width : 70,
					sortable : false
				},{
					name : 'Response',
					index : 'Response',
					width : 150,
					sortable : false
				} ],
				multiselect : false,
				hoverrows : false				
			});

	var defaultData = {
			// Id : MessageId,
			// Proposer : playerName,
			// Proposer : '<img height=25px width=25px src="img/me.gif"/>',
			// Receiver : '<img height=25px width=25px src="img/'
			// + game.getPlayerIcon(SenderID) + '"/>',
			// Proposer : SenderID,
			// Receiver : SenderID == 0 ? 1 : 0,
			MsgType : "<div id='divTableGMsgType'></div>",
			Norm : "<div id='divTableGNorm'></div>",
			Sanction : "<div id='divTableGSanction'></div>",
			Response : "<div id='divButtonGPropose'></div>"
		};
	
	jQuery("#tblGNorms").jqGrid('addRowData', 0, defaultData);
	
	// add a button for submit
	var cont = document.createElement("div");
	
	cont.innerHTML = "<button id='buttonSubmitNormG' onclick='buttonSubmitNormG_click();'>Submit</button>";
	document.getElementById('divButtonGPropose').appendChild(cont);
	document.getElementById('gview_tblGNorms').style.height= '50px';
	
	// append div into Messages grid
	document.getElementById("divTableGNorm").innerHTML = 'obligation to all players';
	document.getElementById("divTableGMsgType").innerHTML = 'Accept requests';

	InsertIntoSanctionSelect('divTableGSanction');
	
}
// end Proposals table


//Send revelation details to the sever
function buttonSubmitNormG_click() {
	
	var sanctions = document.getElementById('divTableGSanctionDropDown');
	var sanction = sanctions.options[sanctions.selectedIndex].value;

	var message = "group obligation sent to accept requests";

	sendNormG("acceptRequests",sanction);
	var rowID = jQuery("#tblNorms").jqGrid('getGridParam', 'records');
	
	addNormToTable("Obligation", game.getMe(), "all players", rowID, message);
	
	
}

function sendNormG(norm,sanction)
{
	var stringJ = "{ \"norm\" : \"" + norm +"\", \"sanction\" : " + sanction +"}";
	jQuery.ajax({
		type : "post",
		url : "sendNormG.jsp",
		data : "json=" + stringJ,
		success : function(msg) {
			// alert(stringJ);
		}
	});
}

function loadNormColorProposalsTable() {	
	jQuery("#tblCNorms").jqGrid(
			{
				datatype : "local",
				height : 200,
				colNames : ['MsgType', 'Receiver', 'Color', 'Norm', 'Sanction',  'Response' ],
				colModel : [ {
					name : 'MsgType',
					index : 'MsgType',
					width : 90,
					sortable : false
				},{
					name : 'Receiver',
					index : 'Receiver',
					width : 75,
					sortable : false
				},{
					name : 'Color',
					index : 'Color',
					width : 90,
					sortable : false
				},{
					name : 'Norm',
					index : 'Norm',
					width : 315,
					sortable : false
				},{
					name : 'Sanction',
					index : 'Sanction',
					width : 70,
					sortable : false
				},{
					name : 'Response',
					index : 'Response',
					width : 150,
					sortable : false
				} ],
				multiselect : false,
				hoverrows : false				
			});

	var defaultData = {
			// Id : MessageId,
			// Proposer : playerName,
			// Proposer : '<img height=25px width=25px src="img/me.gif"/>',
			// Receiver : '<img height=25px width=25px src="img/'
			// + game.getPlayerIcon(SenderID) + '"/>',
			// Proposer : SenderID,
			// Receiver : SenderID == 0 ? 1 : 0,
			MsgType : "<div id='divTableNCMsgType'></div>",
			Receiver : "<div id='divTableNCReceiver'></div>",
			Color : "<div id='divTableNCColor'></div>", 
			Norm : "<div id='divTableNCNorm'></div>",
			Sanction : "<div id='divTableNCSanction'></div>",
			Response : "<div id='divButtonNCPropose'></div>"
		};
	
	jQuery("#tblCNorms").jqGrid('addRowData', 0, defaultData);
	
	// add a button for submit
	var cont = document.createElement("div");
	
	var playerIdToSend = game.getMe();
	cont.innerHTML = "<button id='buttonSubmitNormColor' onclick='buttonSubmitNormColor_click(" + playerIdToSend + ");'>Submit</button>";
	document.getElementById('divButtonNCPropose').appendChild(cont);
	document.getElementById('gview_tblCNorms').style.height= '50px';
	
	// append div into Messages grid
	//document.getElementById("divTableNCNorm").innerHTML = 'If you wish to send an obligation';
	document.getElementById("divTableNCMsgType").innerHTML = 'Norm Color';
	
	InsertIntoPlayersIconsSelect('divTableNCReceiver');
	InsertIntoNormColorSelect();
	InsertIntoNormSelect();
	InsertIntoSanctionSelect("divTableNCSanction");
	
}
// end Proposals table


//Send revelation details to the sever
function buttonSubmitNormColor_click(playerId) {
	
	var ddIcons = document.getElementById('divTableNCReceiverDropDown');
	var colors = document.getElementById('colorsDropDown');
	var sanctions = document.getElementById('divTableNCSanctionDropDown');
	var norms = document.getElementById('normsDropDown');
	var recipientID = ddIcons.options[ddIcons.selectedIndex].value;
	var color = colors.options[colors.selectedIndex].value;
	var norm = norms.options[norms.selectedIndex].value;
	var sanction = sanctions.options[sanctions.selectedIndex].value;
	if (norm == "yes")
		var message = "the obligation is to go through a " + colors.options[colors.selectedIndex].text + " square";
	else
		var message = "the prohibition is not to go through a " + colors.options[colors.selectedIndex].text + "square";
	sendNormColor(playerId,recipientID,colors.options[colors.selectedIndex].text,norm,sanction);
	//clearProposalTableArea();
	//loadNormGoalProposalsTable();
	// rowID = num of rows in proposals grid
	var rowID = jQuery("#tblNorms").jqGrid('getGridParam', 'records');
	//alert("rowID = "+rowID);
	
	addNormToTable("Obligation", playerId, recipientID, rowID, message);
	
	//clearMessagesUI();
	//$("#"+"0").hide();
	
}

function sendNormColor(playerIDSend,recipientID,color,norm,sanction)
{
	var stringJ = "{\"player\" : \"" + playerIDSend + "\" , \"recipient\" : \"" + recipientID; 
		stringJ = stringJ + "\", \"color\" : \"" + color + "\", \"norm\" : \"" + norm +"\", \"sanction\" : " + sanction +"}";
	jQuery.ajax({
		type : "post",
		url : "sendNormColor.jsp",
		data : "json=" + stringJ,
		success : function(msg) {
			 //alert(stringJ);
		}
	});
}

function loadNormGoalProposalsTable() {	
	jQuery("#tblNorms").jqGrid(
			{
				datatype : "local",
				height : 150,
				colNames : ['MsgType', 'Receiver', 'Goal', 'Message',  'Response' ],
				colModel : [ {
					name : 'MsgType',
					index : 'MsgType',
					width : 90,
					sortable : false
				}, {
					name : 'Receiver',
					index : 'Receiver',
					width : 75,
					sortable : false
				},{
					name : 'Goal',
					index : 'Goal',
					width : 90,
					sortable : false
				},{
					name : 'Message',
					index : 'Message',
					width : 385,
					sortable : false
				},{
					name : 'Response',
					index : 'Response',
					width : 150,
					sortable : false
				} ],
				multiselect : false,
				hoverrows : false				
			});

	var defaultData = {
			// Id : MessageId,
			// Proposer : playerName,
			// Proposer : '<img height=25px width=25px src="img/me.gif"/>',
			// Receiver : '<img height=25px width=25px src="img/'
			// + game.getPlayerIcon(SenderID) + '"/>',
			// Proposer : SenderID,
			// Receiver : SenderID == 0 ? 1 : 0,
			MsgType : "<div id='divTableNMsgType'></div>",
			Receiver : "<div id='divTableNReceiver'></div>",
			Message : "<div id='divTableNMessage'></div>", 
			Goal : "<div id='divTableNGoal'></div>",
			Response : "<div id='divButtonNPropose'></div>"
		};
	
	jQuery("#tblNorms").jqGrid('addRowData', 0, defaultData);
	
	// add a button for submit
	var cont = document.createElement("div");
	
	var playerIdToSend = game.getMe();
	cont.innerHTML = "<button id='buttonSubmitNormGoal' onclick='buttonSubmitNormGoal_click(" + playerIdToSend + ");'>Submit</button>";
	document.getElementById('divButtonNPropose').appendChild(cont);	
	
	// append div into Messages grid
	document.getElementById("divTableNMessage").innerHTML = 'If you wish to send an obligation';
	document.getElementById("divTableNMsgType").innerHTML = 'Norm Goal';
	
	InsertIntoPlayersIconsSelect("divTableNReceiver");
	InsertIntoNormGoalsSelect();
}
// end Proposals table


//Send revelation details to the sever
function buttonSubmitNormGoal_click(playerId) {
	
	var ddIcons = document.getElementById('divTableNReceiverDropDown');
	var goals = document.getElementById('goalsDropDown');
	var recipientID = ddIcons.options[ddIcons.selectedIndex].value;
	var goal = goals.options[goals.selectedIndex].value;
	var n = goal.indexOf(",");
	var x = goal.substr(0,n);
	var y = goal.substr(n+1);
	//playerId - my id
	//ddIcons - the player that I want to reveal my goal to
	isNormGoalSubmitted = true;
	var gx = game.goalx;
	var gy = game.goaly;
	
	for ( var i = 0; i < this.numGoals; i++)
		if (game.goals[i].type == -1) {
			gx= game.goals[i].posX;
			gy= game.goals[i].posY;
		}
	
	sendNormGoal(playerId,recipientID,x,y,gx,gy,100);
	//clearProposalTableArea();
	//loadNormGoalProposalsTable();
	//rowID = num of rows in proposals grid
	var rowID = jQuery("#tblNorms").jqGrid('getGridParam', 'records');
	//alert("rowID = "+rowID);
	
	addNormToTable("Obligation", playerId, recipientID, rowID, "Your obligation is to go to ("+x+","+y+").");
	//clearMessagesUI();
	//$("#"+"0").hide();
	
}

function sendNormGoal(playerIDSend,recipientID,x,y,ox,oy,sanction)
{
	var stringJ = "{\"player\" : \"" + playerIDSend + "\" , \"recipient\" : \"" + recipientID; 

		stringJ = stringJ + "\", \"x\" : " + x + ", \"y\" : " + y ;
		stringJ = stringJ + ", \"ox\" : " + ox + ", \"oy\" : " + oy +", \"sanction\" : " + sanction +  "}";


	jQuery.ajax({
		type : "post",
		url : "sendNormGoal.jsp",
		data : "json=" + stringJ,
		success : function(msg) {
			// alert(stringJ);
		}
	});
}

// send Receive proposal area
function proposalArea() {

	var rowSelectSend = 0;
	var rowColorSend = 2;
	var rowSelectReceive = 1;
	var rowColorReceive = 3;
	var playerId;
	var tab;

	row = new Array();
	cell = new Array();

	row_num = playerNumTotal;

	// tab = document.createElement('table');
	// tab.setAttribute('id', 'tableSend');

	// tbo = document.createElement('tbody');

	for (c = 0; c < row_num; c++) {
		// populate data only for me (player)
		playerId = game.getPlayerId(c);
		if (game.getisPlayerMe(playerId) == "true") {
			// number of colors for each player
			cell_num = game.players[c].cards.length;

			/* each player has 2 rows of data (send/receive) */
			for (i = 0; i < 2; i++) {
				tab = document.createElement('table');
				tab.setAttribute('id', i == rowSelectReceive ? 'tableReceive' : 'tableSend');
				tbo = document.createElement('tbody');

				row[i] = document.createElement('tr');
				for (k = 1; k < cell_num + 1; k++) {					
						cell[k] = document.createElement('td');
						switch (i) {
						case rowSelectSend:
							cell[k].setAttribute('id',
									'ProporseSendCellRowChips' + (i + (c * 4))
											+ 'ProposeSendColumnChips' + k);
							var cont = document.createElement("select");
							cont.setAttribute('id', 'SelectSendPlayerId'
									+ game.players[c].id + 'Color'
									+ game.players[c].cards[k - 1].color);
							cont.style.background = "#"
								+ game.players[c].cards[k - 1].color;
							break;
						case rowColorSend:
							cell[k].setAttribute('id',
									'ProporseSendCellRowChips' + (i + (c * 4))
											+ 'ProposeSendColumnChips' + k);
							var cont = document.createElement("div");
							cont
									.setAttribute(
											'id',
											'ProporseSendColorCellRowChips'
													+ (i + (c * 4))
													+ 'ProposeSendColorColumnChips'
													+ k);
							cont.innerHTML = "&nbsp;";
							cont.style.background = "#"
									+ game.players[c].cards[k - 1].color;
							break;
						case rowSelectReceive:
							cell[k].setAttribute('id',
									'ProporseReceiveCellRowChips'
											+ (i + (c * 4))
											+ 'ProposeReceiveColumnChips' + k);
							var cont = document.createElement("select");
							cont.setAttribute('id', 'SelectReceivePlayerId'
									+ game.players[c].id + 'Color'
									+ game.players[c].cards[k - 1].color);
							cont.style.background = "#"
								+ game.players[c].cards[k - 1].color;
							break;
						case rowColorReceive:
							cell[k].setAttribute('id',
									'ProporseReceiveCellRowChips'
											+ (i + (c * 4))
											+ 'ProposeReceiveColumnChips' + k);
							var cont = document.createElement("div");
							cont.setAttribute('id',
									'ProporseReceiveColorCellRowChips'
											+ (i + (c * 4))
											+ 'ProposeReceiveColorColumnChips'
											+ k);
							cont.innerHTML = "&nbsp;";
							cont.style.background = "#"
									+ game.players[c].cards[k - 1].color;
							break;
						}
					
					// append to father element
					cell[k].appendChild(cont);
					row[i].appendChild(cell[k]);
				}
				tbo.appendChild(row[i]);
				tab.appendChild(tbo);				
				// document.getElementById('ProposalsArea').appendChild(tab);
				document.getElementById(i == rowSelectReceive ? 'divTableReceive' : 'divTableSend').appendChild(tab);
			}
		}
	}
	

	// add a button for submit
	var cont = document.createElement("div");
	cont.innerHTML = "<div><button id='buttonSubmitProposal' onclick='buttonSubmitProposal_click();'>Submit Proposal</button></div>";

	
	document.getElementById('divButtonPropose').appendChild(cont);
	document.getElementById("divTableSender").innerHTML = "<img src='img/me.gif'/>";

	
	// populate data
	InsertIntoPlayersIconsSelect("divTableReceiver");
	InsertIntoSendSelect();
	InsertIntoReceiveSelect();
	

	$("#"+"0").show();
}
// END send Receive proposal area

// send proposal button
function buttonSubmitProposal_click() {
	
	for ( var i = 0; i < playerNumTotal; i++) {
		if (game.players[i].isme == 'true') {
			var ddIcons = document.getElementById('divTableReceiverDropDown');	
			sendProposal(game.getPlayerId(i), ddIcons.options[ddIcons.selectedIndex].value);
		}
	}
	//clearMessagesUI();
	//$("#"+"0").hide();	
	
}
//this function send proposal
function sendProposal(playerIDSend, playerIDReceive) {

	var SentChips = new Array();
	var sendRequest = new Array();
	var ReceivedChips = new Array();

	jQuery("select[id^=SelectSendPlayerId" + playerIDSend + "]").each(
			function() {
				idO = this.id;
				var arr = idO.split("Color");
				var color = arr[1];
				var ddlist = document.getElementById(this.id);
				var sum = ddlist.options[ddlist.selectedIndex].value;
	
				var json = "{ \"sum\" : " + sum + ", \"color\" : \"" + color 
					+ "\" ,\"playConId\" : \"" + playerIDSend
					+ "\", \"req\" :  \"Send\" }";
				
				var o = jQuery.parseJSON(json);
				// put json into sendRequest
				
				SentChips.push(o);
				sendRequest.push(o);

			});
	
	jQuery("select[id^=SelectReceivePlayerId" + playerIDSend + "]").each(
			function() {
				idO = this.id;
				var arr = idO.split("Color");
				var color = arr[1];
				var ddlist = document.getElementById(this.id);
				var sum = ddlist.options[ddlist.selectedIndex].value;
				// prepare json
				var json = "{ \"sum\" : " + sum + ", \"color\" : \"" + color
						+ "\", \"playConId\" : \"" + playerIDSend
						+ "\", \"req\" : \"receive\" }";
				var o = jQuery.parseJSON(json);
				// put json into sendRequest
												
				sendRequest.push(o);
				ReceivedChips.push(o);

			});


	var stringJ = "{\"player\" : \"" + playerIDSend + "\" , \"recipient\" : \"" + playerIDReceive + "\" , \"chips\" : [";
	for ( var i = 0; i < sendRequest.length; i++) {
		stringJ = stringJ + "{ \"sum\" : " + sendRequest[i].sum
				+ ", \"color\" : \"" + sendRequest[i].color
				+ "\", \"req\" : \"" + sendRequest[i].req + "\" }";
		// alert(sendRequest[i].req);
		if (i != sendRequest.length - 1) {
			stringJ = stringJ + ",";
		}
	}
	stringJ = stringJ + "]}";

	jQuery.ajax({
		type : "post",
		url : "sendmessage.jsp",
		data : "number=" + stringJ,
		success : function(msg) {
			// alert(msg);
		}
	});

		
	if(typeof console === "undefined") {
	    console = { log: function() { } };
	}
	else
	{
		console.log(game);
	}
	
	
	// rowID = num of rows in proposals grid
	var rowID = jQuery("#tblProposals").jqGrid('getGridParam', 'records');
	addRecordToTable("Proposal", playerIDSend, playerIDReceive, rowID, ReceivedChips, SentChips);
	
}
// end send proposal

// send response to proposal
// 1 accept
// 0 reject
function sendResponseAcceptReject(msgID, accept)
{
	
	//alert("sendResponseAcceptReject msgID : " +  msgID);
	
	var stringJ = "{msgID:\"" + msgID + "\" ,accept:\"" + accept + "\"}";
	// alert(stringJ);
	jQuery.ajax({
		type : "post",
		url : "responseMessage.jsp",
		data : "response=" + stringJ,
		success : function(msg) {
			// alert(msg);
		}
	});

	if (accept == "0")
		document.getElementById("msgToAcceptReject" + msgID).innerHTML = "Reject";
	if (accept == "1")
		document.getElementById("msgToAcceptReject" + msgID).innerHTML = "Accept";
	document.getElementById("msgToAcceptReject" + msgID).parentNode.parentNode.hidden = true;
	//the player responded to the proposal
	pendingProposalMsgID = -1;
	
}
// end send response to proposal


// send login information to server
function login(pin) {	
	// alert(stringJ);
	jQuery.post('login.jsp?pin=' + pin, function(data) {
		 //alert(jQuery.trim(data));
		if (jQuery.trim(data) == "true") {
			window.location = 'game.jsp';
		} 
//		else if (jQuery.trim(data) == "ended") {
			
//		}
	});
}
// END send login information to server


function InsertIntoPlayersIconsSelect(select) {
	
	
	var playerId;	
	var playersIconsDropDown = document.createElement('select');
	
	playersIconsDropDown.setAttribute('id', select+'DropDown');
	playersIconsDropDown.onchange= function(){InsertIntoReceiveSelect();}; 
	playersIconsDropDown.style.width = '70px';
	document.getElementById(select).appendChild(playersIconsDropDown);
	removeAllOption(select+'DropDown');
	for ( var i = 0; i < playerNumTotal; i++) {
		// get player ID
		playerId = game.getPlayerId(i);
		// populate data only for all players except me
		//if (game.getisPlayerMe(playerId) != "true") {
				// clear dropdown before inserting
				//removeAllOption("playersIconsDropDown");
				// insert player img to dropdown
				appendOptionLast(playerId, playerId, select+'DropDown', 'img/' + game.getPlayerIcon(playerId));		
		//}
	}
	
	document.getElementById(select).appendChild(playersIconsDropDown);
	$("#"+select+"DropDown").msDropDown();
	
}
function InsertIntoNormGoalsSelect() {
	var playerId;	
	var goalsDropDown = document.createElement('select');
	
	goalsDropDown.setAttribute('id', 'goalsDropDown');
	goalsDropDown.style.width = '70px';

	document.getElementById('divTableNGoal').appendChild(goalsDropDown);
	var x = game.goals[0].posX;
	var y = game.goals[0].posY;

	appendOptionLast("["+x+","+(y+1)+"]",x+","+(y+1),"goalsDropDown", "");
	appendOptionLast("["+x+","+(y-1)+"]", x+","+(y-1), "goalsDropDown", "");
	appendOptionLast("["+(x+1)+","+y+"]", (x+1)+","+y, "goalsDropDown", "");
	appendOptionLast("["+(x-1)+","+y+"]", (x-1)+","+y, "goalsDropDown", "");
	
	//$("#goalsDropDown").msDropDown();
	
}

function InsertIntoNormColorSelect() {
	var playerId;	
	var colorsDropDown = document.createElement('select');
	
	colorsDropDown.setAttribute('id', 'colorsDropDown');
	colorsDropDown.style.width = '70px';

	document.getElementById('divTableNCColor').appendChild(colorsDropDown);
	for ( var j = 0; j < colorNumTotal; j++) {
		var color = game.colors[j].color;
		var name = game.colors[j].name;
		appendOptionLast(color,name,"colorsDropDown", 'color');
	}
}

function InsertIntoNormSelect() {
	var playerId;	
	var normsDropDown = document.createElement('select');
	
	normsDropDown.setAttribute('id', 'normsDropDown');
	normsDropDown.style.width = '100px';

	document.getElementById('divTableNCNorm').appendChild(normsDropDown);
	appendOptionLast("obligation","yes","normsDropDown", 'obligation');
	appendOptionLast("prohibition","no","normsDropDown", 'prohibition');
	
}
function InsertIntoSanctionSelect(select) {
	var playerId;	
	var normsDropDown = document.createElement('select');
	
	normsDropDown.setAttribute('id', select+"DropDown");
	normsDropDown.style.width = '70px';

	document.getElementById(select).appendChild(normsDropDown);
	appendOptionLast(50,50,select+'DropDown', '');
	appendOptionLast(100,100,select+'DropDown', '');
	appendOptionLast(150,150,select+'DropDown', '');
	appendOptionLast(200,200,select+'DropDown', '');
	appendOptionLast(250,250,select+'DropDown', '');
	appendOptionLast(300,300,select+'DropDown', '');
	appendOptionLast(400,400,select+'DropDown', '');
	appendOptionLast(500,500,select+'DropDown', '');
	
	//document.getElementById(select).appendChild(select+"DropDown");
}

// INSERT INTO Players Chips Send
function InsertIntoSendSelect() {
	var playerId;
	var color;
	var sumChips;
	
	for ( var i = 0; i < playerNumTotal; i++) {
		// populate data only for me (player)
		playerId = game.getPlayerId(i);
		if (game.getisPlayerMe(playerId) == "true") {
			for ( var j = 0; j < colorNumTotal; j++) {
				playerId = game.getPlayerId(i);
				color = game.colors[j].name;
				removeAllOption("SelectSendPlayerId" + playerId + "Color"
						+ color);
				sumChips = (game.getSumChips(playerId, color) < 1 ? 0 : 1);
				for ( var k = 0; k <= sumChips; k++) {
					appendOptionLast(k, k, "SelectSendPlayerId" + playerId
							+ "Color" + color, '');
				}
			}
		}
	}
}

// INSERT INTO Players Chips Receive
function InsertIntoReceiveSelect() {
	var playerId;
	var ddIcons = document.getElementById('divTableReceiverDropDown');	
	var playerIdOpponent = ddIcons.options[ddIcons.selectedIndex].value;
	var color;
	var sumChips = 0;

	for ( var i = 0; i < playerNumTotal; i++) {
		// populate data only for me (player)
		playerId = game.getPlayerId(i);
		if (game.getisPlayerMe(playerId) == "true") {
			for ( var j = 0; j < colorNumTotal; j++) {
				playerId = game.getPlayerId(i);
				color = game.colors[j].name;

				removeAllOption("SelectReceivePlayerId" + playerId + "Color"
						+ color);

				//sumChips += game.getSumChips(playerIdOpponent, color);
				sumChips = (game.getSumChips(playerIdOpponent, color) < 1 ? 0 : 1);
				for ( var k = 0; k <= sumChips; k++) {
					appendOptionLast(k, k, "SelectReceivePlayerId" + playerId
							+ "Color" + color, '');
				}
				sumChips = 0;
			}
		}
	}
}
// END INSERT INTO Players Chips Receive

// insert value at end of Select element
function appendOptionLast(text, value, IDselect, title) {
	var elOptNew = document.createElement('option');
	elOptNew.text = text;
	elOptNew.value = value;
	elOptNew.title = title == null ? "" : title;
	var elSel = document.getElementById(IDselect);

	try {
		elSel.add(elOptNew, null); // standards compliant; doesn't work in IE
	} catch (ex) {
		elSel.add(elOptNew); // IE only
	}

}


// remove all data from Select element
function removeAllOption(IDselect) {
	var elSel = document.getElementById(IDselect);
	var i;
	for (i = elSel.length - 1; i >= 0; i--) {

		elSel.remove(i);
	}
}

// Proposals table
function loadProposalsTable() {
	jQuery("#tblProposals").jqGrid(
			{
				datatype : "local",
				height : 200,
				colNames : ['MsgType', 'Sender', 'Receiver', 'Receive', 'Send', 'Response' ],
				colModel : [ {
					name : 'MsgType',
					index : 'MsgType',
					width : 90,
					sortable : false
				}, {
					name : 'Sender',
					index : 'Sender',
					width : 75,
					sortable : false
				},{
					name : 'Receiver',
					index : 'Receiver',
					width : 75,
					sortable : false
				},{
					name : 'Receive',
					index : 'Receive',
					width : 200,
					sortable : false
				}, {
					name : 'Send',
					index : 'Send',
					width : 200,
					sortable : false
				}, {
					name : 'Response',
					index : 'Response',
					width : 150,
					sortable : false
				} ],
				multiselect : false,
				hoverrows : false				
			});
	var defaultData = {
			// Id : MessageId,
			// Proposer : playerName,
			// Proposer : '<img height=25px width=25px src="img/me.gif"/>',
			// Receiver : '<img height=25px width=25px src="img/'
			// + game.getPlayerIcon(SenderID) + '"/>',
			// Proposer : SenderID,
			// Receiver : SenderID == 0 ? 1 : 0,
			MsgType : "<div id='divTableMsgType'></div>",
			Sender : "<div id='divTableSender'></div>",
			Receiver : "<div id='divTableReceiver'></div>",
			Receive : "<div id='divTableReceive'></div>", 
			Send : "<div id='divTableSend'></div>",
			Response : "<div id='divButtonPropose'></div>"
		};
	
	jQuery("#tblProposals").jqGrid('addRowData', 0, defaultData);
}
function loadCoordsTable() {
	jQuery("#tblCoords").jqGrid(
			{
				datatype : "local",
				height : 200,
				colNames : ['MsgType', 'Norm' ],
				colModel : [ {
					name : 'MsgType',
					index : 'MsgType',
					width : 90,
					sortable : false
				}, {
					name : 'Norm',
					index : 'Norm',
					width : 500,
					sortable : false
				} ],
				multiselect : false,
				hoverrows : false				
			});
	var defaultData = {
			// Id : MessageId,
			// Proposer : playerName,
			// Proposer : '<img height=25px width=25px src="img/me.gif"/>',
			// Receiver : '<img height=25px width=25px src="img/'
			// + game.getPlayerIcon(SenderID) + '"/>',
			// Proposer : SenderID,
			// Receiver : SenderID == 0 ? 1 : 0,
			MsgType : "<div id='divTableMsgTypeC'></div>",
			Norm : "<div id='divTableSenderC'></div>"
		};
	
	jQuery("#tblCoords").jqGrid('addRowData', 0, defaultData);
}

// this function Receive "01:03" return "63"
function getSeconds(timeForm) {

	var time3 = timeForm.split(":");
	// change second fro 09 to 9
	if (time3[1].charAt(0) == 0)
		time3[1] = time3[1].charAt(1);

	// change minutes from 07 to 7
	if (time3[0].charAt(0) == 0)
		time3[0] = time3[0].charAt(1);

	var seconds = parseInt(time3[0]) * 60 + parseInt(time3[1]);
	seconds = parseInt(seconds);
	return seconds;

}
// end getSeconds

// this function Receives "120" return "2:00"
function secondsToTime(seconds) {
	var min = 0;
	while (seconds >= 60) {
		seconds = parseInt(seconds) - 60;
		min = min + 1;
	}
	if (parseInt(min) < 10)
		min = "0" + min;
	if (parseInt(seconds) < 10)
		seconds = "0" + seconds;

	return min + ":" + seconds;

}

/* Add a msg to grid */
function addNormToTable(MsgType, SenderID, ReceiverID, msgID, N) {
	var Norm = "<div><table><tr>";
	Norm = Norm + N + "</tr></table></div>";
	
	var defaultData = {
		MsgType : MsgType,
		Sender : game.getisPlayerMe(SenderID) == "true" ? "<img src='img/me.gif'/>" : "<img src='img/" + game.getPlayerIcon(SenderID) + "'/>",
		Receiver : game.getisPlayerMe(SenderID) == "false" ? "<img src='img/me.gif'/>" : "<img src='img/" + game.getPlayerIcon(ReceiverID) +"'/>   "+ReceiverID+"",	
		Goal : "<div></div>",
		Message : Norm, 
		Response : "<div></div>"
	};

	//alert(defaultData.Message);
	var rowID = jQuery("#tblNorms").jqGrid('getGridParam', 'records');
	jQuery("#tblNorms").jqGrid('addRowData', rowID, defaultData);
	
}

function addNormToCoord(MsgType, Norm) {
	
	var defaultData = {
		MsgType : MsgType,
		Message : Norm
	};

	var rowID = jQuery("#tblCoords").jqGrid('getGridParam', 'records');
	jQuery("#tblCoords").jqGrid('addRowData', rowID, defaultData);
	
}

function addRecordToTable(MsgType, SenderID, ReceiverID, msgID, ReceivedChips, SentChips) {
	var ReceivedChips1 = "<div><table><tr>";
	var SentChips1 = "<div><table><tr>";
	var playerName = game.getPlayerName(SenderID);
	// iterate over msg.ReceivedColor
	
	
	if(MsgType != "GoalRevelation")
	{
		for ( var j = 0; j < SentChips.length; j++) {
			SentChips1 = SentChips1 + '<td class="proposalsSendReceiveCol" style="background-color :#'
					+ SentChips[j].color + ';"><b>'+" " + SentChips[j].sum + " "+'</b></td>';
		}
		SentChips1 = SentChips1 + "</tr></table></div>";

	}
	
		
	// only if msg type is Proposal iterate over recieved chips
	if (MsgType == "Proposal") {
		for ( var j = 0; j < ReceivedChips.length; j++) {
			ReceivedChips1 = ReceivedChips1 + '<td class="proposalsSendReceiveCol" style="background-color :#'
					+ ReceivedChips[j].color + ';"><b>'+" " + ReceivedChips[j].sum + " "+'</b></td>';
		}
		ReceivedChips1 = ReceivedChips1 + "</tr></table></div>";
	}

	var AcceptRejectButtonsHTML = '<div id="msgToAcceptReject'
			+ msgID
			+ '"><table><tr><td><input onclick="sendResponseAcceptReject('
			+ msgID
			+ ',1)" type="button" id="acceptResponseButton'
			+ msgID
			+ '" value="Accept"></td><td><input type="button" id="rejectResponseButton'
			+ msgID + '" value="Reject" onclick="sendResponseAcceptReject('
			+ msgID + ',0)"></td><td></td></tr></table></div>';

	var showButtons = game.getisPlayerMe(ReceiverID) == "true" ? AcceptRejectButtonsHTML
			: '<div id="msgToAcceptReject' + msgID + '"></div>';

	var defaultData = {
		MsgType : MsgType,
		Sender : game.getisPlayerMe(SenderID) == "true" ? "<img src='img/me.gif'/>" : "<img src='img/" + game.getPlayerIcon(SenderID) + "'/><div>"+SenderID+"</div>",
		Receiver : game.getisPlayerMe(ReceiverID) == "true" ? "<img src='img/me.gif'/>" : "<img src='img/" + game.getPlayerIcon(ReceiverID) + "'/><div>"+ReceiverID+"</div>",	
		Send : MsgType != "GoalRevelation" ? SentChips1  : "<div></div>",
		Receive : MsgType == "Proposal" ? ReceivedChips1 : "<div></div>", 
		Response : MsgType == "Proposal" ? showButtons : "<div></div>"
	};

	// rowID = num of rows in proposals grid
	var rowID = jQuery("#tblProposals").jqGrid('getGridParam', 'records');
	
		
	// alert('send: ' + SentChips1);
	// alert('Receive: ' + ReceivedChips1);
	
	
	jQuery("#tblProposals").jqGrid('addRowData', rowID, defaultData);
	
	//the popup window for received proposal
	if( game.getisPlayerMe(ReceiverID) == "truee" ) //FIXED false
	{
		var newDiv = $(document.createElement('div')); 
		newDiv.html('Incoming proposal received!');
		newDiv.dialog({ title: "Proposal Received" });
		setTimeout(function(){newDiv.dialog('close');},2500);
	}
	
}


// send data to server and update game window with changes
function UpdateServer() {
	
	
	jQuery.post('getupdate.jsp', function(data) {
		var o = jQuery.parseJSON(data);		
		
		// Update game isEnded
		game.setIsEnded(o.isEnded);
		// Update player role
		game.setRole(o.role);
		
		//gil
		// Update goal revelation
		game.setIsGoalRevelationAllowed(o.isGoalRevelationAllowed);
		//gilend
		
		//update current phase
		if (currentPhase != o.CurrentPhase) {
			phaseChanged = true;

			// Update Current phase name
			currentPhase = o.CurrentPhase;
			
			
		}
		if(game.numOfGolals!= o.numOfGoals)
		{
			updateGoals(o);
		}
		InsertIntoNormsTable(o.Prohibitions,o.Obligations);
		InsertIntoProposalsTable(o);
		UpdateResponseForProposal(o);
		UpdatePlayerPosOnBoard(o);
		UpdatePlayerChips(o);
		UpdatePlayerPoints(o);
		UpdateBorderColors(o);
		UpdateClock(o.Clock);
		UpdateScore(o.Score);
	});
}

function UpdateClock(c) {
	if (c <= clock)
		return;
	clock = c;
	updateProgressBar();
}
function UpdateScore(s) {
	score = s;
}

function UpdatePlayerPoints(o) {
	for ( var i = 0; i < o.Points.length; i++) {		
		jQuery("#" + "playerId" + o.Points[i].id + "Points").html(o.Points[i].points);											
	}
}
//Change Player Chips after ajax update
function UpdatePlayerChips(o) {
	for ( var i = 0; i < o.chipsChange.length; i++) {
		for ( var j = 0; j < o.chipsChange[i].chips.length; j++) {
			var playerID = o.chipsChange[i].playerID;
			var colorCell = o.chipsChange[i].chips[j].color;				
			var total = o.chipsChange[i].chips[j].sum;
			
			game.setSumChips(playerID, colorCell, total);
			
			if (game.getisPlayerMe(playerID) == "true" || isRevelationEnabled == "false") {
				jQuery("#" + "playerId" + playerID + "Color" + colorCell).html(
						total);								
			}
		}
	}
}
// END change player Chips

function updateGoals(o)
{
		
	removeGoals();
	
	for ( var i = 0; i < o.Goals.length; i++) 
	{
//		alert(JSON.stringify(o.Goals));
		game.goals[i] = o.Goals[i];
		game.goals[i].id = o.Goals[i].id;
		game.goals[i].type = o.Goals[i].type;
		game.goals[i].posX = o.Goals[i].posX;
		game.goals[i].posY = o.Goals[i].posY;
	}

	
	game.numOfGolals = o.Goals.length;
	game.numGoals = o.Goals.length;
	
	createGoals();
	
	//updatePathFinderGoals();
	
	
	
	
}


function removeGoals(){

    var goalSquare;
    
    for ( var i = 0; i < game.numOfGolals; i++) {
    //   if(game.goals[i].type >= 10){
           goalSquare = document.getElementById("DivRow" + game.goals[i].posX + "Column" + game.goals[i].posY);
           
           goalSquare.removeChild(goalSquare.lastChild);
            
  //     }
    }
    
    //alert("Finished removing goals");
}





// Change BorderColors (game pallet) after ajax update
function UpdateBorderColors(o) {
	
	for ( var i = 0; i < o.BorderColors.length; i++) {		
			var color = o.BorderColors[i].color;
			var posX = o.BorderColors[i].posX;				
			var posY = o.BorderColors[i].posY;
			
			game.setColorCell(color, posX, posY);								
	}
	
	// rebuild game bord colors
	addColors();
}
// END Change BorderColors (game pallet) after ajax update

// Change Player POS
function UpdatePlayerPosOnBoard(o) {
	for ( var i = 0; i < o.moveChange.length; i++) {
		var playerID = o.moveChange[i].playerID;
		var xpos = o.moveChange[i].position.y;
		var ypos = o.moveChange[i].position.x;
		// alert(playerID);
		// alert(xpos);
		// alert(ypos);
		MoveElementPosition(playerID, xpos, ypos);
	}
}
// END change player pos
//add data to proposals table
function InsertIntoNormsTable(prohibitions,obligations) {

	if (prohibitions != null)
		for ( var i = 0; i < prohibitions.length; i++) {
			 //alert("msg: " + prohibitions[i]);
			//var s = JSON.parse(prohibitions[i]);
			var newDiv = document.createElement('div');
			newDiv.id = 'pro'+i;
			//iDiv.className = 'block';
			newDiv.innerHTML = prohibitions[i].msg;
			document.getElementById("notifyContainer").insertBefore(newDiv, document.getElementById("notifyContainer").firstChild);
			//alert("msg: "+JSON.stringify(prohibitions[i]));
		}
	if (obligations != null)
		for ( var i = 0; i < obligations.length; i++) {
			 //alert("msg: " + JSON.stringify(obligations[i]));
			var newDiv = document.createElement('div');
			newDiv.id = 'obl'+i;
			//iDiv.className = 'block';
			newDiv.innerHTML = obligations[i].msg;
			//newDiv.innerHTML = obligations[i];
			document.getElementById("notifyContainer").insertBefore(newDiv, document.getElementById("notifyContainer").firstChild);
		}
}
// add data to proposals table
function InsertIntoProposalsTable(o) {

	for ( var i = 0; i < o.msgs.length; i++) {
		 //alert("msg to: " + o.msgs[i].ReceiverID);
		addRecordToTable("Proposal", o.msgs[i].SenderID, o.msgs[i].ReceiverID, o.msgs[i].MessageId,
				o.msgs[i].SentChips, o.msgs[i].ReceivedChips);
		
		pendingProposalMsgID =  o.msgs[i].MessageId;
	}
}
// END add data to proposals table in Receiver

// update response for proposal at proposer side
function UpdateResponseForProposal(o) {
	// rowID = num of rows in proposals grid
	
		
	var rowID = jQuery("#tblProposals").jqGrid('getGridParam', 'records');
	
	for ( var i = 0; i < o.response.length; i++) {
		// jQuery("#tblProposals").jqGrid('setCell', rowID - 1, 4,
		// o.response[i].response);
		document.getElementById("msgToAcceptReject" + (rowID - 1)).innerHTML = o.response[i].response;
	}
}
// END update response for proposal at proposer side

// move player pos on the game board
function MoveElementPosition(playerId, toX, toY) {
	var cellColor = game.getColorCell(toX, toY);
	// substract num chips
	var numChips = game.getSumChips(playerId, cellColor);
	game.setSumChips(playerId, cellColor, numChips - 1);
	// update div numOf chips
	jQuery("#" + "playerId" + playerId + "Color" + cellColor)
			.html(numChips - 1);
	// move
	var newDiv = document.getElementById("player" + playerId);
	var cell = document.getElementById("DivRow" + toX + "Column" + toY);
	cell.appendChild(newDiv);
};
// end move player pos on the game board




